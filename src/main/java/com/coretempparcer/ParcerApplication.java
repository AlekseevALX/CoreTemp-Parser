package com.coretempparcer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class ParcerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(ParcerApplication.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 700);
        stage.setTitle("CoreTemp Parcer!" + " ver: " + MainClass.getVer());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        if (!MainClass.isPropertiesLoaded()) MainClass.loadProperties();

        launch();
    }


}