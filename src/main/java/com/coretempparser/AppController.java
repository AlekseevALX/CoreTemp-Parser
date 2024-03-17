//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.coretempparser;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;


public class AppController implements Initializable {

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
    ToggleButton autoParse_btn;

    @FXML
    ToggleButton autoRefresh_btn;

    @FXML
    CheckBox showLog;
    DBReader dbReader = new DBReader();
    ChartsFiller chartsFiller;

    private static String choosenComputer;
    private static HashMap<String, HashMap<String, SortedMap<Date, Float>>> chartData = new HashMap();
    private static boolean chartDataIsDefined = false;

    private static String computerName = MainClass.getComputerName();
    Timeline tl;
    Timeline autoRefreshLineCharts_tl;
    Timeline autoParse_tl;

    public AppController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chartsFiller = new ChartsFiller(graphicTemp, graphicLoad, graphicSpeed, graphicPower, dbReader);
        setInitialFormValues();
        refillForm();
    }

    public void refillForm() {
        MainClass.setLogging(true);
        thisCompName.setText(computerName);
        String choosenComp = "";

        if (compChoice.getValue() != null) {
            choosenComp = compChoice.getValue().toString();
        }

        if (!MainClass.dbChecked) {
            MainClass.checkDB();
        }

        if (!MainClass.dbChecked) {
            System.out.println("Data base isn't checked! Check out the properties!");
            MainClass.addToLog("Data base isn't checked! Check out the properties!");
            return;
        }

        ArrayList<String> computersInDB = new ArrayList<>();

        try {
            computersInDB = dbReader.findComputersInDB();
        } catch (SQLException e) {
            MainClass.addToLog("Failed trying to find any other computers in DB!");
            System.out.println("Failed trying to find any other computers in DB!");
        }

        if (!computersInDB.contains(computerName)) {
            computersInDB.add(computerName);
        }

        ObservableList<String> oList = FXCollections.observableArrayList(computersInDB);

        compChoice.setItems(oList);

        compChoice.setValue(choosenComp);
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
    protected void onStartButtonClick() {
        this.toParse();
    }

    @FXML
    protected void onAutoparseButtonClick() {
        startAutoParse();
    }

    @FXML
    protected void onAutoRefreshButtonClick() {
        startAutoRefresh();
    }

    private void startAutoParse() {

        if (this.autoParse_btn.isSelected()) {
            MainClass.setLogging(showLog.isSelected());
            MainClass.auto = true;
            MainClass.log = "";
            MainClass.addToLog("Welcome to the jungle!!");
            MainClass mainClass = new MainClass();
            mainClass.autoParcing();

            KeyFrame kfAutoParse;
            if (MainClass.auto) {
                kfAutoParse = new KeyFrame(Duration.millis(1000.0D),
                        (actionEvent) -> {
                            this.autoParse();
                        },
                        new KeyValue[0]);
                this.autoParse_tl = new Timeline(new KeyFrame[]{kfAutoParse});
                this.autoParse_tl.setCycleCount(Timeline.INDEFINITE);
                this.autoParse_tl.play();
            }
        } else {
            MainClass.auto = false;
        }
    }

    private void startAutoRefresh() {
        if (!checkChosenCompName()) return;
        if (autoRefresh_btn.isSelected()) {
            KeyFrame kfRefresh;
            kfRefresh = new KeyFrame(Duration.millis(1000.0D),
                    (actionEvent) -> {
                        this.autoRefreshLineCharts();
                    },
                    new KeyValue[0]);
            this.autoRefreshLineCharts_tl = new Timeline(new KeyFrame[]{kfRefresh});
            this.autoRefreshLineCharts_tl.setCycleCount(Timeline.INDEFINITE);
            this.autoRefreshLineCharts_tl.play();
        } else {
            if (this.autoRefreshLineCharts_tl != null)
                this.autoRefreshLineCharts_tl.stop();
        }

    }

    public void onDefinePropertiesButtonClick() throws IOException {
        PropertiesApplication propertiesApplication = new PropertiesApplication();
        Stage stage = new Stage();
        propertiesApplication.start(stage);
    }

    public void showLogSetted() {
        MainClass.setLogging(showLog.isSelected());
        if (!showLog.isSelected()) {
            MainClass.clearLog();
            textLog.clear();
        }
    }

    public void onStopButtonClick() {
        MainClass.done = true;
        MainClass.auto = false;
        MainClass.countOfThreads = 0;
        MainClass.currentWorkingThread = 0;
        GregorianCalendar cal = new GregorianCalendar();

        MainClass.addToLog("User stopped the process " + cal.getTime());
    }

    protected void toParse() {
        if (!MainClass.done) return;
        MainClass.setLogging(showLog.isSelected());
        MainClass.log = "";
        MainClass.addToLog("Welcome to the jungle!!");
        MainClass mainObject = MainClass.getInstance();
        MainClass.countOfThreads = 0;
        mainObject.startParseSession(false);
        KeyFrame kf = new KeyFrame(Duration.millis(100.0D), (actionEvent) -> {
            this.waitTillTheEnd();
        }, new KeyValue[0]);
        this.tl = new Timeline(new KeyFrame[]{kf});
        this.tl.setCycleCount(Timeline.INDEFINITE);
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
        if (!checkChosenCompName()) {
            return;
        }

        this.refreshLinearChartToPeriod(compChoice.getValue().toString());
    }

    @FXML
    protected void mainClearButtonClicked() {
//        this.clearGraphics();
        chartsFiller.clearGraphics();
    }

    @FXML
    protected void onDeleteBaseButtonClick() {
        MainClass.log = "";
        MainClass.deleteBase();
        this.textLog.setText(MainClass.log);

        MainClass.clearCache();
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

        chartsFiller.setupTimeStamps(date1, date2);
        chartsFiller.prepareChartData(chartData, compName);
        chartsFiller.fillingChartsData(chartData);
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

    }

    private void autoRefreshLineCharts() {
        checkChosenCompName();
        this.refreshLinearChartAuto(compChoice.getValue().toString());
    }

    private void autoParse() {

        if (MainClass.done && !MainClass.auto) {

            MainClass.addToLog("Done");
            this.textLog.appendText(MainClass.log);
            MainClass.clearLog();
            this.autoParse_tl.stop();
        } else {

            this.textLog.appendText(MainClass.log);
            MainClass.clearLog();

        }
    }

    private boolean checkChosenCompName() {
        if (compChoice.getValue() == null || compChoice.getValue().equals("")) {
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


}
