package com.coretempparcer;

import java.sql.*;
import java.util.HashMap;

public class DBChecker {
    private static HashMap<Integer, String> columns;

    public DBChecker(HashMap<Integer, String> columns) {
        this.columns = columns;
    }

    private static String getExecuteText() {
        String text = "";
        String colName = "";
        String tableName = MainClass.getTableName();

        text = text.concat("CREATE TABLE IF NOT EXISTS " + tableName + "(");
        text = text.concat(columns.get(0) + " " + "timestamp UNIQUE, ");

        int ch = columns.size();

        for (int i = 1; i < ch; i++) {
            colName = columns.get(i);
            if (colName.equals("")) continue;

            text = text.concat(colName + " varchar(10), ");
        }

        text = text.substring(0, text.length() - 2) + ");";

        return text;
    }

    public void checkDB() {
        Statement stm;

        try {
            stm = MainClass.con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        String queryText = getExecuteText();

        try {
            stm.execute(queryText);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        MainClass.writeToLog("Db checked in thread " + Thread.currentThread().getName());

    }

    public static boolean dbIsDefined(){
        Statement stm;
        ResultSet resultSet;

        String queryText = getIsDefinedText();

        try {
            stm = MainClass.con.createStatement();
            resultSet = stm.executeQuery(queryText);
            resultSet.next();
            Timestamp d = resultSet.getTimestamp(1);
            MainClass.writeToLog("checking db is defined: positive");
            MainClass.writeToLog("searching actual files to parsing");
            return true;
        } catch (SQLException e) {
            MainClass.writeToLog("checking db is defined: negative"); //Must rewrite this method of checking later
            MainClass.writeToLog("All files will be parsed");
            return false;
        }

    }

    public static String getIsDefinedText() {
        String tableName = MainClass.getTableName();
        String colTime = MainClass.getColdb_time();
        String text = "SELECT " + colTime + " FROM " + tableName.toUpperCase() + " " +
                "ORDER BY " + colTime + " DESC LIMIT 1";

        return text;
    }
}
