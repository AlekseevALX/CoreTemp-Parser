//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.coretempparcer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class AppControllerProperties implements Initializable {

    @FXML
    private TextField IPDB;
    @FXML
    private TextField DBName;
    @FXML
    private TextField portDB;
    @FXML
    private RadioButton PG;
    @FXML
    private RadioButton MSQL;
    @FXML
    private TextField loginDB;
    @FXML
    private TextField passwordDB;
    @FXML
    private TextField directoryWithCTLogs;
    @FXML
    private TextField db_colTime;
    @FXML
    private TextField db_colTemp;
    @FXML
    private TextField db_colLoad;
    @FXML
    private TextField db_colSpeed;
    @FXML
    private TextField db_colCpu;
    @FXML
    private TextField db_core;
    @FXML
    private TextField db_cpuPower;
    @FXML
    private TextField f_cpuPower;
    @FXML
    private TextField f_colTime;
    @FXML
    private TextField f_colTemp;
    @FXML
    private TextField f_colLoad;
    @FXML
    private TextField f_colSpeed;
    @FXML
    private TextField f_colCpu;
    @FXML
    private TextField f_core;
    @FXML
    private TextField countOfCharPoint;
    @FXML
    private TextField countMinutesPerAutoGraphic;
    @FXML
    private TextField maxParcingThreads;
    @FXML
    private TextField tableName;
    @FXML
    private TitledPane userSettings_tab;
    @FXML
    private TitledPane sysProp_tab;
    @FXML
    private Label checkConnectionResult;

//    double initialYOfUserTab = userSettings_tab.getLayoutY();

    double initialYOfSystemTab;

    public AppControllerProperties() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HashMap<String, String> userProp = MainClass.getUserSettingsMap();
        HashMap<String, String> sysSett = MainClass.getSystemPropertiesMap();
        initialYOfSystemTab = sysProp_tab.getLayoutY();

        //users
        IPDB.setText(userProp.get("IPDB"));
        DBName.setText(userProp.get("DBName"));
        portDB.setText(userProp.get("portDB"));

        try {
            if (userProp.get("typeDB").toUpperCase().equals("PG")) {
                PG.setSelected(true);
                MSQL.setSelected(false);
            } else if (userProp.get("typeDB").toUpperCase().equals("MSQL")) {
                MSQL.setSelected(true);
                PG.setSelected(false);
            }
        } catch (Exception e) {
            //none
        }

        loginDB.setText(userProp.get("loginDB"));
        passwordDB.setText(userProp.get("passwordDB"));
        directoryWithCTLogs.setText(userProp.get("directoryWithCTLogs"));
        countOfCharPoint.setText(userProp.get("countOfCharPoint"));
        countMinutesPerAutoGraphic.setText(userProp.get("countMinutesPerAutoGraphic"));
        maxParcingThreads.setText(userProp.get("maxParcingThreads"));
        tableName.setText(userProp.get("tableName"));

        //system
        db_colTime.setText(sysSett.get("db_time"));
        db_colTemp.setText(sysSett.get("db_temp"));
        db_colLoad.setText(sysSett.get("db_load"));
        db_colSpeed.setText(sysSett.get("db_speed"));
        db_colCpu.setText(sysSett.get("db_cpu"));
        db_core.setText(sysSett.get("db_core"));
        db_cpuPower.setText(sysSett.get("db_cpuPower"));
        f_colTime.setText(sysSett.get("f_time"));
        f_colTemp.setText(sysSett.get("f_temp"));
        f_colLoad.setText(sysSett.get("f_load"));
        f_colSpeed.setText(sysSett.get("f_speed"));
        f_colCpu.setText(sysSett.get("f_cpu"));
        f_core.setText(sysSett.get("f_core"));
        f_cpuPower.setText(sysSett.get("f_cpuPower"));

        checkConnectionResult.setText("unknown");

    }

    public void onMouseClickedPG() {
        PG.setSelected(true);
        MSQL.setSelected(false);
    }

    public void onMouseClickedMSQL() {
        MSQL.setSelected(true);
        PG.setSelected(false);
    }

    public void onMouseClickedAcceptButton() throws IOException {
        HashMap<String, String> userProp = MainClass.getUserSettingsMap();
        HashMap<String, String> sysSett = MainClass.getSystemPropertiesMap();

        userProp.put("IPDB", IPDB.getText());
        userProp.put("DBName", DBName.getText());
        userProp.put("portDB", portDB.getText());
        if (PG.isSelected()) {
            userProp.put("typeDB", "PG");
        } else if (MSQL.isSelected()) {
            userProp.put("typeDB", "MSQL");
        }
        userProp.put("loginDB", loginDB.getText());
        userProp.put("passwordDB", passwordDB.getText());
        userProp.put("directoryWithCTLogs", directoryWithCTLogs.getText());

        sysSett.put("f_time", f_colTime.getText());
        sysSett.put("f_temp", f_colTemp.getText());
        sysSett.put("f_load", f_colLoad.getText());
        sysSett.put("f_speed", f_colSpeed.getText());
        sysSett.put("f_cpu", f_colCpu.getText());
        sysSett.put("f_core", f_core.getText());
        sysSett.put("f_cpuPower", f_cpuPower.getText());

        sysSett.put("db_time", db_colTime.getText());
        sysSett.put("db_temp", db_colTemp.getText());
        sysSett.put("db_load", db_colLoad.getText());
        sysSett.put("db_speed", db_colSpeed.getText());
        sysSett.put("db_cpu", db_colCpu.getText());
        sysSett.put("db_core", db_core.getText());
        sysSett.put("db_cpuPower", db_cpuPower.getText());

        userProp.put("countOfCharPoint", countOfCharPoint.getText());
        userProp.put("countMinutesPerAutoGraphic", countMinutesPerAutoGraphic.getText());
        userProp.put("maxParcingThreads", maxParcingThreads.getText());
        userProp.put("tableName", tableName.getText());

        MainClass.closeConnection();

        MainClass.saveProperties();
        MainClass.loadProperties();
    }

    public void onMouseClickedCheckConnection() {
        if (MainClass.connectionToBase()){
            checkConnectionResult.setText("OK");
        }
        else {
            checkConnectionResult.setText("failed");
        }
    }

    public void onMouseClicked_userSettings_tab() {
        sysProp_tab.setExpanded(false);

        double thisHeight = userSettings_tab.getHeight();
        double thisY = userSettings_tab.getLayoutY();

        if (userSettings_tab.isExpanded()) {
            sysProp_tab.setLayoutY(thisY + thisHeight);
        } else {
            sysProp_tab.setLayoutY(initialYOfSystemTab);
        }

    }

    public void onMouseClicked_sysProp_tab() {
        userSettings_tab.setExpanded(false);

        sysProp_tab.setLayoutY(initialYOfSystemTab);
    }
}
