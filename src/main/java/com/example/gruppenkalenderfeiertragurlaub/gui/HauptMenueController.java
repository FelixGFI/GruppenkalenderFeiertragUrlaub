package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.HauptmenueApp;
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

    Stage stage;

    public void initialize() {

    }
    @FXML
    protected void onBtAnwesneheitClick() {
        System.out.println("Klick Anwesneheit");
    }

    @FXML
    protected void onBtKalenderplanungClick() {
        openSubwindowFromButtonClick(stage, "Kalenderplanung","gruppenKalenderView.fxml");
    }

    @FXML
    protected void onBtKuechenplanungClick() {
        openSubwindowFromButtonClick(stage, "Küchenplanung", "kuechenKalenderView.fxml");
    }

    @FXML
    protected void onBtBetriebsurlaubsplanungClick() {
        openSubwindowFromButtonClick(stage, "Betriebsurlaubsplanung", "betriebsurlaubView.fxml");
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

    /**
     * öffnet ein neues Fenster als subfenster des Hauptmenüs, aus dem fxmlFile dessen Pfad als String übergeben wurde.
     * das Neu geöffnete Fenster muss erst geschlossen werden bis wieder mit dem Hauptmenü fenster interagiert werden kann
     * @param titel
     * @param fxmlReccouse
     */
    private void openSubwindowFromButtonClick(Stage parentStage, String titel, String fxmlReccouse) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlReccouse));
        Scene newScene;
        try {
            newScene = new Scene(loader.load());
        } catch (IOException ex) {
            // TODO: handle error
            return;
        }
        Stage inputStage = new Stage();
        inputStage.initOwner(parentStage);
        inputStage.setScene(newScene);
        inputStage.setTitle(titel);
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.showAndWait();
    }

    /**
     * zeigt das Hauptmenü Fenster als neues Fenster an. Initialisiert und speichert die stage des Hauptfensters (von welchem diese Klasse
     * die Controller Classe ist) als globale Variable um diese später als parent für Subfenster zu verwenden.
     * @throws IOException
     */
    public void showDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HauptmenueApp.class.getResource("UeberMenueView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = new Stage();
        stage.setTitle("Hauptmenü");
        stage.setScene(scene);
        stage.show();
    }
}
