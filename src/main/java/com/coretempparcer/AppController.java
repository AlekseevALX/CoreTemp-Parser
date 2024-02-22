//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.coretempparcer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;


public class AppController implements Initializable {
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
    private ChoiceBox compChoice;
    @FXML
    private Label thisCompName;
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

    private static String choosenComputer;
    private static HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartData = new HashMap();
    private static boolean chartDataIsDefined = false;

    private static String computerName = MainClass.getComputerName();
    Timeline tl;
    Timeline refresh;

    public AppController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setInitialFormValues();
        refillForm();
    }

    public void refillForm() {
        MainClass.setLogging(true);
        thisCompName.setText(computerName);

        if (!MainClass.dbChecked) {
            MainClass.checkDB();
        }

        ArrayList<String> computersInDB = new ArrayList<>();

        try {
            computersInDB = dbReader.findComputersInDB();
        } catch (SQLException e) {
            System.out.println("Failed trying to find any other computers in DB!");
        }

        if (!computersInDB.contains(computerName)) {
            computersInDB.add(computerName);
        }

        ObservableList<String> oList = FXCollections.observableArrayList(computersInDB);

        compChoice.setItems(oList);
    }

    public void setInitialFormValues() {
        dateFromH.setText("00");
        dateFromM.setText("00");
        dateFromS.setText("00");

        dateToH.setText("00");
        dateToM.setText("00");
        dateToS.setText("00");
    }

    public static boolean isChartDataIsDefined() {
        return chartDataIsDefined;
    }

    public static void setChartDataIsDefined(boolean chartDataIsDefined) {
        AppController.chartDataIsDefined = chartDataIsDefined;
    }

    @FXML
    protected void onStartButtonClick() throws IOException {
        this.toParce();
    }

    public void onDefinePropertiesButtonClick() throws IOException {
        PropertiesApplication propertiesApplication = new PropertiesApplication();
        Stage stage = new Stage();
        propertiesApplication.start(stage);
    }

    public void onStopButtonClick() {
        MainClass.done = true;
        MainClass.auto = false;
        MainClass.countOfThreads = 0;
        MainClass.currentWorkingThread = 0;
        GregorianCalendar cal = new GregorianCalendar();

        MainClass.addToLog("User stopped the process " + cal.getTime());
    }

    @FXML
    protected void autoButtonClicked() {

        if (!checkChoosenCompName()) return;

        if (this.autoButton.isSelected()) {
            MainClass.setLogging(true);
            MainClass.auto = true;
            MainClass.log = "";
            MainClass.addToLog("Welcome to the jungle!!");
            MainClass mainClass = new MainClass();
            mainClass.autoParcing();

            KeyFrame kfRefresh;
            if (MainClass.auto) {
                kfRefresh = new KeyFrame(Duration.millis(1000.0D),
                        (actionEvent) -> {
                            this.autoRefresh();
                        },
                        new KeyValue[0]);
                this.refresh = new Timeline(new KeyFrame[]{kfRefresh});
                this.refresh.setCycleCount(1);
                this.refresh.play();
            }
        } else {
            MainClass.auto = false;
        }

    }

    protected void toParce() {
        if (!MainClass.done) return;

        MainClass.log = "";
        MainClass.addToLog("Welcome to the jungle!!");
        MainClass mainObject = new MainClass();
        MainClass.countOfThreads = 0;
        mainObject.startParceSession(false);
        KeyFrame kf = new KeyFrame(Duration.millis(100.0D), (actionEvent) -> {
            this.waitTillTheEnd();
        }, new KeyValue[0]);
        this.tl = new Timeline(new KeyFrame[]{kf});
        this.tl.setCycleCount(1);
        this.tl.play();
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
        if (!checkChoosenCompName()) {
            return;
        }

        this.refreshLinearChartToPeriod(compChoice.getValue().toString());
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

        refillForm();
    }

    public void refreshLinearChartToPeriod(String compName) {
        this.refreshLinearChart(true, compName);
    }

    public void refreshLinearChartAuto(String compName) {
        this.refreshLinearChart(false, compName);
    }

    public void refreshLinearChart(boolean fixedPeriod, String compName) {

        GregorianCalendar calendar;
        Date date1;
        Date date2;

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
            Integer countMinutesPerAutoGraphic = MainClass.getCountMinutesPerAutoGraphic();
            calendar = new GregorianCalendar();
            date2 = calendar.getTime();
            Long timeInMillis = calendar.getTimeInMillis();
            timeInMillis = timeInMillis - (long) (countMinutesPerAutoGraphic * 60 * 1000);
            calendar.setTimeInMillis(timeInMillis);
            date1 = calendar.getTime();
        }

        this.dbReader.setupTimeStamps(date1, date2);
        this.dbReader.prepareChartData(chartData, compName);
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
        Float value;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        for (Map.Entry<Date, Float> entry : coreMap.entrySet()) {
            dateSer = sdf.format(entry.getKey());
            value = entry.getValue();
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
        if (MainClass.done && !MainClass.auto) {
            MainClass.addToLog("Done");
            this.textLog.appendText(MainClass.log);
            MainClass.clearLog();
            refillForm();
            this.tl.stop();
        }

        this.textLog.appendText(MainClass.log);
        MainClass.clearLog();

        if (!MainClass.done) {
            this.tl.playFromStart();
        }

    }

    private void autoRefresh() {

        if (MainClass.done && !MainClass.auto) {
            MainClass.addToLog("Done");
            this.textLog.appendText(MainClass.log);
            MainClass.clearLog();
            this.refresh.stop();
        } else {
            checkChoosenCompName();
            this.refreshLinearChartAuto(compChoice.getValue().toString());

            this.textLog.appendText(MainClass.log);
            MainClass.clearLog();

            this.refresh.playFromStart();
        }

    }

    private boolean checkChoosenCompName() {
        if (compChoice.getValue() == null) {
            MainClass.addToLog("Choose the computer!");
            this.textLog.appendText(MainClass.log);
            MainClass.clearLog();
            return false;
        }

        if (choosenComputer == null || choosenComputer.equals("")) {
            choosenComputer = compChoice.getValue().toString();
        }

        if (!choosenComputer.equals(compChoice.getValue().toString())) {
            chartData = new HashMap<>();
            setChartDataIsDefined(false);
            choosenComputer = compChoice.getValue().toString();
        }
        return true;
    }

//    private void autoParse() throws IOException {
//        MainClass.addToLog("autoParse #1 MainClassAuto: " + MainClass.auto);
//        if (!MainClass.auto) {
//            this.parsing.stop();
//        }
//
//        MainClass.addToLog("autoParse #2 MainClassDone: " + MainClass.done);
//        if (MainClass.done) {
//            this.toParce();
//        } else {
//            MainClass.addToLog("I can't parse because mainClass.done = false"); //HERE
//        }
//
//        MainClass.addToLog("autoParse #3 MainClassAuto: " + MainClass.auto);
//        if (MainClass.auto) {
//            this.parsing.playFromStart();
//        }
//
//    }


}
