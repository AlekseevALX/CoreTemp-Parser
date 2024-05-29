package com.coretempparser;

import java.util.ArrayList;

public class QueryTextGenerator {

    public static String getQueryText_CreateIfNotExist(String[] columns, String colCompName) {
        if (columns == null || columns.length == 0) {
            return "";
        }

        String text = "";
        String colName;
        String tableName = MainClass.getTableName();

        text = text.concat("CREATE TABLE IF NOT EXISTS " + tableName + "(");

        text = text.concat(columns[0] + " " + "timestamp, ");

        text = text.concat(colCompName + " varchar(20), ");

        int ch = columns.length;

        for (int i = 2; i < ch; i++) {
            colName = columns[i];
            if (colName.equals("")) continue;

            text = text.concat(colName + " varchar(10), ");
        }

        text = text.concat("PRIMARY KEY (")
                .concat(columns[0])
                .concat(", ")
                .concat(colCompName)
                .concat(")");

        text = text.concat(");");

        return text;
    }

    public static String getQueryText_IncreaseTable(ArrayList<String> newColumns) {
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

    public static String getQueryText_IsDefined() {
        String tableName = MainClass.getTableName();
        String colTime = MainClass.getColdb_time();
        return "SELECT " + colTime + " FROM " + tableName.toUpperCase() + " " +
                "ORDER BY " + colTime + " DESC LIMIT 1";
    }

    public static String getQueryText_CheckDBColumns() {
        String text = "";
        String tableName = MainClass.getTableName();
        text = text.concat("SELECT * FROM ")
                .concat(tableName)
                .concat(" LIMIT 1");

        return text;
    }

    public static String getQueryText_Insert(String[] columns, String compName, String tableName, int countOfRecords) {

        String text = "";
        int columnCount = columns.length;

        text = text.concat("INSERT INTO " + tableName + " ");
        text = text.concat("(");

        for (String column : columns) {
            text = text.concat(column) + ",";
        }

        if (!compName.isEmpty()) {
            text = text.concat(MainClass.getColCompName()) + ",";
        }

        text = text.substring(0, text.length() - 1);
        text = text.concat(") ");
        text = text.concat("VALUES ");

        for (int i = 0; i < countOfRecords; i++) {
            text = text.concat("(");

            for (int j = 0; j < columnCount; j++) {
                text = text.concat("?,");
            }

            if (!compName.isEmpty()) {
                text = text.concat("?,");
            }

            text = text.substring(0, text.length() - 1);
            text = text.concat("), ");
        }

        text = text.substring(0, text.length() - 2);

        return text;
    }

    public static String getQueryText_Exists(String[] columns, String compName, String tableName) {
        String text = "";
        String colTime = columns[0];

        text = text.concat("SELECT ")
                .concat(colTime)
                .concat(" FROM " + tableName)
                .concat(" WHERE ")
                .concat(colTime + " >= ?")
                .concat(" and ")
                .concat(colTime + " <= ?");

        if (!compName.isEmpty()) {
            text = text.concat(" and ")
                    .concat(MainClass.getColCompName() + " =?");
        }

        return text;
    }

    public static String getQueryText_Read(String compName, String tableName) {
        String text = "";

        String colCompName = MainClass.getColCompName();

        text = text.concat("SELECT ")
                .concat("*")
                .concat(" FROM " + tableName)
                .concat(" WHERE ")
                .concat("time" + " >= ?")
                .concat(" and ")
                .concat("time" + " <= ?");

        if (!compName.isEmpty()) {
            text = text.concat(" and ")
                    .concat(colCompName + "= ?");
        }

        return text;
    }

    public static String getQueryText_FindComps(String tableName) {
        String text = "";

        String colCompName = MainClass.getColCompName();

        text = text.concat("SELECT ")
                .concat(colCompName)
                .concat(" FROM " + tableName)
                .concat(" GROUP BY ")
                .concat(colCompName);

        return text;
    }

    public static String getQueryText_FindColumns(String tableName) {
        String text = "";

        text = text.concat("SELECT *")
                .concat(" FROM " + tableName)
                .concat(" WHERE ")
                .concat(MainClass.getColCompName())
                .concat(" = ?")
                .concat(" LIMIT 1 ");

        return text;
    }

    public static String getQueryText_FindLastDate() {
        String colTime = MainClass.getColdb_time();
        String colComp = MainClass.getColCompName();
        String tableName = MainClass.getTableName();
        String thisCompName = MainClass.getComputerName();
        String text = "";

        text = text.concat("SELECT ")
                .concat(colTime)
                .concat(" FROM ")
                .concat(tableName)
                .concat(" WHERE ")
                .concat(colComp)
                .concat(" = ")
                .concat("'")
                .concat(thisCompName)
                .concat("'")
                .concat(" ORDER BY time DESC LIMIT 1");

        return text;
    }

    public static String getQueryText_DeleteBase(String tableName) {
        return "DROP TABLE IF EXISTS " + tableName;
    }
}
