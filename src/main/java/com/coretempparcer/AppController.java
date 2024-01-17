//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.coretempparcer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import javafx.util.Duration;

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

    public AppController() {
    }

    @FXML
    protected void onStartButtonClick() throws IOException {
        this.toParce();
    }

    protected void toParce() throws IOException {
        MainClass.log = "";
        MainClass.writeToLog("Welcome to the jungle!!");
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
    }

    @FXML
    protected void onDeleteBaseButtonClick() {
        MainClass.log = "";
        MainClass.deleteBase();
        this.textLog.setText(MainClass.log);
        this.textLog.forward();
    }

    public void testButtonClicked() throws IOException {
        String filePath = "C:\\Pet\\test\\test file\\CT-Log 2024-01-15 18-34-12.csv";
        Reader in = new FileReader(filePath);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);

        for (CSVRecord record : records) {
            String columnOne = record.get(0);

            if (columnOne.equals("")) continue;
            String columnTwo = record.get(1);
        }
    }
    public void refreshLinearChartToPeriod() {
        this.refreshLinearChart(true);
    }

    public void refreshLinearChartAuto() {
        this.refreshLinearChart(false);
    }

    public void refreshLinearChart(boolean fixedPeriod) {
        HashMap<String, HashMap<String, HashMap<Date, Float>>> chartData = new HashMap();
        GregorianCalendar calendar;
        Date date1;
        Date date2;
        Integer countMinutesPerAutoGraphic = MainClass.getCountMinutesPerAutoGraphic();
        if (fixedPeriod) {
            int year = ((LocalDate) this.dateFrom.getValue()).getYear();
            int month = ((LocalDate) this.dateFrom.getValue()).getMonthValue();
            int date = ((LocalDate) this.dateFrom.getValue()).getDayOfMonth();
            int hour = Integer.parseInt(this.dateFromH.getText());
            int minute = Integer.parseInt(this.dateFromM.getText());
            int second = Integer.parseInt(this.dateFromS.getText());
            calendar = new GregorianCalendar();
            calendar.set(year, month - 1, date, hour, minute, second);
            date1 = calendar.getTime();
            year = ((LocalDate) this.dateTo.getValue()).getYear();
            month = ((LocalDate) this.dateTo.getValue()).getMonthValue();
            date = ((LocalDate) this.dateTo.getValue()).getDayOfMonth();
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

    private void fillingChartsData(HashMap<String, HashMap<String, HashMap<Date, Float>>> chartData) {
        String colTime = MainClass.getColTime();
        String colTemp = MainClass.getColTemp();
        String colLoad = MainClass.getColLoad();
        String colSpeed = MainClass.getColSpeed();
        String colCpu = MainClass.getColCpu();
        String core = MainClass.getCore();
        int countOfCores = MainClass.getCountOfCores();
        HashMap<String, HashMap<Date, Float>> chartTemp = chartData.get(colTemp);
        HashMap<String, HashMap<Date, Float>> chartLoad = chartData.get(colLoad);
        HashMap<String, HashMap<Date, Float>> chartSpeed = chartData.get(colSpeed);
        this.clearGraphics();
        int i;
        HashMap coreMap;
        Series series;
        if (chartTemp != null) {
            for (i = 0; i < 1; ++i) {
                coreMap = chartTemp.get(core + i + colTemp);
                if (coreMap.size() > 0) {
                    series = new Series();
                    series.setName(core + i + colTemp);
                    this.fillForOneCore(core + i + colTemp, coreMap, series, this.graphicTemp);
                }
            }
        }

        if (chartLoad != null) {
            for (i = 0; i < countOfCores; ++i) {
                coreMap = (HashMap) chartLoad.get(core + i + colLoad);
                if (coreMap.size() > 0) {
                    series = new Series();
                    series.setName(core + i + colLoad);
                    this.fillForOneCore(core + i + colLoad, coreMap, series, this.graphicLoad);
                }
            }
        }

        if (chartSpeed != null) {
            for (i = 0; i < MainClass.getCountOfCores(); ++i) {
                coreMap = chartSpeed.get(core + i + colSpeed);
                if (coreMap.size() > 0) {
                    series = new Series();
                    series.setName(core + i + colSpeed);
                    this.fillForOneCore(core + i + colSpeed, coreMap, series, this.graphicSpeed);
                }
            }
        }

    }

    private void fillForOneCore(String s, HashMap<Date, Float> coreMap, Series series, LineChart chart) {
        String dateSer = "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Iterator<Entry<Date, Float>> iterator = coreMap.entrySet().iterator();
        Set<Date> set = coreMap.keySet();
        Date[] arrDate = (Date[]) set.toArray(new Date[0]);
        Arrays.sort(arrDate);
        Date[] var11 = arrDate;
        int var12 = arrDate.length;

        for (int var13 = 0; var13 < var12; ++var13) {
            Date d = var11[var13];
            Float var = (Float) coreMap.get(d);
            dateSer = sdf.format(d);
            series.getData().add(new Data(dateSer, var));
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
            MainClass.writeToLog("Done");
            this.textLog.setText(MainClass.log);
            this.textLog.forward();
            this.tl.stop();
        }

        this.textLog.setText(MainClass.log);
        this.textLog.forward();
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
        MainClass.writeToLog("autoParse #1 MainClassAuto: " + MainClass.auto);
        if (!MainClass.auto) {
            this.parsing.stop();
        }

        MainClass.writeToLog("autoParse #2 MainClassDone: " + MainClass.done);
        if (MainClass.done) {
            this.toParce();
        } else {
            MainClass.writeToLog("I can't parse because mainClass.done = false");
        }

        MainClass.writeToLog("autoParse #3 MainClassAuto: " + MainClass.auto);
        if (MainClass.auto) {
            this.parsing.playFromStart();
        }

    }

    public void onDefinePropertiesButtonClick(ActionEvent actionEvent) throws IOException {
        PropertiesApplication propertiesApplication = new PropertiesApplication();
        Stage stage = new Stage();
        propertiesApplication.start(stage);
    }
}
