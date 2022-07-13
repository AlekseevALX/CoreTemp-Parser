package com.coretempparcer;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AppController {
    @FXML
    private Label welcomeText;
    @FXML
    private TextField filepath;
    @FXML
    LineChart graphicView;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to the jungle!!");
        String[] args = new String[1];
        args[0] = filepath.getText();
        MainClass.main(args);
        waitTillTheEnd();
    }

    protected void waitTillTheEnd(){
        synchronized (AppController.class){
            while (true){
                if (MainClass.done){

                    welcomeText.setText("Done");
                    break;
                }
            }
        }

    }

}