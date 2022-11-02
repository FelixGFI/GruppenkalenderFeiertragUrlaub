package com.example.gruppenkalenderfeiertragurlaub;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class BetriebsurlaubController {

    @FXML Button btSpeichern;
    @FXML Button btAbbrechen;
    @FXML Button btUebernehmen;
    @FXML Button btVorigerMonat;
    @FXML Button btNaechesterMonat;

    @FXML ComboBox<String> comboBoxMonatAuswahl;
    @FXML ComboBox<Integer> comboBoxJahrAuswahl;

    @FXML DatePicker dpVon;
    @FXML DatePicker dpBis;

    @FXML TableColumn<BetriebsurlaubsTag, String> tcDatum;

    @FXML TableColumn<BetriebsurlaubsTag, String> tcIstBetriebsurlaub;

    @FXML TableView<BetriebsurlaubsTag> tbTabelle;

    String monate[] = { "Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember" };
    Integer jahre[] = {2022, 2023, 2024, 2025, 2026, 2027, 2028, 20229, 2030, 2031, 2032, 2033, 2031, 2034, 2035, 2036, 2037, 2038, 2039, 2040};

 public void initialize() {

        tcDatum.setCellValueFactory(
                new PropertyValueFactory<>("datum"));

        tcIstBetriebsurlaub.setCellValueFactory(
                new PropertyValueFactory<>("isBetriebsurlaub"));

        tbTabelle.getItems().add(
                new BetriebsurlaubsTag("A date", "Nein"));
        tbTabelle.getItems().add(
                new BetriebsurlaubsTag("Another Date", "Ja"));

        //fügt Alle benötigten Items den Comboxboxen Hinzu
        comboBoxMonatAuswahl.getItems().addAll(monate);
        comboBoxJahrAuswahl.getItems().addAll(jahre);

        //Setzt den Akktuellen Monat/das Aktuelle Jahr als Vorauswahl
        comboBoxMonatAuswahl.getSelectionModel().select(LocalDate.now().getMonthValue()-1);
        comboBoxJahrAuswahl.getSelectionModel().select(LocalDate.now().getYear()-jahre[0]);


    }

    @FXML protected void onBtVorherigerMonatClick() {
        System.out.println("Klick Vorheriger Monat");
    }

    @FXML protected void onBtNaechsterMonatClick() {
        System.out.println("Klick Naechster Monat");
    }

    @FXML protected void onBtAbbrechenClick() {
        System.out.println("Klick Abbrechen");
    }

    @FXML protected void onBtSpeichernClick() {
        System.out.println(comboBoxMonatAuswahl.getSelectionModel().getSelectedItem());
        System.out.println("Klick Speichern");
    }
    @FXML protected void onBtUebernehmenClick() {
        System.out.println("Klick Übernehmen");
    }
}
