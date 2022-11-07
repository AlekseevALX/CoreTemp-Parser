package com.coretempparcer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

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
    LineChart graphicView;

    @FXML
    private TextArea textLog;

    @FXML
    DatePicker dateTo;

    @FXML
    DatePicker dateFrom;

    Timeline tl;

    @FXML
    protected void onStartButtonClick() {

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
    protected void onMouseClickedLineChart() {
        //LineChart creation+
//        final NumberAxis xAxis = new NumberAxis();
//        final NumberAxis yAxis = new NumberAxis();
//        xAxis.setLabel("Number of Month");

//        final LineChart<Number,Number> lineChart =
//                new LineChart<Number,Number>(xAxis,yAxis);


//        graphicView.setTitle("Core temp");
        XYChart.Series series = new XYChart.Series();
        series.setName("CPU1");
        //populating the series with data
        series.getData().add(new XYChart.Data(1, 32));
        series.getData().add(new XYChart.Data(2, 25));
        series.getData().add(new XYChart.Data(3, 15));
        series.getData().add(new XYChart.Data(4, 24));
        series.getData().add(new XYChart.Data(5, 34));
        series.getData().add(new XYChart.Data(6, 36));
        series.getData().add(new XYChart.Data(7, 22));
        series.getData().add(new XYChart.Data(8, 45));
        series.getData().add(new XYChart.Data(9, 43));
        series.getData().add(new XYChart.Data(10, 17));
        series.getData().add(new XYChart.Data(11, 29));
        series.getData().add(new XYChart.Data(12, 25));
        series.getData().add(new XYChart.Data(13, 46));

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("CPU2");
        //populating the series with data
        series1.getData().add(new XYChart.Data(1, 26));
        series1.getData().add(new XYChart.Data(2, 35));
        series1.getData().add(new XYChart.Data(3, 48));
        series1.getData().add(new XYChart.Data(4, 59));
        series1.getData().add(new XYChart.Data(5, 64));
        series1.getData().add(new XYChart.Data(6, 79));
        series1.getData().add(new XYChart.Data(7, 57));
        series1.getData().add(new XYChart.Data(8, 44));
        series1.getData().add(new XYChart.Data(9, 40));
        series1.getData().add(new XYChart.Data(10, 34));
        series1.getData().add(new XYChart.Data(11, 22));
        series1.getData().add(new XYChart.Data(12, 15));
        series1.getData().add(new XYChart.Data(13, 9));

        graphicView.getData().add(series);
        graphicView.getData().add(series1);
        //LineChart creation-
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
    protected void mainOKButtonClicked(){
        String a = "dg"; //DEBUG

        refreshLinearChartToPeriod();
    }

    @FXML
    protected void onDeleteBaseButtonClick(){
        MainClass.log = "";
        MainClass.deleteBase();
        textLog.setText(MainClass.log);
        textLog.forward();
    }

    public void refreshLinearChartToPeriod() {
        HashMap<String, Float> chartData = new HashMap<>();

        refreshLinearChart(true);

    }

    public void refreshLinearChart(boolean fixedPeriod) {
        HashMap<String, Float> chartData = new HashMap<>();

        if (fixedPeriod){
            //var == 1 - date from, var == 2 - date to
            int year = dateFrom.getValue().getYear();
            int month = dateFrom.getValue().getMonthValue();
            int date = dateFrom.getValue().getDayOfMonth();
            int hour = Integer.parseInt(dateFromH.getText());
            int minute = Integer.parseInt(dateFromM.getText());
            int second = Integer.parseInt(dateFromS.getText());

            GregorianCalendar calendar = new GregorianCalendar(); ///.set(year, month, date, hour, minute, second);
            calendar.set(year, month - 1, date, hour, minute, second);
            Date dateFrom = calendar.getTime();

            year = dateTo.getValue().getYear();
            month = dateTo.getValue().getMonthValue();
            date = dateTo.getValue().getDayOfMonth();
            hour = Integer.parseInt(dateToH.getText());
            minute = Integer.parseInt(dateToM.getText());
            second = Integer.parseInt(dateToS.getText());

            calendar.set(year, month - 1, date, hour, minute, second);
            Date dateTo = calendar.getTime();

            dbReader.setupTimeStamps(dateFrom, dateTo);

            chartData = dbReader.prepareChartData();
        }



    }

    private String validateTimeFieldValue(String s, String variant) {

        if (s.length() == 0) return "";

        if (variant.equals("M") || variant.equals("S")) {

            if (s.length() > 2) return "59";

            Integer a = Integer.parseInt(s);

            if (a < 0) {
                return "0";
            }

            if (a > 59) {
                return "59";
            }
        } else {
            if (variant.equals("H")) {
                if (s.length() > 2) return "23";

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
        if (MainClass.done) {
            MainClass.writeToLog("Done");
            textLog.setText(MainClass.log);
            textLog.forward();
//            System.out.println("I don't wanted to die!!!");
            tl.stop();
        }

        textLog.setText(MainClass.log);
        textLog.forward();

        if (!MainClass.done) {
            tl.playFromStart();
        }


    }


}