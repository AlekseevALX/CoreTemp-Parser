//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.coretempparcer;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.Map.Entry;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.util.Pair;
import org.apache.commons.csv.*;


public class AppController {
    DBReader dbReader = new DBReader();
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
    LineChart graphicPower;
    @FXML
    private TextArea textLog;
    @FXML
    DatePicker dateTo;
    @FXML
    DatePicker dateFrom;
    @FXML
    ToggleButton autoButton;
    private static HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartData = new HashMap();
    private static boolean chartDataIsDefined = false;
    Timeline tl;
    Timeline refresh;
    Timeline parsing;

    public static HashMap<String, HashMap<String, SortedMap<Date, Float>>> getChartData() {
        return chartData;
    }

    public static boolean isChartDataIsDefined() {
        return chartDataIsDefined;
    }

    public static void setChartDataIsDefined(boolean chartDataIsDefined) {
        AppController.chartDataIsDefined = chartDataIsDefined;
    }

    public AppController() {
    }

    @FXML
    protected void onStartButtonClick() throws IOException {
        this.toParce();
    }

    protected void toParce() throws IOException {
        MainClass.log = "";
        MainClass.addToLog("Welcome to the jungle!!");
        String dir = MainClass.getDirectoryWithCTLogs();
        String[] args = new String[]{dir};
        MainClass mainObject = new MainClass();
        MainClass.done = false;
        MainClass.countOfThreads = 0;
        mainObject.main(args);
        KeyFrame kf = new KeyFrame(Duration.millis(100.0D), (actionEvent) -> {
            this.waitTillTheEnd();
        }, new KeyValue[0]);
        this.tl = new Timeline(new KeyFrame[]{kf});
        this.tl.setCycleCount(1);
        this.tl.play();
    }

