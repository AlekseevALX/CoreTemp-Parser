package com.coretempparser;

import java.sql.*;
import java.util.*;
import java.util.Date;


public class DBWriter {
    private final FileData fileData;
    private final String tableName = MainClass.getTableName();

    ResultSet resSel;

    public DBWriter(FileData fileData) {
        this.fileData = fileData;
    }

    public void writeToBase() throws SQLException {

        String compName = MainClass.getComputerName();

        if (fileData.isNeedsToFindExistingRecords()) {
            deleteAlreadyExistsRecords(compName);
        }

        String[] columns = MainClass.getColNames(compName);

        PreparedStatement stm;

        String queryText = QueryTextGenerator.getQueryText_Insert(columns, compName, tableName);

        if (MainClass.connectionToBase()) {
            stm = MainClass.connectionToDB.prepareStatement(queryText);
        } else {
            MainClass.addToLog("fail write to base");
            return;
        }

        HashMap<Integer, String[]> strings = fileData.getStrings();
        String[] oneString;


        for (HashMap.Entry<Integer, String[]> pair : strings.entrySet()) {
            if (MainClass.done) {
                MainClass.addToLog("Can't write record to base, process is stopped! Thread:" + Thread.currentThread().getName());
                return;
            }
            oneString = pair.getValue();
            setupParameters(stm, oneString, compName);
            try {
                stm.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        stm.close();
    }

    public void deleteAlreadyExistsRecords(String compName) throws ArrayIndexOutOfBoundsException, SQLException {
        String[] columns = MainClass.getColNames(compName);
        HashMap<Integer, String[]> strings = fileData.getStrings();

        PreparedStatement stm;

        String queryText = QueryTextGenerator.getQueryText_Exists(columns, compName, tableName);

        if (MainClass.connectionToBase()) {
            stm = MainClass.connectionToDB.prepareStatement(queryText);
        } else {
            MainClass.addToLog("fail deleteAlreadyExistsRecords");
            return;
        }

        setupParametersToSelect(stm, fileData, compName);

        resSel = stm.executeQuery();

        if (resSel.getFetchSize() == strings.size()) {
            fileData.setStrings(new HashMap<>());
            fileData.setStringCount(0);
            stm.close();
            return;
        }

        while (resSel.next()) {
            seekAndDestroy(resSel.getTimestamp(1), strings);
        }

        stm.close();
    }

    private void setupParameters(PreparedStatement stm, String[] oneString, String compName) {

        for (int i = 0; i < oneString.length; i++) {
            try {
                if (i == 0) {
                    java.sql.Timestamp dd = parseDateToTimeStamp(oneString[i], this.fileData.getFileName());
                    stm.setTimestamp(i + 1, dd);
                } else {
                    stm.setString(i + 1, oneString[i]);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (!compName.isEmpty()) {
            try {
                stm.setString(oneString.length + 1, compName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void setupParametersToSelect(PreparedStatement stm, FileData fileData, String compName) throws ArrayIndexOutOfBoundsException {

        java.sql.Timestamp d1 = new java.sql.Timestamp(0);

        try {
            d1 = parseDateToTimeStamp(fileData.getStrings().get(0)[0], fileData.getFileName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        java.sql.Timestamp d2 = parseDateToTimeStamp(fileData.getStrings().get(fileData.getStringCount() - 1)[0], fileData.getFileName());

        try {
            stm.setTimestamp(1, d1);
            stm.setTimestamp(2, d2);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!compName.isEmpty()) {
            try {
                stm.setString(3, compName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void seekAndDestroy(Timestamp timestamp, HashMap<Integer, String[]> strings) {
        String dateStr;

        Iterator<Map.Entry<Integer, String[]>> iterator = strings.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, String[]> pair = iterator.next();
            String[] value = pair.getValue();
            dateStr = value[0];
            Timestamp ts = parseDateToTimeStamp(dateStr, this.fileData.getFileName());

            if (ts.getTime() == timestamp.getTime()) {
                iterator.remove();
            }
        }

        this.fileData.setStringCount(strings.size());
    }

    private static java.sql.Timestamp parseDateToTimeStamp(String dateString, String filename) throws ArrayIndexOutOfBoundsException {
        //16:07:11 03/06/22
        String[] splitDate;
        String[] splitTime;
        String[] splitDay;

        splitDate = dateString.split(" ");

        try {
            splitTime = splitDate[0].split(":");
            splitDay = splitDate[1].split("/");
        } catch (ArrayIndexOutOfBoundsException e) {
            MainClass.addToLog("Bad data in file " + filename);
            MainClass.addToLog("Original string is " + dateString);
            throw new ArrayIndexOutOfBoundsException();
        }


        int hour = Integer.parseInt(splitTime[0]);
        int minute = Integer.parseInt(splitTime[1]);
        int second = Integer.parseInt(splitTime[2]);

        int month = Integer.parseInt(splitDay[0]);
        int date = Integer.parseInt(splitDay[1]);
        int year = 2000 + Integer.parseInt(splitDay[2]); //adding Vladivostok

        GregorianCalendar calendar = new GregorianCalendar(); ///.set(year, month, date, hour, minute, second);
        calendar.set(year, month - 1, date, hour, minute, second);
        Date res = calendar.getTime();

        java.sql.Timestamp sqlDate = new java.sql.Timestamp(res.getTime());
        sqlDate.setNanos(0);

        return sqlDate;
    }
}
