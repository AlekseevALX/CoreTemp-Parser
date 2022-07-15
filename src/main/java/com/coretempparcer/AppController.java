package com.coretempparcer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class AppController {
    @FXML
    private Label welcomeText;
    @FXML
    private TextField filepath;
    @FXML
    LineChart graphicView;

    Timeline tl;

    @FXML
    protected void onStartButtonClick() {
        welcomeText.setText("Welcome to the jungle!!");
        String[] args = new String[1];
        args[0] = filepath.getText();

        MainClass mainObject = new MainClass();

        MainClass.done = false;

        mainObject.main(args);

        KeyFrame kf = new KeyFrame(Duration.millis(100), actionEvent -> waitTillTheEnd());
        tl = new Timeline(kf);
        tl.setCycleCount(5000);
        tl.play();

    }

   private void waitTillTheEnd(){
        if (MainClass.done){
            welcomeText.setText("Done");
//            System.out.println("I don't wanted to die!!!");
            tl.stop();
        }
   }


}