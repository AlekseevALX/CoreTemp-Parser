package com.coretempparcer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class AppController {
    @FXML
    private Label welcomeText;
    @FXML
    private TextField filepath;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to the jungle!!");
        String[] args = new String[1];
        args[0] = filepath.getText();
        MainClass.main(args);
//        welcomeText.setText("Finish!");
    }
}