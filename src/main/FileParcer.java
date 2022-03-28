package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Month;
import java.util.*;
import java.util.regex.Pattern;

public class FileParcer {

    public static FileData parceFile(String fileName) throws IOException {

        String file;

        file = fileName;

        FileData fd = fileParsing(file);

        return fd;
    }

    public static FileData fileParsing(String file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

        FileData dataOfFile = new FileData(file);

        String oneString = "";
        String[] spltStrCol;
        int step = 1; //step 1 - read title, 2 - read names of column, 3 - read strings
        String[] spltStr;
        int i = 0;

        while (bufferedReader.ready()) {

            oneString = bufferedReader.readLine();

            if (oneString.equals("")) continue;

            if (step == 3) {
                if (oneString.substring(0, 11).equals("Session end")) {
                    continue;
                }
                spltStr = oneString.split(",");
                spltStr = prepareString(spltStr);
                dataOfFile.addString(i, spltStr);
                i += 1;
            }

            if (step == 2) {
                spltStrCol = oneString.split(",");
                int a = 0;
                for (String s : spltStrCol) {
                    s = prepareColName(s);
                    dataOfFile.addColumn(a, s);
                    a += 1;
                }
                prepareColumns(dataOfFile);
                step = 3;
            }

            if (step == 1) {
                spltStr = oneString.split(",");

                if (spltStr[0].equals("CPUID:")) {
                    dataOfFile.setCPUID(spltStr[1]);
                }

                if (spltStr[0].equals("Processor:")) {
                    dataOfFile.setProcessor(spltStr[1]);
                }

                if (spltStr[0].equals("Platform:")) {
                    dataOfFile.setPlatform(spltStr[1]);
                }

                if (spltStr[0].equals("Revision:")) {
                    dataOfFile.setRevision(spltStr[1]);
                }

                if (spltStr[0].equals("Lithography:")) {
                    dataOfFile.setLithography(spltStr[1]);
                }

                if (spltStr[0].equals("Session start:")) {

                    Date sesTart = parseSessionStart(spltStr[1]);
                    dataOfFile.setSessionStart(sesTart);

                    step = 2;
                }
            }
        }

        bufferedReader.close();

        dataOfFile.setStringcount(i);

        return dataOfFile;
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
