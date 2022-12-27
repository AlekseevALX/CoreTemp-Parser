package com.coretempparcer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.util.Pair;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppController {

    DBReader dbReader = new DBReader();
    @FXML
    private TextField filepath;
    @FXML
    private TextField dateFromH;
    @FXML
    private TextField dateFromM;
    @FXML
    private TextField dateFromS;
    @FXML
    private TextField dateToH;
    @FXML
    private TextField dateToM;
    @FXML
    private TextField dateToS;
    @FXML
    LineChart graphicTemp;
    @FXML
    LineChart graphicLoad;
    @FXML
    LineChart graphicSpeed;

    @FXML
    private TextArea textLog;

    @FXML
    DatePicker dateTo;

    @FXML
    DatePicker dateFrom;

    @FXML
    ToggleButton autoButton;

    Timeline tl;

    Timeline refresh;

    Timeline parsing;

    @FXML
    protected void onStartButtonClick() {

        toParce();

    }

    protected void toParce() {
        MainClass.log = "";

        MainClass.writeToLog("Welcome to the jungle!!");
        String[] args = new String[1];
        args[0] = filepath.getText();

        MainClass mainObject = new MainClass();

        MainClass.done = false;
        MainClass.countOfThreads = 0;

        mainObject.main(args);

        KeyFrame kf = new KeyFrame(Duration.millis(100), actionEvent -> waitTillTheEnd());
        tl = new Timeline(kf);
        tl.setCycleCount(1);
        tl.play();
    }

    @FXML
    protected void autoButtonClicked() {
        MainClass.auto = autoButton.isSelected();

        if (MainClass.auto) {
            KeyFrame kfAuto = new KeyFrame(Duration.millis(5000), actionEvent -> autoRefresh());
            refresh = new Timeline(kfAuto);
            refresh.setCycleCount(1);
            refresh.play();
        }

        if (MainClass.auto) {
            KeyFrame kfParse = new KeyFrame(Duration.millis(5000), actionEvent -> autoParse());
            parsing = new Timeline(kfParse);
            parsing.setCycleCount(1);
            parsing.play();
        }
    }

    @FXML
    protected void DateFromHOnKeyReleased() {
        String val = dateFromH.getText();

        String res = validateTimeFieldValue(val, "H");

        if (!val.equals(res)) {
            dateFromH.setText(res);
        }
    }

    @FXML
    protected void DateFromMOnKeyReleased() {
        String val = dateFromM.getText();

        String res = validateTimeFieldValue(val, "M");

        if (!val.equals(res)) {
            dateFromM.setText(res);
        }
    }

    @FXML
    protected void DateFromSOnKeyReleased() {
        String val = dateFromS.getText();

        String res = validateTimeFieldValue(val, "S");

        if (!val.equals(res)) {
            dateFromS.setText(res);
        }
    }

    @FXML
    protected void DateToHOnKeyReleased() {
        String val = dateToH.getText();

        String res = validateTimeFieldValue(val, "H");

        if (!val.equals(res)) {
            dateToH.setText(res);
        }
    }

    @FXML
    protected void DateToMOnKeyReleased() {
        String val = dateToM.getText();

        String res = validateTimeFieldValue(val, "M");

        if (!val.equals(res)) {
            dateToM.setText(res);
        }
    }

    @FXML
    protected void DateToSOnKeyReleased() {
        String val = dateToS.getText();

        String res = validateTimeFieldValue(val, "S");

        if (!val.equals(res)) {
            dateToS.setText(res);
        }
    }

    @FXML
    protected void mainOKButtonClicked() {

        refreshLinearChartToPeriod();

    }

    @FXML
    protected void mainClearButtonClicked() {

        clearGraphics();

    }

    protected void clearGraphics() {
        graphicLoad.getData().clear();

        graphicSpeed.getData().clear();

        graphicTemp.getData().clear();
    }

    @FXML
    protected void onDeleteBaseButtonClick(){
        MainClass.log = "";
        MainClass.deleteBase();
        textLog.setText(MainClass.log);
        textLog.forward();
    }

    public void refreshLinearChartToPeriod() {

        refreshLinearChart(true);

    }

    public void refreshLinearChartAuto() {

        refreshLinearChart(false);

    }

    public void refreshLinearChart(boolean fixedPeriod) {
        //map of charts data<map of cores in each chart<map of data of each core in chart>>
        HashMap<String, HashMap<String, HashMap<Date, Float>>> chartData = new HashMap<>();

        GregorianCalendar calendar;

        Date date1;

        Date date2;

        if (fixedPeriod){
            //var == 1 - date from, var == 2 - date to
            int year = dateFrom.getValue().getYear();
            int month = dateFrom.getValue().getMonthValue();
            int date = dateFrom.getValue().getDayOfMonth();
            int hour = Integer.parseInt(dateFromH.getText());
            int minute = Integer.parseInt(dateFromM.getText());
            int second = Integer.parseInt(dateFromS.getText());

            calendar = new GregorianCalendar(); ///.set(year, month, date, hour, minute, second);
            calendar.set(year, month - 1, date, hour, minute, second);
            date1 = calendar.getTime();

            year = dateTo.getValue().getYear();
            month = dateTo.getValue().getMonthValue();
            date = dateTo.getValue().getDayOfMonth();
            hour = Integer.parseInt(dateToH.getText());
            minute = Integer.parseInt(dateToM.getText());
            second = Integer.parseInt(dateToS.getText());

            calendar.set(year, month - 1, date, hour, minute, second);
            date2 = calendar.getTime();

        }

        else {
            calendar = new GregorianCalendar();
            date2 = calendar.getTime();

            Long timeInMillis = calendar.getTimeInMillis();

            timeInMillis = timeInMillis - MainClass.countMinutesPerAutoGraphic * 60 * 1000;

            calendar.setTimeInMillis(timeInMillis);

            date1 = calendar.getTime();
        }

        dbReader.setupTimeStamps(date1, date2);

        dbReader.prepareChartData(chartData);

        fillingChartsData(chartData);

    }

    private void fillingChartsData(HashMap<String, HashMap<String, HashMap<Date, Float>>> chartData) {
        String colTime = MainClass.colTime;
        String colTemp = MainClass.colTemp;
        String colLoad = MainClass.colLoad;
        String colSpeed = MainClass.colSpeed;
        String colCpu = MainClass.colCpu;
        String core = MainClass.core;

        HashMap<String, HashMap<Date, Float>> charttemp = chartData.get(colTemp);

        HashMap<String, HashMap<Date, Float>> chartload = chartData.get(colLoad);

        HashMap<String, HashMap<Date, Float>> chartspeed = chartData.get(colSpeed);

        clearGraphics();

        if (charttemp != null) {
            for (int i = 0; i < 1; i++) {
                HashMap<Date, Float> coreMap = charttemp.get(core + i + colTemp);
                if (coreMap.size() > 0) {
                    XYChart.Series series = new XYChart.Series();

                    series.setName("core" + i + colTemp);

                    fillForOneCore(core + i + colTemp, coreMap, series, graphicTemp);
                }
            }
        }

        if (chartload != null) {
            for (int i = 0; i < MainClass.countOfCores; i++) {
                HashMap<Date, Float> coreMap = chartload.get(core + i + colLoad);
                if (coreMap.size() > 0) {
                    XYChart.Series series = new XYChart.Series();

                    series.setName("core" + i + colLoad);

                    fillForOneCore(core + i + colLoad, coreMap, series, graphicLoad);
                }
            }
        }

        if (chartspeed != null) {
            for (int i = 0; i < MainClass.countOfCores; i++) {
                HashMap<Date, Float> coreMap = chartspeed.get(core + i + colSpeed);
                if (coreMap.size() > 0) {
                    XYChart.Series series = new XYChart.Series();

                    series.setName("core" + i + colSpeed);

                    fillForOneCore(core + i + colSpeed, coreMap, series, graphicSpeed);
                }
            }
        }

    }

    private void fillForOneCore(String s, HashMap<Date, Float> coreMap, XYChart.Series series, LineChart chart) {

        String dateSer = "";

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        Iterator<Map.Entry<Date, Float>> iterator = coreMap.entrySet().iterator();

        Set<Date> set = coreMap.keySet();

        Date[] arrDate = set.toArray(new Date[0]);

        Arrays.sort(arrDate);

        Float var;

        for (Date d : arrDate) {
            var = coreMap.get(d);
            dateSer = sdf.format(d);
            series.getData().add(new XYChart.Data(dateSer, var));
        }

        chart.getData().add(series);

    }

    private String validateTimeFieldValue(String s, String variant) {

        if (s.length() == 0) return "00";

        if (s.length() >2){
            s = s.substring(0, 2);
        }

        if (variant.equals("M") || variant.equals("S")) {

//            if (s.length() > 2) return "59";

            Integer a = Integer.parseInt(s);

            if (a < 0) {
                return "0";
            }

            if (a > 59) {
                return "59";
            }

        } else {
            if (variant.equals("H")) {

//                if (s.length() > 2) return "23";

                Integer a = Integer.parseInt(s);

                if (a < 0) {
                    return "0";
                }

                if (a > 23) {
                    return "23";
                }
            }
        }

        return s;
    }
    private void waitTillTheEnd() {

        //MainClass.writeToLog("waitTillTheEnd #1 done:" + MainClass.done); //D-D

        if (MainClass.done) {
            MainClass.writeToLog("Done");
            textLog.setText(MainClass.log);
            textLog.forward();
//            System.out.println("I don't wanted to die!!!");
            tl.stop();
        }

        textLog.setText(MainClass.log);
        textLog.forward();

        //MainClass.writeToLog("waitTillTheEnd #2 done:" + MainClass.done); //D-D
        if (!MainClass.done) {
            tl.playFromStart();
        }

    }

    private void autoRefresh() {

        if (!MainClass.auto) {
            refresh.stop();
        }

        refreshLinearChartAuto();

        if (MainClass.auto) {
            refresh.playFromStart();
        }

    }

    private void autoParse() {

        MainClass.writeToLog("autoParse #1 MainClassAuto: " + MainClass.auto); //D-D

        if (!MainClass.auto) {
            parsing.stop();
        }

        MainClass.writeToLog("autoParse #2 MainClassDone: " + MainClass.done); //D-D

        if (MainClass.done) {
            toParce();
        }
        else {
            MainClass.writeToLog("I can't parse because mainClass.done = false");
        }

        MainClass.writeToLog("autoParse #3 MainClassAuto: " + MainClass.auto); //D-D

        if (MainClass.auto) {
            parsing.playFromStart();
        }

    }

}