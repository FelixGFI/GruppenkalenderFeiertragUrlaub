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
        /*FXMLLoader fxmlLoader = new FXMLLoader(HauptmenueApp.class.getResource("UeberMenueView.fxml)"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hauptmenü");
        stage.setScene(scene);
        stage.show();*/
    }

    public static void main(String[] args) {
        launch();
    }
    /*public GruppenKalenderApp(String titel, String reccourse) {
        this.titel = titel;
        this.reccourse = reccourse;
    }*/

}