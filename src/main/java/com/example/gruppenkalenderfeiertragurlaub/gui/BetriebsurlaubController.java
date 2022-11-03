package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.ArrayListStorage;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.ComboboxConfigurater;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.TableConfigurator;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.BetriebsurlaubsTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    @FXML TableColumn<BetriebsurlaubsTag, LocalDate> tcDatum;

    @FXML TableColumn<BetriebsurlaubsTag, Boolean> tcIstBetriebsurlaub;

    @FXML TableView<BetriebsurlaubsTag> tbTabelle;

    final ArrayListStorage arrayListStorage = new ArrayListStorage();

    public void initialize() {

        configureTableView();

        tbTabelle.getItems().add(new BetriebsurlaubsTag(LocalDate.now(), false));
        tbTabelle.getItems().add(new BetriebsurlaubsTag(LocalDate.now().minusMonths(1), true));

        ComboboxConfigurater comboboxConfigurater = new ComboboxConfigurater();
        comboboxConfigurater.configureCBJahrAuswahl(comboBoxJahrAuswahl);
        comboboxConfigurater.configureCBMonatAuswahl(comboBoxMonatAuswahl);
    }

    private void configureTableView() {
        //Wichtig! FXML object (wie table Colums, Table View, Buttons etc. Nicht neu initialisieren/überschreiben
        //Weil das FXML object im code ja schon ein UI element referenziert.

        TableConfigurator tableConfigurator = new TableConfigurator();
        tableConfigurator.configureBooleanTableColum(tcIstBetriebsurlaub, "isBetriebsurlaub");
        tableConfigurator.configureLocalDateTableColum(tcDatum, "datum");
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
        try {
            System.out.println(tbTabelle.getSelectionModel().getSelectedItem().getIsBetriebsurlaub());
            System.out.println(tbTabelle.getSelectionModel().getSelectedItem().getDatum());
        } catch (Exception e) {

        }

        tbTabelle.getItems().add(new BetriebsurlaubsTag(LocalDate.now().plusDays(5), true));
        System.out.println("Klick Speichern");
    }
    @FXML protected void onBtUebernehmenClick() {
        System.out.println("Klick Übernehmen");
    }
}
