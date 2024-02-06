package com.coretempparcer;

import java.sql.*;
import java.util.*;
import java.util.Date;

public class DBReader {
    java.sql.Timestamp tsStart;
    java.sql.Timestamp tsFinish;
    private Date dateFrom;
    private Date dateTo;
    private ResultSet resSel;

    public DBReader() {
    }

    private String tableName = MainClass.getTableName();

    public HashMap<String, HashMap<String, SortedMap<Date, Float>>> prepareChartData(HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartData) {

        try {
            readFromDB(chartData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return chartData;
    }

    private void readFromDB(HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartData) throws SQLException {

        PreparedStatement stm;

        String queryText = getQueryTextRead();

        String[] columns;

        if (MainClass.connectionToBase()) {

            stm = MainClass.connectionToDB.prepareStatement(queryText);

            setupParametersToSelect(stm, tsStart, tsFinish);

            resSel = stm.executeQuery();

            if (MainClass.isColumnsSettingsIsReaded()) {
                columns = MainClass.getColNames();
            } else {
                columns = getColumnNamesFromDataBase();
            }

            if (MainClass.getCountOfCores() < 0) {
                defineCountOfCores(columns);
            }

            if (!AppController.isChartDataIsDefined()) {
                defineChartDataStructure(chartData, columns);
            } else {
                AppController.clearChartsData();
            }

            String colTime = MainClass.getColdb_time();
            String colTemp = MainClass.getColdb_temp();
            String colLoad = MainClass.getColdb_load();
            String colSpeed = MainClass.getColdb_speed();
            String colPower = MainClass.getColdb_cpupower();

            while (resSel.next()) {

                Date date = resSel.getTimestamp(colTime);

                fillingOneChart(chartData, resSel, date, colTemp);
                fillingOneChart(chartData, resSel, date, colLoad);
                fillingOneChart(chartData, resSel, date, colSpeed);
                fillingOneChart(chartData, resSel, date, colPower);

            }

            if (MainClass.getCountOfCharPoint() > 0) {
                roundTheGraphToTheNumberOfPoints(chartData);
            }
        }
    }

    private void roundTheGraphToTheNumberOfPoints(HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartsData) {
        int numCharPoints = MainClass.getCountOfCharPoint();
        int initialCountPoints = 0;
        int baseSizeOnePoint = 0;
        int increaseSizeOnePoint = 0;
        int countOfBasePoints = 0;
        int countOfIncreasePoints = 0;
        int residue = 0;

        for (Map.Entry<String, HashMap<String, SortedMap<Date, Float>>> chart : chartsData.entrySet()) {    //1

            HashMap<String, SortedMap<Date, Float>> cores = chart.getValue();

            for (Map.Entry<String, SortedMap<Date, Float>> core : cores.entrySet()) {                       //2
                SortedMap<Date, Float> coreData = core.getValue();
                String coreName = core.getKey();

                if (coreData.size() > numCharPoints) {
                    SortedMap<Date, Float> res = new TreeMap<>();

                    initialCountPoints = coreData.size();
                    baseSizeOnePoint = initialCountPoints / numCharPoints;
                    increaseSizeOnePoint = baseSizeOnePoint + 1;
                    residue = initialCountPoints % numCharPoints;

                    countOfBasePoints = numCharPoints - residue;
                    countOfIncreasePoints = residue;

                    Date dateNewPoint = null;

                    float newPoint = 0;
                    int ch = 0;

                    float fullnessBasePoint = 0;
                    float fullnessIncreasePoint = 0;

                    float chBasePoint = 0;
                    float chIncreasePoint = 0;


                    if (residue == 0) {
                        for (Map.Entry<Date, Float> record : coreData.entrySet()) {     //this map which we must operate with                        //3
                            ch++;
                            newPoint += record.getValue();

                            if (dateNewPoint == null) {
                                dateNewPoint = record.getKey();
                            }

                            if (ch == baseSizeOnePoint) { //when no need to increase points
                                newPoint = newPoint / ch;
                                ch = 0;
                                res.put(dateNewPoint, newPoint);

                                newPoint = 0;
                                dateNewPoint = null;

                            }
                        }
                        cores.put(coreName, res);

                    } else {
                        for (Map.Entry<Date, Float> record : coreData.entrySet()) {     //this map which we must operate with                        //3
                            ch++;
                            newPoint += record.getValue();

                            if (dateNewPoint == null) {
                                dateNewPoint = record.getKey();
                            }

                            if (ch == baseSizeOnePoint && fullnessBasePoint < 1 && fullnessBasePoint < fullnessIncreasePoint) {
                                newPoint = newPoint / ch;
                                ch = 0;
                                res.put(dateNewPoint, newPoint);

                                newPoint = 0;
                                dateNewPoint = null;

                                chBasePoint++;
                                fullnessBasePoint = chBasePoint / countOfBasePoints;

                                continue;
                            }

                            if (ch == increaseSizeOnePoint && fullnessIncreasePoint < 1 && fullnessBasePoint >= fullnessIncreasePoint) {
                                newPoint = newPoint / ch;
                                ch = 0;
                                res.put(dateNewPoint, newPoint);

                                newPoint = 0;
                                dateNewPoint = null;

                                chIncreasePoint++;
                                fullnessIncreasePoint = chIncreasePoint / countOfIncreasePoints;

                            }

                        }

                        cores.put(coreName, res);

                    }


                }


            }
        }
    }

    private void fillingOneChart(HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartData, ResultSet resSel, Date date, String col) throws SQLException {
        HashMap<String, SortedMap<Date, Float>> mapCores = chartData.get(col);

        String field;

        Float val;

        if (mapCores != null) {
            for (Map.Entry<String, SortedMap<Date, Float>> entry : mapCores.entrySet()) {
                field = entry.getKey();

                if (!field.endsWith(col)) continue;

                SortedMap<Date, Float> pointsOfCore = mapCores.get(field);
                val = resSel.getFloat(field);
                pointsOfCore.put(date, val);
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

    private void defineCountOfCores(String[] columns) {
        int currCore = 0;
        int countOfCores = MainClass.getCountOfCores();
        String colCore = MainClass.getColdb_core();

        if (countOfCores < 0) {
            for (String s : columns) {
                if (s.length() < 5) continue;
                if ((s.toUpperCase().contains(colCore.toUpperCase())) && Integer.parseInt(s.substring(4, 5)) > currCore) {
                    currCore = Integer.parseInt(s.substring(4, 5));
                }
            }

            countOfCores = currCore + 1;
            MainClass.setCountOfCores(countOfCores);
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

    private String[] getColumnNamesFromDataBase() throws SQLException {

        ResultSetMetaData rsmd = resSel.getMetaData();

        String[] columns = new String[rsmd.getColumnCount()];

        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            columns[i] = rsmd.getColumnName(i + 1);
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