    @FXML
    protected void autoButtonClicked() {
        MainClass.auto = this.autoButton.isSelected();
        KeyFrame kfParse;
        if (MainClass.auto) {
            kfParse = new KeyFrame(Duration.millis(5000.0D), (actionEvent) -> {
                this.autoRefresh();
            }, new KeyValue[0]);
            this.refresh = new Timeline(new KeyFrame[]{kfParse});
            this.refresh.setCycleCount(1);
            this.refresh.play();
        }

        if (MainClass.auto) {
            kfParse = new KeyFrame(Duration.millis(5000.0D), (actionEvent) -> {
                try {
                    this.autoParse();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, new KeyValue[0]);
            this.parsing = new Timeline(new KeyFrame[]{kfParse});
            this.parsing.setCycleCount(1);
            this.parsing.play();
        }

    }

    @FXML
    protected void DateFromHOnKeyReleased() {
        String val = this.dateFromH.getText();
        String res = this.validateTimeFieldValue(val, "H");
        if (!val.equals(res)) {
            this.dateFromH.setText(res);
        }

    }

    @FXML
    protected void DateFromMOnKeyReleased() {
        String val = this.dateFromM.getText();
        String res = this.validateTimeFieldValue(val, "M");
        if (!val.equals(res)) {
            this.dateFromM.setText(res);
        }

    }

    @FXML
    protected void DateFromSOnKeyReleased() {
        String val = this.dateFromS.getText();
        String res = this.validateTimeFieldValue(val, "S");
        if (!val.equals(res)) {
            this.dateFromS.setText(res);
        }

    }

    @FXML
    protected void DateToHOnKeyReleased() {
        String val = this.dateToH.getText();
        String res = this.validateTimeFieldValue(val, "H");
        if (!val.equals(res)) {
            this.dateToH.setText(res);
        }

    }

    @FXML
    protected void DateToMOnKeyReleased() {
        String val = this.dateToM.getText();
        String res = this.validateTimeFieldValue(val, "M");
        if (!val.equals(res)) {
            this.dateToM.setText(res);
        }

    }

    @FXML
    protected void DateToSOnKeyReleased() {
        String val = this.dateToS.getText();
        String res = this.validateTimeFieldValue(val, "S");
        if (!val.equals(res)) {
            this.dateToS.setText(res);
        }

    }

    @FXML
    protected void mainOKButtonClicked() {
        this.refreshLinearChartToPeriod();
    }

    @FXML
    protected void mainClearButtonClicked() {
        this.clearGraphics();
    }

    protected void clearGraphics() {
        this.graphicLoad.getData().clear();
        this.graphicSpeed.getData().clear();
        this.graphicTemp.getData().clear();
        this.graphicPower.getData().clear();
    }
    @FXML
    protected void onDeleteBaseButtonClick() {
        MainClass.log = "";
        MainClass.deleteBase();
        this.textLog.setText(MainClass.log);
    }

    public void refreshLinearChartToPeriod() {
        this.refreshLinearChart(true);
    }

    public void refreshLinearChartAuto() {
        this.refreshLinearChart(false);
    }

    public void refreshLinearChart(boolean fixedPeriod) {

        GregorianCalendar calendar;
        Date date1;
        Date date2;
        Integer countMinutesPerAutoGraphic = MainClass.getCountMinutesPerAutoGraphic();
        if (fixedPeriod) {
            int year = (this.dateFrom.getValue()).getYear();
            int month = (this.dateFrom.getValue()).getMonthValue();
            int date = (this.dateFrom.getValue()).getDayOfMonth();
            int hour = Integer.parseInt(this.dateFromH.getText());
            int minute = Integer.parseInt(this.dateFromM.getText());
            int second = Integer.parseInt(this.dateFromS.getText());
            calendar = new GregorianCalendar();
            calendar.set(year, month - 1, date, hour, minute, second);
            date1 = calendar.getTime();
            year = (this.dateTo.getValue()).getYear();
            month = (this.dateTo.getValue()).getMonthValue();
            date = (this.dateTo.getValue()).getDayOfMonth();
            hour = Integer.parseInt(this.dateToH.getText());
            minute = Integer.parseInt(this.dateToM.getText());
            second = Integer.parseInt(this.dateToS.getText());
            calendar.set(year, month - 1, date, hour, minute, second);
            date2 = calendar.getTime();
        } else {
            calendar = new GregorianCalendar();
            date2 = calendar.getTime();
            Long timeInMillis = calendar.getTimeInMillis();
            timeInMillis = timeInMillis - (long) (countMinutesPerAutoGraphic * 60 * 1000);
            calendar.setTimeInMillis(timeInMillis);
            date1 = calendar.getTime();
        }

        this.dbReader.setupTimeStamps(date1, date2);
        this.dbReader.prepareChartData(chartData);
        this.fillingChartsData(chartData);
    }

    private void fillingChartsData(HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartData) {
        String colTemp = MainClass.getColdb_temp();
        String colLoad = MainClass.getColdb_load();
        String colSpeed = MainClass.getColdb_speed();
        String colCPUPower = MainClass.getColdb_cpupower();
        HashMap<String, SortedMap<Date, Float>> chartTemp = chartData.get(colTemp);
        HashMap<String, SortedMap<Date, Float>> chartLoad = chartData.get(colLoad);
        HashMap<String, SortedMap<Date, Float>> chartSpeed = chartData.get(colSpeed);
        HashMap<String, SortedMap<Date, Float>> chartCPUPower = chartData.get(colCPUPower);

        this.clearGraphics();
        int i;
        SortedMap coreMap;
        Series series;

        if (chartTemp != null) {
            for (Map.Entry<String, SortedMap<Date, Float>> entry : chartTemp.entrySet()) {
                coreMap = entry.getValue();
                if (coreMap.size() > 0) {
                    series = new Series();
                    series.setName(entry.getKey());
                    this.fillForOneCore(entry.getKey(), coreMap, series, this.graphicTemp);
                }
            }
        }

        if (chartLoad != null) {
            for (Map.Entry<String, SortedMap<Date, Float>> entry : chartLoad.entrySet()) {
                coreMap = entry.getValue();
                if (coreMap.size() > 0) {
                    series = new Series();
                    series.setName(entry.getKey());
                    this.fillForOneCore(entry.getKey(), coreMap, series, this.graphicLoad);
                }
            }
        }

        if (chartSpeed != null) {
            for (Map.Entry<String, SortedMap<Date, Float>> entry : chartSpeed.entrySet()) {
                coreMap = entry.getValue();
                if (coreMap.size() > 0) {
                    series = new Series();
                    series.setName(entry.getKey());
                    this.fillForOneCore(entry.getKey(), coreMap, series, this.graphicSpeed);
                }
            }
        }

        if (chartCPUPower != null) {
            for (Map.Entry<String, SortedMap<Date, Float>> entry : chartCPUPower.entrySet()) {
                coreMap = entry.getValue();
                if (coreMap.size() > 0) {
                    series = new Series();
                    series.setName(entry.getKey());
                    this.fillForOneCore(entry.getKey(), coreMap, series, this.graphicPower);
                }
            }
        }

    }

    public static void clearChartsData() {
        for (Map.Entry<String, HashMap<String, SortedMap<Date, Float>>> entryCharts : chartData.entrySet()) {

            HashMap<String, SortedMap<Date, Float>> currMapCores = entryCharts.getValue();

            for (Map.Entry<String, SortedMap<Date, Float>> entryCores : currMapCores.entrySet()) {
                SortedMap<Date, Float> currMapOneCore = entryCores.getValue();
                currMapOneCore.clear();
            }
        }
    }

    private void fillForOneCore(String s, SortedMap<Date, Float> coreMap, Series series, LineChart chart) {
        String dateSer;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Iterator<Entry<Date, Float>> iterator = coreMap.entrySet().iterator();
        Set<Date> set = coreMap.keySet();
        Date[] arrDate = set.toArray(new Date[0]);
        Arrays.sort(arrDate);
        Date[] dates = arrDate;
        int length = arrDate.length;

        for (int i = 0; i < length; ++i) {
            Date d = dates[i];
            Float value = coreMap.get(d);
            dateSer = sdf.format(d);
            series.getData().add(new Data(dateSer, value));
        }

        chart.getData().add(series);
    }

    private String validateTimeFieldValue(String s, String variant) {
        if (s.length() == 0) {
            return "00";
        } else {
            if (s.length() > 2) {
                s = s.substring(0, 2);
            }

            Integer a;
            if (!variant.equals("M") && !variant.equals("S")) {
                if (variant.equals("H")) {
                    a = Integer.parseInt(s);
                    if (a < 0) {
                        return "0";
                    }

                    if (a > 23) {
                        return "23";
                    }
                }
            } else {
                a = Integer.parseInt(s);
                if (a < 0) {
                    return "0";
                }

                if (a > 59) {
                    return "59";
                }
            }

            return s;
        }
    }

    private void waitTillTheEnd() {
        if (MainClass.done) {
            MainClass.addToLog("Done");
            this.textLog.appendText(MainClass.log);
            MainClass.clearLog();
            this.tl.stop();
        }

        this.textLog.appendText(MainClass.log);
        MainClass.clearLog();
        if (!MainClass.done) {
            this.tl.playFromStart();
        }

    }

    private void autoRefresh() {
        if (!MainClass.auto) {
            this.refresh.stop();
        }

        this.refreshLinearChartAuto();
        if (MainClass.auto) {
            this.refresh.playFromStart();
        }

    }

    private void autoParse() throws IOException {
        MainClass.addToLog("autoParse #1 MainClassAuto: " + MainClass.auto);
        if (!MainClass.auto) {
            this.parsing.stop();
        }

        MainClass.addToLog("autoParse #2 MainClassDone: " + MainClass.done);
        if (MainClass.done) {
            this.toParce();
        } else {
            MainClass.addToLog("I can't parse because mainClass.done = false"); //HERE
        }

        MainClass.addToLog("autoParse #3 MainClassAuto: " + MainClass.auto);
        if (MainClass.auto) {
            this.parsing.playFromStart();
        }

    }

    public void onDefinePropertiesButtonClick() throws IOException {
        PropertiesApplication propertiesApplication = new PropertiesApplication();
        Stage stage = new Stage();
        propertiesApplication.start(stage);
    }
    public void onStopButtonClick(){
        MainClass.done = true;
        MainClass.auto = false;
        MainClass.countOfThreads = 0;
        MainClass.currentWorkingThread = 0;
        GregorianCalendar cal = new GregorianCalendar();

        MainClass.addToLog("User stopped the process " + cal.getTime());
    }
}
