package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.HauptmenueApp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GruppenKalenderApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        showDialog();
    }
    public static void showDialog() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HauptmenueApp.class.getResource("gui/gruppenKalenderView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Hauptmenü");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
