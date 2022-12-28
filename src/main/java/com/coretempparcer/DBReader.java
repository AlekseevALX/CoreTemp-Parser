package com.coretempparcer;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class DBReader {
    java.sql.Timestamp tsStart;
    java.sql.Timestamp tsFinish;

    Date dateFrom;

    Date dateTo;

    ResultSet resSel;

    public DBReader() {
    }

    private String tableName = "CoreTemp";

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public HashMap<String, HashMap<String, HashMap<Date, Float>>> prepareChartData(HashMap<String, HashMap<String, HashMap<Date, Float>>> chartData) {

        try {
            readFromDB(chartData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return chartData;
    }

    private void readFromDB(HashMap<String, HashMap<String, HashMap<Date, Float>>> chartData) throws SQLException {

        PreparedStatement stm;

        String queryText = getQueryTextRead();

        if (MainClass.connectionToBase()) {

            stm = MainClass.con.prepareStatement(queryText);

            setupParametersToSelect(stm, tsStart, tsFinish);

            resSel = stm.executeQuery();

            MainClass.con.close();

            ArrayList<String> columns = getColumnNames();

            defineCountOfCores(columns);

            defineChartDataStructure(chartData, columns);

            String colTime = MainClass.colTime;
            String colTemp = MainClass.colTemp;
            String colLoad = MainClass.colLoad;
            String colSpeed = MainClass.colSpeed;
            String colCpu = MainClass.colCpu;

            while (resSel.next()) {

                Date date = resSel.getTimestamp(colTime);

                fillingOneChart(chartData, resSel, date, colTemp);
                fillingOneChart(chartData, resSel, date, colLoad);
                fillingOneChart(chartData, resSel, date, colSpeed);

            }
        }
    }

    private void fillingOneChart(HashMap<String, HashMap<String, HashMap<Date, Float>>> chartData, ResultSet resSel, Date date, String col) throws SQLException {
        HashMap<String, HashMap<Date, Float>> mapTemp = chartData.get(col);

        String field = "";

        Float val;

        if (mapTemp != null) {
            for (int i = 0; i < MainClass.countOfCores; i++) {
                field = "core" + i + col;
                HashMap<Date, Float> point = mapTemp.get(field);
                val = resSel.getFloat(field);
                point.put(date, val);
            }
        }
    }

    private void defineChartDataStructure(HashMap<String, HashMap<String, HashMap<Date, Float>>> chartData, ArrayList<String> columns) {
        String colTime = "time";
        String colTemp = "temp";
        String colLoad = "load";
        String colSpeed = "speedmhz";
        String colCpu = "cpu";

        for (String s : columns) {
            if (s.equals(colTime) || s.substring(0, 3).equals(colCpu)) continue;

            if (s.contains(colTemp) && chartData.get(colTemp) == null) {
                chartData.put(colTemp, new HashMap<>());
            }

            if (s.contains(colLoad) && chartData.get(colLoad) == null) {
                chartData.put(colLoad, new HashMap<>());
            }

            if (s.contains(colSpeed) || chartData.get(colSpeed) == null) {
                chartData.put(colSpeed, new HashMap<>());
            }
        }

        if (chartData.get(colTemp) != null) {
            HashMap<String, HashMap<Date, Float>> mapTemp = chartData.get(colTemp);
            for (int i = 0; i < MainClass.countOfCores; i++) {
                mapTemp.put("core" + i + colTemp, new HashMap<>());
            }
        }

        if (chartData.get(colLoad) != null) {
            HashMap<String, HashMap<Date, Float>> mapTemp = chartData.get(colLoad);
            for (int i = 0; i < MainClass.countOfCores; i++) {
                mapTemp.put("core" + i + colLoad, new HashMap<>());
            }
        }

        if (chartData.get(colSpeed) != null) {
            HashMap<String, HashMap<Date, Float>> mapTemp = chartData.get(colSpeed);
            for (int i = 0; i < MainClass.countOfCores; i++) {
                mapTemp.put("core" + i + colSpeed, new HashMap<>());
            }
        }

    }

    private void defineCountOfCores(ArrayList<String> columns) {
        int currCore = 0;
        if (MainClass.countOfCores == 0) {
            for (String s : columns) {
                if (s.length() < 5) continue;
                if ((s.contains("core") || s.contains("Core")) && Integer.parseInt(s.substring(4, 5)) > currCore) {
                    currCore = Integer.parseInt(s.substring(4, 5));
                }
            }

            MainClass.countOfCores = currCore + 1;
        }
    }

    private String getQueryTextRead() {
        String text = "";

        text = text.concat("SELECT ");
        text = text.concat("*");
        text = text.concat(" FROM " + tableName);
        text = text.concat(" WHERE ");
        text = text.concat("time" + " >= ?");
        text = text.concat(" and ");
        text = text.concat("time" + " <= ?");

        return text;
    }

    private ArrayList<String> getColumnNames() throws SQLException {
        ArrayList<String> columns = new ArrayList<>();

        ResultSetMetaData rsmd = resSel.getMetaData();

        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            columns.add(rsmd.getColumnName(i + 1));
        }

        return columns;
    }

    public void setupTimeStamps(Date dateFrom, Date dateTo) {

        tsStart = new java.sql.Timestamp(dateFrom.getTime());
        tsStart.setNanos(0);

        tsFinish = new java.sql.Timestamp(dateTo.getTime());
        tsFinish.setNanos(0);

    }
    private void setupParametersToSelect(PreparedStatement stm, java.sql.Timestamp tsStart, java.sql.Timestamp tsFinish) {
        try {
            stm.setTimestamp(1, tsStart);
            stm.setTimestamp(2, tsFinish);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
