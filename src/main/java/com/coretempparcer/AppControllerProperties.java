//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.coretempparcer;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.Map.Entry;

public class AppControllerProperties implements Initializable {

    @FXML
    private TextField urlDB;
    @FXML
    private TextField loginDB;
    @FXML
    private TextField passwordDB;
    @FXML
    private TextField directoryWithCTLogs;
    @FXML
    private TextField colTime;
    @FXML
    private TextField colTemp;
    @FXML
    private TextField colLoad;
    @FXML
    private TextField colSpeed;
    @FXML
    private TextField colCpu;
    @FXML
    private TextField core;
    @FXML
    private TextField countOfCharPoint;
    @FXML
    private TextField countMinutesPerAutoGraphic;
    @FXML
    private TextField maxParcingThreads;
    @FXML
    private TextField tableName;

    public AppControllerProperties() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HashMap<String, String> prop = MainClass.getAppProperties();

        urlDB.setText(prop.get("urlDB"));
        loginDB.setText(prop.get("loginDB"));
        passwordDB.setText(prop.get("passwordDB"));
        directoryWithCTLogs.setText(prop.get("directoryWithCTLogs"));
        colTime.setText(prop.get("colTime"));
        colTemp.setText(prop.get("colTemp"));
        colLoad.setText(prop.get("colLoad"));
        colSpeed.setText(prop.get("colSpeed"));
        colCpu.setText(prop.get("colCpu"));
        core.setText(prop.get("core"));
        countOfCharPoint.setText(prop.get("countOfCharPoint"));
        countMinutesPerAutoGraphic.setText(prop.get("countMinutesPerAutoGraphic"));
        maxParcingThreads.setText(prop.get("maxParcingThreads"));
        tableName.setText(prop.get("tableName"));
    }

    public void onMouseClickedAcceptButton() throws IOException {
        HashMap<String, String> prop = MainClass.getAppProperties();

        prop.put("urlDB", urlDB.getText());
        prop.put("loginDB", loginDB.getText());
        prop.put("passwordDB", passwordDB.getText());
        prop.put("directoryWithCTLogs", directoryWithCTLogs.getText());
        prop.put("colTime", colTime.getText());
        prop.put("colTemp", colTemp.getText());
        prop.put("colLoad", colLoad.getText());
        prop.put("colSpeed", colSpeed.getText());
        prop.put("colCpu", colCpu.getText());
        prop.put("core", core.getText());
        prop.put("countOfCharPoint", countOfCharPoint.getText());
        prop.put("countMinutesPerAutoGraphic", countMinutesPerAutoGraphic.getText());
        prop.put("maxParcingThreads", maxParcingThreads.getText());
        prop.put("tableName", tableName.getText());

        MainClass.saveProperties();
    }
}
