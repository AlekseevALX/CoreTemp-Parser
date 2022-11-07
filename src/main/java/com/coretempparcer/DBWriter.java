package com.coretempparcer;



import org.postgresql.jdbc.PgResultSet;

import java.sql.*;
import java.util.*;
import java.util.Date;


public class DBWriter {
    private FileData fileData;
    private String tableName = "CoreTemp";

    ResultSet resSel;

    public DBWriter(FileData fileData) {
        this.fileData = fileData;
    }

    public void writeToBase() throws ClassNotFoundException, SQLException {

        deleteAlreadyExistsRecords();

        HashMap<Integer, String> columns = fileData.getColumns();

//        Class.forName("org.postgresql.Driver");

        PreparedStatement stm;

        String queryText = getQueryTextInsert(columns);

        stm = MainClass.con.prepareStatement(queryText);

        HashMap<Integer, String[]> strings = fileData.getStrings();
        String[] oneString;

        for (HashMap.Entry pair : strings.entrySet()) {
            oneString = (String[]) pair.getValue();
            setupParameters(stm, oneString);
            stm.executeUpdate();
        }

        stm.close();
    }

    public void deleteAlreadyExistsRecords() throws ArrayIndexOutOfBoundsException, SQLException {
        HashMap<Integer, String> columns = fileData.getColumns();
        HashMap<Integer, String[]> strings = fileData.getStrings();

        PreparedStatement stm;

        String queryText = getQueryTextExists(columns);

        stm = MainClass.con.prepareStatement(queryText);

        setupParametersToSelect(stm, fileData);

        resSel = stm.executeQuery();

        if (((PgResultSet) resSel).getLastUsedFetchSize() == strings.size()) {
            fileData.setStrings(new HashMap<>());
            fileData.setStringcount(0);
//            con.close();
            stm.close();
            return;
        }

        while (resSel.next()) {
            seekAndDestroy(resSel.getTimestamp(1), strings);
        }

//        con.close();
        stm.close();
    }

    private String getQueryTextInsert(HashMap<Integer, String> columns) {
        //        String sql = "INSERT INTO JC_CONTACT (FIRST_NAME, LAST_NAME, PHONE, EMAIL) VALUES (?, ?, ?,?)";
        String text = "";
        int columnCount = fileData.getColumnCount();

        text = text.concat("INSERT INTO " + tableName + " ");
        text = text.concat("(");

        for (int i = 0; i < columnCount; i++) {
            text = text.concat(columns.get(i)) + ",";
        }

        text = text.substring(0, text.length() - 1);
        text = text.concat(") ");
        text = text.concat("VALUES ");
        text = text.concat("(");

        for (int i = 0; i < columnCount; i++) {
            text = text.concat("?,");
        }

        text = text.substring(0, text.length() - 1);
        text = text.concat(") ");

        return text;
    }

    private String getQueryTextExists(HashMap<Integer, String> columns) {
        //SELECT TIME FROM CORETEMP WHERE TIME >= ? and TIME <= ?
        String text = "";
        String colName = columns.get(0);

        text = text.concat("SELECT ");
        text = text.concat(colName);
        text = text.concat(" FROM " + tableName);
        text = text.concat(" WHERE ");
        text = text.concat(colName + " >= ?");
        text = text.concat(" and ");
        text = text.concat(colName + " <= ?");

        return text;
    }

    private void setupParameters(PreparedStatement stm, String[] oneString) {

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

    }

    private void setupParametersToSelect(PreparedStatement stm, FileData fileData) throws ArrayIndexOutOfBoundsException {
//        java.sql.Timestamp dd = parseDate(oneString[i]);
//        stm.setTimestamp(i + 1, dd);
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
        String[] spltDate = new String[2];
        String[] spltTime = new String[3];
        String[] spltDay = new String[3];

        spltDate = dateString.split(" ");

        try {
            spltTime = spltDate[0].split(":");
            spltDay = spltDate[1].split("/");
        }
        catch (ArrayIndexOutOfBoundsException e){
            MainClass.writeToLog("Bad data in file " + filename);
            MainClass.writeToLog("Original string is " + dateString);
            throw new ArrayIndexOutOfBoundsException();
        }


        Integer hour = Integer.parseInt(spltTime[0]);
        Integer minute = Integer.parseInt(spltTime[1]);
        Integer second = Integer.parseInt(spltTime[2]);

        Integer month = Integer.parseInt(spltDay[0]);
        Integer date = Integer.parseInt(spltDay[1]);
        Integer year = 2000 + Integer.parseInt(spltDay[2]); //dobavlaem Vladivostok

        GregorianCalendar calendar = new GregorianCalendar(); ///.set(year, month, date, hour, minute, second);
        calendar.set(year, month - 1, date, hour, minute, second);
        Date res = calendar.getTime();

        java.sql.Timestamp sqlDate = new java.sql.Timestamp(res.getTime());
        sqlDate.setNanos(0);

//        int mo = Month.valueOf()
        return sqlDate;
    }
}
