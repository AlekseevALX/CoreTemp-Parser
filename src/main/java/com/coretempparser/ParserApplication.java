package com.coretempparser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class ParserApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(ParserApplication.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 700);
        stage.setTitle("CoreTemp Parser!" + " ver: " + MainClass.getVer());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        if (!MainClass.isPropertiesLoaded()) MainClass.loadProperties();

        launch();
    }


}