package com.coretempparcer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.Properties;

public class ParcerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(ParcerApplication.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("CoreTemp Parcer!" + " ver: " + MainClass.getVer());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        defineProperties();

        launch();
    }

    private static void defineProperties() throws IOException {
        String appConfigPath = "C:\\Pet\\CoreTempParcer\\src\\main\\java\\com\\coretempparcer\\properties\\properties.properties";

        Properties properties = new Properties();
        properties.load(new FileInputStream(appConfigPath));

        MainClass.setVer(properties.getProperty("version"));
        MainClass.setUrlDB(properties.getProperty("urlDB"));
        MainClass.setLoginDB(properties.getProperty("loginDB"));
        MainClass.setPasswordDB(properties.getProperty("passwordDB"));
        MainClass.setDirectoryWithCTLogs(properties.getProperty("directoryWithCTLogs"));

    }
    
}