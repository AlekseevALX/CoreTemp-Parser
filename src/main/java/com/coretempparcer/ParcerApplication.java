package com.coretempparcer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ParcerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ParcerApplication.class.getResource("view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1500, 600);
        stage.setTitle("CoreTemp Parcer!");
        scene.getStylesheets().add(this.getClass().getResource("src/main/java/styles/1.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}