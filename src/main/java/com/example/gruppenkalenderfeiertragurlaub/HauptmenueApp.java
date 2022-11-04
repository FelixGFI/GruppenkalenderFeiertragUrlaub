package com.example.gruppenkalenderfeiertragurlaub;

import com.example.gruppenkalenderfeiertragurlaub.gui.HauptMenueController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class HauptmenueApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        HauptMenueController umc = new HauptMenueController();
        umc.showDialog();
    }

    public static void main(String[] args) {
        launch();
    }

}