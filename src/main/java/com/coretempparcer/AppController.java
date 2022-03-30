package com.coretempparcer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AppController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to the jungle!!");
        String[] args = new String[1];
        args[0] = "C:\\Pet\\CoreTempTestData";
        MainClass.main(args);
        welcomeText.setText("Finish!");
    }
}