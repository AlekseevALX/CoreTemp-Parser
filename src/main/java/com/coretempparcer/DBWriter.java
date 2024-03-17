package com.coretempparcer;

import java.sql.*;
import java.util.*;
import java.util.Date;


public class DBWriter {
    private FileData fileData;
    private String tableName = MainClass.getTableName();

    ResultSet resSel;

    public DBWriter(FileData fileData) {
        this.fileData = fileData;
    }

    public void writeToBase() throws ClassNotFoundException, SQLException {

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


        for (HashMap.Entry pair : strings.entrySet()) {
            if (MainClass.done) {
                MainClass.addToLog("Can't write record to base, process is stopped! Thread:" + Thread.currentThread().getName());
                return;
            }
            oneString = (String[]) pair.getValue();
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
            fileData.setStringcount(0);
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

        java.sql.Timestamp d2 = parseDateToTimeStamp(fileData.getStrings().get(fileData.getStringcount() - 1)[0], fileData.getFileName());

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

        this.fileData.setStringcount(strings.size());
    }

    private static java.sql.Timestamp parseDateToTimeStamp(String dateString, String filename) throws ArrayIndexOutOfBoundsException {
        //16:07:11 03/06/22
        String[] spltDate;
        String[] spltTime;
        String[] spltDay;

        spltDate = dateString.split(" ");

        try {
            spltTime = spltDate[0].split(":");
            spltDay = spltDate[1].split("/");
        } catch (ArrayIndexOutOfBoundsException e) {
            MainClass.addToLog("Bad data in file " + filename);
            MainClass.addToLog("Original string is " + dateString);
            throw new ArrayIndexOutOfBoundsException();
        }


        Integer hour = Integer.parseInt(spltTime[0]);
        Integer minute = Integer.parseInt(spltTime[1]);
        Integer second = Integer.parseInt(spltTime[2]);

        Integer month = Integer.parseInt(spltDay[0]);
        Integer date = Integer.parseInt(spltDay[1]);
        Integer year = 2000 + Integer.parseInt(spltDay[2]); //adding Vladivostok

        GregorianCalendar calendar = new GregorianCalendar(); ///.set(year, month, date, hour, minute, second);
        calendar.set(year, month - 1, date, hour, minute, second);
        Date res = calendar.getTime();

        java.sql.Timestamp sqlDate = new java.sql.Timestamp(res.getTime());
        sqlDate.setNanos(0);

        return sqlDate;
    }
}
