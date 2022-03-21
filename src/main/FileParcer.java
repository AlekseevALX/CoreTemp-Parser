package main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Month;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class FileParcer {

    public static FileData parceFile() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        String file = new String();

        file = bufferedReader.readLine();

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
                spltStr = oneString.split(",");
                dataOfFile.addString(i, spltStr);
                i += 1;
            }

            if (step == 2) {

                spltStrCol = oneString.split(",");
                int a = 0;
                for (String s : spltStrCol) {
                    s = prepareString(s);
                    if (!s.equals("")) {
                        dataOfFile.addColumn(a, s);
                        a += 1;
                    }
                }
                dataOfFile.setColumnCount(a);

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
        return dataOfFile;
    }

    public static String prepareString(String s) {
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
