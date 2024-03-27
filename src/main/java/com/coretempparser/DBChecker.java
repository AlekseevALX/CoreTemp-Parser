package com.coretempparser;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DBChecker {
    private static String[] columns;

    private static final String colCompName = MainClass.getColCompName();

    public DBChecker() {
        columns = MainClass.getColNames("");
    }

    public boolean checkDB() throws SQLException {

        String queryText = QueryTextGenerator.getQueryText_CreateIfNotExist(columns, colCompName);

        if (queryText.equals("")) {
            System.out.println("Db is not checked!");
            return false;
        }

        if (MainClass.connectionToBase()) {
            try (Statement stm = MainClass.connectionToDB.createStatement()) {
                stm.execute(queryText);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

            MainClass.addToLog("Db checked in thread " + Thread.currentThread().getName());
            return true;
        } else {
            System.out.println("Db is not checked! No connection to base!");
            MainClass.addToLog("Db is not checked! No connection to base!");
            return false;
        }

    }

    public static boolean dbIsDefined() {
        Statement stm;
        ResultSet resultSet;

        String queryText = QueryTextGenerator.getQueryText_IsDefined();

        try {
            stm = MainClass.connectionToDB.createStatement();
            resultSet = stm.executeQuery(queryText);
            resultSet.next();
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

        String queryText = QueryTextGenerator.getQueryText_CheckDBColumns();

        int db_colCount;

        try {
            stm = MainClass.connectionToDB.createStatement();
            resultSet = stm.executeQuery(queryText);
            ResultSetMetaData metaData = resultSet.getMetaData();
            db_colCount = metaData.getColumnCount();

            if (db_colCount < main_ColNames.length + 1) {  //+1 - because array colNames don't include column 'compname'
                String[] db_ColNames = new String[db_colCount];

                for (int i = 0; i < db_colCount; i++) {
                    db_ColNames[i] = metaData.getColumnName(i + 1);
                }

                for (String mainColName : main_ColNames) {
                    if (!Arrays.asList(db_ColNames).contains(mainColName)) {
                        newColumns.add(mainColName);
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
        String queryText = QueryTextGenerator.getQueryText_IncreaseTable(newColumns);
        stm = MainClass.connectionToDB.createStatement();
        stm.execute(queryText);
    }

}
