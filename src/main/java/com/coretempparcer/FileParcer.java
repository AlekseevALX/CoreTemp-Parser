package com.coretempparcer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class FileParcer {

    public static FileData parceFile(String fileName, long[] lastFile) throws IOException {
        FileData fd;

        fd = fileParsing_withRandomAccess(fileName, lastFile);

        return fd;
    }

    public static FileData fileParsing_withCSVRecord(String file) {

        FileData dataOfFile = new FileData(file);

        int[] columns = MainClass.getColumns();
        String[] dataString;

        int startStringNumber = MainClass.getFirstStringOfData();
        int initCh = 1;
        int ch = 0;

        HashMap<Integer, String[]> readedRecords = new HashMap<>();
        try (Reader in = new FileReader(file)) {
            Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);

            for (CSVRecord record : records) {
                dataString = new String[columns.length];

                if (initCh < startStringNumber) {
                    initCh++;
                    continue;
                }

                if (record.values().length < dataString.length) continue;

                for (int i = 0; i < dataString.length; i++) {
                    dataString[i] = record.get(columns[i]);
                }

                readedRecords.put(ch, dataString);

                ch++;

            }
        } catch (IOException e) {
            e.printStackTrace();
            return dataOfFile;
        }

        dataOfFile.setStrings(readedRecords);
        return dataOfFile;
    }

    public static FileData fileParsing_withBufferedReader(String file, long currfTime) {

        FileData dataOfFile = new FileData(file);

        int[] columns = MainClass.getColumns();
        String[] dataString;

        int startStringNumber = MainClass.getFirstStringOfData();
        int initCh = 1;
        int ch = 0;

        HashMap<Integer, String[]> readedRecords = new HashMap<>();
        try {
            byte[] buf = Files.readAllBytes(Paths.get(file));
            String readedFile = new String(buf);
            String[] spltStr_file = readedFile.split(System.lineSeparator());
            String[] str;
            for (String record : spltStr_file) {
                str = record.split(",");
                dataString = new String[columns.length];
                if (initCh < startStringNumber) {
                    initCh++;
                    continue;
                }
                if (str.length < dataString.length) continue;
                for (int i = 0; i < dataString.length; i++) {
                    dataString[i] = str[columns[i]];
                }
                readedRecords.put(ch, dataString);
                ch++;
            }
            MainClass.setLastFile(currfTime, (long) buf.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        dataOfFile.setStrings(readedRecords);
        return dataOfFile;
    }

    public static FileData fileParsing_withRandomAccess(String file, long[] lastFile) {

        long currfTime = ParcingSession_thread.parseFileNameToDate(file);

        FileData dataOfFile = new FileData(file);

        int[] columns = MainClass.getColumns();
        String colf_time = MainClass.getColf_time();
        String[] dataString;

        int startStringNumber = MainClass.getFirstStringOfData();
        int initCh = 1;
        int ch = 0;
        int len;

        HashMap<Integer, String[]> readedRecords = new HashMap<>();

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {

            byte[] buf;

            if (lastFile[0] != currfTime) {
                len = (int) raf.length();
                buf = new byte[len];
                raf.read(buf);
                MainClass.setLastFile(currfTime, raf.getFilePointer());
            } else {
                raf.seek(lastFile[1]);
                len = (int) (raf.length() - (int) lastFile[1]);
                buf = new byte[len];
                raf.read(buf);
                MainClass.setLastFile(currfTime, raf.getFilePointer());
                dataOfFile.setNeedsToFindExistingRecords(false);
            }

            String readedFile = new String(buf);
            String[] spltStr_file = readedFile.split(System.lineSeparator());
            String[] str;

            for (String record : spltStr_file) {
                str = record.split(",");
                dataString = new String[columns.length];
                if (initCh < startStringNumber && lastFile[0] != currfTime) {
                    initCh++;
                    continue;
                }
                if (str.length < dataString.length || str[0].equals(colf_time)) continue;
                for (int i = 0; i < dataString.length; i++) {
                    dataString[i] = str[columns[i]];
                }
                readedRecords.put(ch, dataString);
                ch++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return dataOfFile;
        }


        dataOfFile.setStrings(readedRecords);
        return dataOfFile;
    }

    public static synchronized void increaseElapsedTime(Long time) {
        MainClass.increaseElapsedTimeCounter(time);
    }

    public static void readColumnSettingsFromFile(String file) throws IOException {
        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
        String firstCol;

        String f_colTime = MainClass.getColf_time();

        for (CSVRecord record : records) {
            firstCol = record.get(0);
            if (firstCol.isEmpty()) continue;

            if (firstCol.toUpperCase().equals(f_colTime.toUpperCase())) {
                MainClass.setFirstStringOfData((int) record.getRecordNumber() + 1);
                toFindNeededColumns(record);
                break;
            }

        }
        in.close();
    }

    private static void toFindNeededColumns(CSVRecord record) {
        String currCol;
        String[] tempString = new String[record.size()];
        String compName = MainClass.getComputerName();


        String db_colTime = MainClass.getColdb_time();
        String db_colCore = MainClass.getColdb_core();
        String db_colTemp = MainClass.getColdb_temp();
        String db_colLoad = MainClass.getColdb_load();
        String db_colSpeed = MainClass.getColdb_speed();
        String db_colCPUPower = MainClass.getColdb_cpupower();

        String f_core = MainClass.getColf_core();
        String f_colTime = MainClass.getColf_time();
        String f_colTemp = MainClass.getColf_temp();
        String f_colLoad = MainClass.getColf_load();
        String f_colSpeed = MainClass.getColf_speed();
        String f_colCPUPower = MainClass.getColf_cpupower();

        int[] columns;
        String[] colNames;
        int colCount = 0; //count of columns which really must be readed in the database
        int currCorenumber = -1;
        int currCore = -1;

        //prepare col names
        for (int i = 0; i < record.values().length; i++) {
            currCol = record.get(i);
            if (currCol.isEmpty()) continue;

            currCol = prepareColName(currCol);
            tempString[i] = currCol;
        }

        //delete columns which not needed

        for (int i = 0; i < tempString.length; i++) {
            currCol = tempString[i];

            if (currCol == null || currCol.isEmpty()) {
                continue;
            }

            //col Time
            if (currCol.toUpperCase().equals(f_colTime.toUpperCase())) {
                tempString[i] = db_colTime;
                colCount++;
                continue;
            }

            //col Core ? Temp.
            if (currCol.toUpperCase().startsWith(f_core.toUpperCase()) && currCol.toUpperCase().endsWith(f_colTemp.toUpperCase())) {
                String symbol = currCol.substring(f_core.length(), f_core.length() + 1);
                if (Pattern.matches("[0-9]", symbol)) {
                    currCorenumber = Integer.parseInt(symbol);
                }
                if (currCorenumber == 0) {
                    tempString[i] = db_colCore + currCorenumber + db_colTemp;
                    colCount++;
                    continue;
                } else {
                    tempString[i] = "";
                }
                continue;
            }

            //col Core ?
            if (currCol.toUpperCase().startsWith(f_core.toUpperCase()) &&
                    !(currCol.toUpperCase().endsWith(f_colTemp.toUpperCase())) &&
                    !(currCol.toUpperCase().endsWith(f_colSpeed.toUpperCase())) &&
                    !(currCol.toUpperCase().endsWith(f_colLoad.toUpperCase()))) {
                String coreN = currCol.substring(f_core.length(), currCol.length());
                if (Pattern.matches("[0-9]", coreN)) {
                    currCore = Integer.parseInt(coreN);
                }
                tempString[i] = "";
                continue;
            }

            //col Low temp
            if (currCol.toUpperCase().startsWith("LOW") && currCol.toUpperCase().endsWith(f_colTemp.toUpperCase())) {
                tempString[i] = "";
                continue;
            }

            //col High temp
            if (currCol.toUpperCase().startsWith("HIGH") && currCol.toUpperCase().endsWith(f_colTemp.toUpperCase())) {
                tempString[i] = "";
                continue;
            }

            //col Core load
            if (currCol.toUpperCase().startsWith(f_core.toUpperCase()) && currCol.toUpperCase().endsWith(f_colLoad.toUpperCase())) {
                tempString[i] = db_colCore + currCore + db_colLoad;
                colCount++;
                continue;
            }

            //col Core speed
            if (currCol.toUpperCase().startsWith(f_core.toUpperCase()) && currCol.toUpperCase().endsWith(f_colSpeed.toUpperCase())) {
                tempString[i] = db_colCore + currCore + db_colSpeed;
                colCount++;
                continue;
            }

            //col CPU 0 Power
            if (currCol.toUpperCase().endsWith(f_colCPUPower.toUpperCase())) {
                tempString[i] = db_colCPUPower;
                colCount++;
                continue;
            }
        }

        int ch = 0;
        colNames = new String[colCount];
        columns = new int[colCount];

        for (int i = 0; i < tempString.length; i++) {
            if (tempString[i] == null || tempString[i].isEmpty()) continue;

            colNames[ch] = tempString[i];
            columns[ch] = i;
            ch++;
        }

        MainClass.setColNames(compName, colNames);
        MainClass.setColumns(columns);

    }

    public static String prepareColName(String s) {
        //        Pattern.matches("[a-z]||[A-Z]||[а-я]||[А-Я]||[ё,Ё]||[\\.\\,]", st)
        String res = "";
        String[] spltStr = s.split("");

        for (String a : spltStr) {
            if (Pattern.matches("[a-z]||[A-Z]||[0-9]", a)) {
                res = res.concat(a);
            }
        }

        return res;
    }


}
