package com.coretempparcer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.sql.Timestamp;
import java.time.Month;
import java.util.*;
import java.util.regex.Pattern;

public class FileParcer {

    public static FileData parceFile(String fileName) throws IOException {

        FileData fd = fileParsing(fileName);

        return fd;
    }

    public static FileData fileParsing(String file) throws IOException {

        FileData dataOfFile = new FileData(file);

        String[] spltStr;
        String[] addingStr;
        int i = 0;

        String firstCol;
        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);

        for (CSVRecord record : records) {
            if (record.values().length < 1) continue;

            firstCol = record.get(0);
            if (firstCol.isEmpty()) continue;

            switch (firstCol) {
                case ("Time"): {
                    int a = 0;
                    for (String s : record.values()) {
                        s = prepareColName(s);
                        if (dataOfFile.addColumn(a, s)) a += 1;
                    }
                    prepareColumns(dataOfFile);
                    break;
                }
                case ("CPUID:"):
                    dataOfFile.setCPUID(firstCol);
                    break;
                case ("Processor:"): {
                    dataOfFile.setProcessor(firstCol);
                    break;
                }
                case ("Platform:"): {
                    dataOfFile.setPlatform(firstCol);
                    break;
                }
                case ("Revision:"): {
                    dataOfFile.setRevision(firstCol);
                    break;
                }
                case ("Lithography:"): {
                    dataOfFile.setLithography(firstCol);
                    break;
                }
                case ("Session start:"): {
                    Date seStart = parseSessionStart(record.get(1));
                    dataOfFile.setSessionStart(seStart);
                    break;
                }
                case ("Session end"): {
                    break;
                }
                default: {
                    if (record.values().length < 2) {
                        break;
                    }
                    spltStr = prepareString(record.values());
                    addingStr = deleteExcessColumnsFromString(dataOfFile, spltStr);
                    dataOfFile.addString(i, addingStr);
                    i += 1;
                }
            }


        }

        dataOfFile.setStringcount(i);

        deleteExcessColumnsFromFileData(dataOfFile);

        return dataOfFile;
    }

    private static void deleteExcessColumnsFromFileData(FileData dataOfFile) {
        HashMap<Integer, String> columns = dataOfFile.getColumns();

        Iterator<Map.Entry<Integer, String>> iterator = columns.entrySet().iterator();

        HashMap<Integer, String> newCol = new HashMap<>();

        int ch = 0;

        while (iterator.hasNext()) {
            Map.Entry<Integer, String> pair = iterator.next();
            String value = pair.getValue();
            if (MainClass.validateColumn(value)) {
                newCol.put(ch, value);
                ch += 1;
            }
        }

        dataOfFile.setColumns(newCol);
        dataOfFile.setColumnCount(newCol.size());
    }

    private static String[] deleteExcessColumnsFromString(FileData fd, String[] spltStr) {
        ArrayList<String> result = new ArrayList<>();
        HashMap<Integer, String> columns = fd.getColumns();

        for (int i = 0; i < fd.getColumnCount(); i++) {
            if (MainClass.validateColumn(columns.get(i))) {
                result.add(spltStr[i]);
            }
        }

        spltStr = result.toArray(new String[0]);
        return spltStr;
    }

    private static void prepareColumns(FileData fileData) {
        int coreCount = 0;
        String currentCol = "";
        String currentCore = "";
        HashMap<Integer, String> newCols = new HashMap<>();
        HashMap<Integer, String> columns = fileData.getColumns();

        for (int i = 0; i < columns.size(); i++) {
            if (!columns.get(i).equals("")) {
                coreCount += 1;
            } else break;
        }

        int a = 0;

        for (int i = 0; i < columns.size(); i++) {
            currentCol = columns.get(i);

/*Time,Core0Temp,Core1Temp,Core2Temp,Core3Temp,Core4Temp,Core5Temp,,
Core0,Lowtemp,Hightemp,Coreload,CorespeedMHz,
Core1,Lowtemp,Hightemp,Coreload,CorespeedMHz,
Core2,Lowtemp,Hightemp,Coreload,CorespeedMHz,
Core3,Lowtemp,Hightemp,Coreload,CorespeedMHz,
Core4,Lowtemp,Hightemp,Coreload,CorespeedMHz,
Core5,Lowtemp,Hightemp,Coreload,CorespeedMHz,
CPU0Power
 */
            if (currentCol.equals("")) continue;

            if (currentCol.equals("Time")) {

                newCols.put(a, currentCol);
                a += 1;
            }

            if ((currentCol.length() > 5) && Pattern.matches("[0-9]", currentCol.substring(4, 5)) && (currentCol.length() >= 8) && (currentCol.substring(5, 9).equals("Temp"))) {
                newCols.put(a, currentCol);
                a += 1;
            }

            if ((currentCol.length() == 5) && (currentCol.substring(0, 4).equals("Core")) && Pattern.matches("[0-9]", currentCol.substring(4, 5))) {
                currentCore = currentCol;
            }

            if (currentCol.equals("Lowtemp")) {
                newCols.put(a, currentCore + currentCol);
                a += 1;
            }

            if (currentCol.equals("Hightemp")) {
                newCols.put(a, currentCore + currentCol);
                a += 1;
            }

            if (currentCol.equals("Coreload")) {
                newCols.put(a, currentCore + currentCol.substring(4, 8));
                a += 1;
            }

            if (currentCol.equals("CorespeedMHz")) {
                newCols.put(a, currentCore + currentCol.substring(4, 12));
                a += 1;
            }

            //CPU0Power
            if (currentCol.substring(0, 3).equals("CPU") && (currentCol.length() >= 8) && currentCol.substring(4, 9).equals("Power")) {
                newCols.put(a, currentCol);
                a += 1;
            }

        }

        fileData.setColumnCount(a);
        fileData.setColumns(newCols);
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

    public static String[] prepareString(String[] spltStr) {
        String[] res = new String[spltStr.length];
        String str;

        int a = 0;

        for (int i = 0; i < spltStr.length; i++) {
            str = spltStr[i];
            if (str.equals("")) continue;

            res[a] = str;
            a += 1;
        }

        String[] strArray = new String[a];

        for (int i = 0; i < a; i++) {
            strArray[i] = res[i];
        }

        return strArray;
    }

    public static Date parseSessionStart(String sessionStart) {
        String[] spltDate = sessionStart.split(" ");
        String[] spltTime = spltDate[0].split(":");
        Integer hour = Integer.parseInt(spltTime[0]);
        Integer minute = Integer.parseInt(spltTime[1]);
        Integer second = Integer.parseInt(spltTime[2]);
        Integer month = Month.valueOf(spltDate[2].toUpperCase(Locale.ROOT)).getValue();
        Integer date = Integer.parseInt(spltDate[3]);
        Integer year = Integer.parseInt(spltDate[5]);
        GregorianCalendar calendar = new GregorianCalendar(); ///.set(year, month, date, hour, minute, second);
        calendar.set(year, month - 1, date, hour, minute, second);
        Date res = calendar.getTime();
//        int mo = Month.valueOf()
        return res;
    }

}
