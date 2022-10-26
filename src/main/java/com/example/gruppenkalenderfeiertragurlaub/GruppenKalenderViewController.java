package com.example.gruppenkalenderfeiertragurlaub;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GruppenKalenderViewController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}