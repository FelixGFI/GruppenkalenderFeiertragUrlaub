package com.example.gruppenkalenderfeiertragurlaub;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HauptMenueController {

    @FXML Button btProgrammSchliessen;
    @FXML Button btAnwesenheit;
    @FXML Button btKalenderplanug;
    @FXML Button btKuechenplanung;
    @FXML Button btBetriebsurlaubsplanung;
    @FXML Button btTeilnehmerverwaltung;
    @FXML Button btGruppenverwaltung;
    @FXML Button btForderungenverwaltung;
    @FXML Button btKostenstellenverwaltung;
    @FXML Button btPlatzhalterKnopf1;
    @FXML Button btPlatzhalterKnopf2;
    @FXML Button btPlatzhalterKnopf3;
    @FXML Button btPlatzhalterKnopf4;

    Stage stage = new Stage();

    public void initialize() {

    }
    @FXML
    protected void onBtAnwesneheitClick() {
        System.out.println("Klick Anwesneheit");
    }

    @FXML
    protected void onBtKalenderplanungClick() throws IOException {
        openSubwindowFromButtonClick("Kalenderplanung","gruppenKalenderView.fxml");
    }

    @FXML
    protected void onBtKuechenplanungClick() {
        openSubwindowFromButtonClick("Küchenplanung", "kuechenKalenderView.fxml");
    }

    @FXML
    protected void onBtBetriebsurlaubsplanungClick() {
        openSubwindowFromButtonClick("Betriebsurlaubsplanung", "betriebsurlaubView.fxml");
    }

    @FXML
    protected void onBtTeilnehmerverwaltungClick() {
        System.out.println("Klick Teilnehmerverwaltung");
    }

    @FXML
    protected void onBtGruppenverwaltungClick() {
        System.out.println("Klick Gruppenverwaltung");
    }

    @FXML
    protected void onBtForderungenverwaltungClick() {
        System.out.println("Klick Vorderungenverwaltung");
    }

    @FXML
    protected void onBtKostenstellenverwaltungClick() {
        System.out.println("Klick Kostenstellenverwaltung");
    }

    @FXML
    protected void onBtPlatzhalterKnopf1Click() {
        System.out.println("Klick PlatzhalterKnopf1");
    }

    @FXML
    protected void onBtPlatzhalterKnopf2Click() {
        System.out.println("Klick PlatzhalterKnopf2");
    }

    @FXML
    protected void onBtPlatzhalterKnopf3Click() {
        System.out.println("Klick PlatzhalterKnopf3");
    }

    @FXML
    protected void onBtPlatzhalterKnopf4Click() {
        System.out.println("Klick PlatzhalterKnopf4");
    }

    @FXML
    protected void onBtProgrammSchliessenClick() {
        System.out.println("Klick Programm schließen");
    }

    private void openSubwindowFromButtonClick(String titel, String fxmlReccouse) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlReccouse));
        Scene newScene;
        try {
            newScene = new Scene(loader.load());
        } catch (IOException ex) {
            // TODO: handle error
            return;
        }

        Stage inputStage = new Stage();
        inputStage.initOwner(stage);
        inputStage.setScene(newScene);
        inputStage.setTitle(titel);
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.showAndWait();
    }
    public void showDialog() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HauptmenueApp.class.getResource("UeberMenueView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hauptmenü");
        stage.setScene(scene);
        stage.show();
    }
}
