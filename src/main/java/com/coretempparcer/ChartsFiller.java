package com.coretempparcer;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ChartsFiller {

    DBReader dbReader;
    LineChart graphicTemp;
    LineChart graphicLoad;
    LineChart graphicSpeed;
    LineChart graphicPower;

    public void setDbReader(DBReader dbReader) {
        this.dbReader = dbReader;
    }

    public ChartsFiller(LineChart graphicTemp, LineChart graphicLoad, LineChart graphicSpeed, LineChart graphicPower, DBReader dbReader) {
        this.graphicTemp = graphicTemp;
        this.graphicLoad = graphicLoad;
        this.graphicSpeed = graphicSpeed;
        this.graphicPower = graphicPower;
        this.dbReader = dbReader;
    }

    public void setupTimeStamps(Date dateFrom, Date dateTo) {
        dbReader.setupTimeStamps(dateFrom, dateTo);
    }

    public void fillingCharts(ResultSet resSel, HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartData) throws SQLException {

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

    public HashMap<String, HashMap<String, SortedMap<Date, Float>>> prepareChartData(HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartData, String compName) {

        try {
            dbReader.readFromDB(chartData, compName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            fillingCharts(dbReader.getResSel(), chartData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return chartData;
    }

    public void fillingChartsData(HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartData) {
        String colTemp = MainClass.getColdb_temp();
        String colLoad = MainClass.getColdb_load();
        String colSpeed = MainClass.getColdb_speed();
        String colCPUPower = MainClass.getColdb_cpupower();
        HashMap<String, SortedMap<Date, Float>> chartTemp = chartData.get(colTemp);
        HashMap<String, SortedMap<Date, Float>> chartLoad = chartData.get(colLoad);
        HashMap<String, SortedMap<Date, Float>> chartSpeed = chartData.get(colSpeed);
        HashMap<String, SortedMap<Date, Float>> chartCPUPower = chartData.get(colCPUPower);

        clearGraphics();
        SortedMap coreMap;
        XYChart.Series series;

        if (chartTemp != null) {
            for (Map.Entry<String, SortedMap<Date, Float>> entry : chartTemp.entrySet()) {
                coreMap = entry.getValue();
                if (coreMap.size() > 0) {
                    series = new XYChart.Series();
                    series.setName(entry.getKey());
                    this.fillForOneCore(entry.getKey(), coreMap, series, this.graphicTemp);
                }
            }
        }

        if (chartLoad != null) {
            for (Map.Entry<String, SortedMap<Date, Float>> entry : chartLoad.entrySet()) {
                coreMap = entry.getValue();
                if (coreMap.size() > 0) {
                    series = new XYChart.Series();
                    series.setName(entry.getKey());
                    this.fillForOneCore(entry.getKey(), coreMap, series, this.graphicLoad);
                }
            }
        }

        if (chartSpeed != null) {
            for (Map.Entry<String, SortedMap<Date, Float>> entry : chartSpeed.entrySet()) {
                coreMap = entry.getValue();
                if (coreMap.size() > 0) {
                    series = new XYChart.Series();
                    series.setName(entry.getKey());
                    this.fillForOneCore(entry.getKey(), coreMap, series, this.graphicSpeed);
                }
            }
        }

        if (chartCPUPower != null) {
            for (Map.Entry<String, SortedMap<Date, Float>> entry : chartCPUPower.entrySet()) {
                coreMap = entry.getValue();
                if (coreMap.size() > 0) {
                    series = new XYChart.Series();
                    series.setName(entry.getKey());
                    this.fillForOneCore(entry.getKey(), coreMap, series, this.graphicPower);
                }
            }
        }

    }

    private void fillForOneCore(String s, SortedMap<Date, Float> coreMap, XYChart.Series series, LineChart chart) {
        String dateSer;
        Float value;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        for (Map.Entry<Date, Float> entry : coreMap.entrySet()) {
            dateSer = sdf.format(entry.getKey());
            value = entry.getValue();
            series.getData().add(new XYChart.Data(dateSer, value));
        }

        chart.getData().add(series);
    }

    protected void clearGraphics() {
        graphicLoad.getData().clear();
        graphicSpeed.getData().clear();
        graphicTemp.getData().clear();
        graphicPower.getData().clear();
    }

}
