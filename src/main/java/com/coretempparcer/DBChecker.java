package com.coretempparcer;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DBChecker {
    private static String[] columns;

    private static String colCompName = MainClass.getColCompName();

    public DBChecker() {
        this.columns = MainClass.getColNames("");
    }

    public void checkDB() throws SQLException {

        String queryText = getExecuteText();

        if (MainClass.connectionToBase()) {
            try (Statement stm = MainClass.connectionToDB.createStatement()) {
                stm.execute(queryText);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            MainClass.addToLog("Db checked in thread " + Thread.currentThread().getName());
        } else {
            MainClass.addToLog("Db is not checked! In thread " + Thread.currentThread().getName());
        }

    }

    private static String getExecuteText() {
        String text = "";
        String colName = "";
        String tableName = MainClass.getTableName();

        text = text.concat("CREATE TABLE IF NOT EXISTS " + tableName + "(");

        text = text.concat(columns[0] + " " + "timestamp, ");

        text = text.concat(colCompName + " varchar(20), ");

        int ch = columns.length;

        for (int i = 1; i < ch; i++) {
            colName = columns[i];
            if (colName.equals("")) continue;

            text = text.concat(colName + " varchar(10), ");
        }

        text = text.concat("PRIMARY KEY (")
                .concat(columns[0])
                .concat(",")
                .concat(colCompName)
                .concat(")");

        text = text.concat(");");

        return text;
    }

    public static boolean dbIsDefined() {
        Statement stm;
        ResultSet resultSet;

        String queryText = getIsDefinedText();

        try {
            stm = MainClass.connectionToDB.createStatement();
            resultSet = stm.executeQuery(queryText);
            resultSet.next();
            Timestamp d = resultSet.getTimestamp(1);
            MainClass.addToLog("checking db is defined: positive");
            MainClass.addToLog("searching actual files to parsing");
            return true;
        } catch (SQLException e) {
            MainClass.addToLog("checking db is defined: negative"); //Must rewrite this method of checking later
            MainClass.addToLog("All files will be parsed");
            return false;
        }

    }

    public static void checkDBColumns() {
        Statement stm;
        ResultSet resultSet;
        String[] main_ColNames = MainClass.getColNames(MainClass.getComputerName());
        ArrayList<String> newColumns = new ArrayList<>();

        String queryText = getcheckDBColumnsText();

        int db_colCount = 0;

        try {
            stm = MainClass.connectionToDB.createStatement();
            resultSet = stm.executeQuery(queryText);
            ResultSetMetaData metaData = resultSet.getMetaData();
            db_colCount = metaData.getColumnCount();

            if (db_colCount < main_ColNames.length + 1) {  //+1 - because array colnames don't include column 'compname'
                String[] db_ColNames = new String[db_colCount];

                for (int i = 0; i < db_colCount; i++) {
                    db_ColNames[i] = metaData.getColumnName(i + 1);
                }

                for (int i = 0; i < main_ColNames.length; i++) {
                    if (!Arrays.asList(db_ColNames).contains(main_ColNames[i])) {
                        newColumns.add(main_ColNames[i]);
                    }
                }

                if (newColumns.size() > 0) {
                    increaseTable(newColumns);
                    String message = "DataBase increased with columns ";
                    for (String s : newColumns) {
                        message = message.concat(s + "," + System.lineSeparator());
                    }
                    message = message.substring(0, message.length() - 3);
                    MainClass.addToLog(message);
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void increaseTable(ArrayList<String> newColumns) throws SQLException {
        Statement stm;
        String queryText = getIncreaseText(newColumns);
        stm = MainClass.connectionToDB.createStatement();
        stm.execute(queryText);
    }

    private static String getIncreaseText(ArrayList<String> newColumns) {
        String text = "";
        text = text.concat("ALTER TABLE ")
                .concat(MainClass.getTableName());

        for (String s : newColumns) {
            text = text.concat(" ADD ")
                    .concat(s)
                    .concat(" varchar(10),");
        }

        text = text.substring(0, text.length() - 1);

        return text;
    }

    private static String getIsDefinedText() {
        String tableName = MainClass.getTableName();
        String colTime = MainClass.getColdb_time();
        String text = "SELECT " + colTime + " FROM " + tableName.toUpperCase() + " " +
                "ORDER BY " + colTime + " DESC LIMIT 1";

        return text;
    }

    private static String getcheckDBColumnsText() {
        String text = "";
        String tableName = MainClass.getTableName();
        text = text.concat("SELECT * FROM ")
                .concat(tableName)
                .concat(" LIMIT 1");

        return text;
    }
}
