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

    private String tableName = MainClass.getTableName();

    public HashMap<String, HashMap<String, SortedMap<Date, Float>>> prepareChartData(HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartData, String compName) {

        try {
            readFromDB(chartData, compName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return chartData;
    }

    public ArrayList findComputersInDB() throws SQLException {

        ArrayList<String> res = new ArrayList<>();

        PreparedStatement stm;
        String queryText = getQueryTextFindComp();

        if (MainClass.connectionToBase()) {
            stm = MainClass.connectionToDB.prepareStatement(queryText);
            resSel = stm.executeQuery();
            while (resSel.next()) {
                res.add(resSel.getString(1));
            }
        }
        return res;
    }

    private void readFromDB(HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartData, String compName) throws SQLException {

        PreparedStatement stm;

        String queryText = getQueryTextRead(compName);

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
        int initialCountPoints;
        int baseSizeOnePoint;
        int increaseSizeOnePoint;
        int countOfBasePoints;
        int countOfIncreasePoints;
        int residue;

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

    private String getQueryTextRead(String compName) {
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

    private String getQueryTextFindComp() {
        String text = "";

        String colCompName = MainClass.getColCompName();

        text = text.concat("SELECT ")
                .concat(colCompName)
                .concat(" FROM " + tableName)
                .concat(" GROUP BY ")
                .concat(colCompName);

        return text;
    }

    private String getQueryTextFindColumns(String compName) {
        String text = "";

        text = text.concat("SELECT *")
                .concat(" FROM " + tableName)
                .concat(" WHERE ")
                .concat(MainClass.getColCompName())
                .concat(" = ?")
                .concat(" LIMIT 1 ");


        return text;
    }

    public String[] getColumnNamesFromDataBase(String compName) {

        ArrayList<String> columns = new ArrayList<>();

        String var;

        PreparedStatement stm;

        String queryText = getQueryTextFindColumns(compName);

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
