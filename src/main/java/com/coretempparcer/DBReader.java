package com.coretempparcer;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class DBReader {
    java.sql.Timestamp tsStart;
    java.sql.Timestamp tsFinish;

    private ResultSet resSel;

    public DBReader() {

    }

    public ResultSet getResSel() {
        return resSel;
    }

    private String tableName = MainClass.getTableName();

    public ArrayList findComputersInDB() throws SQLException {

        ArrayList<String> res = new ArrayList<>();

        PreparedStatement stm;

        String queryText = QueryTextGenerator.getQueryText_FindComps(tableName);

        if (MainClass.connectionToBase()) {
            stm = MainClass.connectionToDB.prepareStatement(queryText);
            resSel = stm.executeQuery();
            while (resSel.next()) {
                res.add(resSel.getString(1));
            }
        }
        return res;
    }

    public void readFromDB(HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartData, String compName) throws SQLException {

        PreparedStatement stm;

        String queryText = QueryTextGenerator.getQueryText_Read(compName, tableName);

        String[] columns;

        if (MainClass.connectionToBase()) {

            stm = MainClass.connectionToDB.prepareStatement(queryText);

            setupParametersToSelect(stm, tsStart, tsFinish, compName);

            resSel = stm.executeQuery();

            columns = MainClass.getColNames(compName);

            if (!AppController.isChartDataIsDefined()) {
                defineChartDataStructure(chartData, columns);
            } else {
                AppController.clearChartsData();
            }

        }
    }

    private void defineChartDataStructure(HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartData, String[] columns) {
        String colTime = MainClass.getColdb_time();
        String colTemp = MainClass.getColdb_temp();
        String colLoad = MainClass.getColdb_load();
        String colSpeed = MainClass.getColdb_speed();
        String colCpuPower = MainClass.getColdb_cpupower();

        for (String s : columns) {
            if (s.equals(colTime)) continue;

            if (s.contains(colTemp) && chartData.get(colTemp) == null) {
                chartData.put(colTemp, new HashMap<>());
            }

            if (s.contains(colLoad) && chartData.get(colLoad) == null) {
                chartData.put(colLoad, new HashMap<>());
            }

            if (s.contains(colSpeed) && chartData.get(colSpeed) == null) {
                chartData.put(colSpeed, new HashMap<>());
            }

            if (s.contains(colCpuPower) && chartData.get(colCpuPower) == null) {
                chartData.put(colCpuPower, new HashMap<>());
            }
        }

        for (String s : columns) {
            if (s.endsWith(colCpuPower) && chartData.get(colCpuPower) != null) {
                HashMap<String, SortedMap<Date, Float>> mapCPUPower = chartData.get(colCpuPower);
                mapCPUPower.put(s, new TreeMap<>());
            }

            if (s.endsWith(colTemp) && chartData.get(colTemp) != null) {
                HashMap<String, SortedMap<Date, Float>> mapTemp = chartData.get(colTemp);
                mapTemp.put(s, new TreeMap<>());
            }

            if (s.endsWith(colLoad) && chartData.get(colLoad) != null) {
                HashMap<String, SortedMap<Date, Float>> mapLoad = chartData.get(colLoad);
                mapLoad.put(s, new TreeMap<>());
            }

            if (s.endsWith(colSpeed) && chartData.get(colSpeed) != null) {
                HashMap<String, SortedMap<Date, Float>> mapSpeed = chartData.get(colSpeed);
                mapSpeed.put(s, new TreeMap<>());
            }
        }

        AppController.setChartDataIsDefined(true);

    }

    public String[] getColumnNamesFromDataBase(String compName) {

        ArrayList<String> columns = new ArrayList<>();

        String var;

        PreparedStatement stm;

        String queryText = QueryTextGenerator.getQueryText_FindColumns(tableName);

        if (MainClass.connectionToBase()) {

            try {
                stm = MainClass.connectionToDB.prepareStatement(queryText);
                stm.setString(1, compName);
                resSel = stm.executeQuery();
                resSel.next();
                ResultSetMetaData rsmd = resSel.getMetaData();

                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    if (i + 1 == 1) {
                        columns.add(rsmd.getColumnName(i + 1));
                    } else {
                        var = resSel.getString(i + 1);
                        if (var != null && !var.isEmpty()) {
                            columns.add(rsmd.getColumnName(i + 1));
                        }
                    }
                }
            } catch (SQLException e) {
                columns.toArray(new String[0]);
            }

        } else {
            columns.toArray(new String[0]);
        }

        return columns.toArray(new String[0]);
    }

    public void setupTimeStamps(Date dateFrom, Date dateTo) {

        tsStart = new java.sql.Timestamp(dateFrom.getTime());
        tsStart.setNanos(0);

        tsFinish = new java.sql.Timestamp(dateTo.getTime());
        tsFinish.setNanos(0);

    }

    private void setupParametersToSelect(PreparedStatement stm, java.sql.Timestamp tsStart, java.sql.Timestamp tsFinish, String compName) {
        try {
            stm.setTimestamp(1, tsStart);
            stm.setTimestamp(2, tsFinish);
            if (!compName.isEmpty()) {
                stm.setString(3, compName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
