package main;

import java.io.*;
import java.sql.*;
import java.time.Month;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

public class DBWriter {
    private FileData fileData;
    private String tableName = "CoreTemp";

    public DBWriter(FileData fileData) {
        this.fileData = fileData;
    }

    public void writeToBase() {
        HashMap<Integer, String> columns = fileData.getColumns();
        int strCount = fileData.getStrings().size();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:postgresql://localhost:5432/TestDBforJava";
        String login = "postgres";
        String password = "postgres";

        Connection con = null;

        try {
            con = DriverManager.getConnection(url, login, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement stm = null;

        String queryText = getQueryText(columns);

        try {
            stm = con.prepareStatement(queryText);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        HashMap<Integer, String[]> strings = fileData.getStrings();
        String[] oneString;

        for (int i = 0; i < strCount; i++) {
            oneString = strings.get(i);
            setupParameters(stm, oneString);
            try {
                stm.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getQueryText(HashMap<Integer, String> columns) {
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

    private void setupParameters(PreparedStatement stm, String[] oneString) {

        for (int i = 0; i < oneString.length; i++) {
            try {
                if (i == 0) {
                    java.sql.Timestamp dd = parseDate(oneString[i]);
                    stm.setTimestamp(i + 1, dd);
                } else {
                    stm.setString(i + 1, oneString[i]);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private static java.sql.Timestamp parseDate(String dateString) {
        //16:07:11 03/06/22
        String[] spltDate = dateString.split(" ");
        String[] spltTime = spltDate[0].split(":");
        String[] spltDay = spltDate[1].split("/");

        Integer hour = Integer.parseInt(spltTime[0]);
        Integer minute = Integer.parseInt(spltTime[1]);
        Integer second = Integer.parseInt(spltTime[2]);

        Integer month = Integer.parseInt(spltDay[1]);
        Integer date = Integer.parseInt(spltDay[0]);
        Integer year = 2000 + Integer.parseInt(spltDay[2]); //dobavlaem Vladivostok

        GregorianCalendar calendar = new GregorianCalendar(); ///.set(year, month, date, hour, minute, second);
        calendar.set(year, month - 1, date, hour, minute, second);
        Date res = calendar.getTime();

        java.sql.Timestamp sqlDate = new java.sql.Timestamp(res.getTime());

//        int mo = Month.valueOf()
        return sqlDate;
    }
}
