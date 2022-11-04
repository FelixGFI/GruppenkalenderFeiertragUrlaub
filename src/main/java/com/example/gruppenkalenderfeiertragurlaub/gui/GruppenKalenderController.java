package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.ArrayListStorage;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.ComboboxConfigurator;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.TableColumnConfigurator;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenKalenderTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class GruppenKalenderController {

    @FXML Button btSpeichern;
    @FXML Button btAbbrechen;
    @FXML Button btUebernehmen;
    @FXML Button btVorherigerMonat;
    @FXML Button btNaechsterMonat;

    @FXML ComboBox<String> comboBoxMonatAuswahl;
    @FXML ComboBox<Integer> comboBoxJahrAuswahl;
    @FXML ComboBox<String> comboBoxGruppenAuswahl;
    @FXML ComboBox<Character> comboBoxStatusAuswahl;

    @FXML DatePicker dpVon;
    @FXML DatePicker dpBis;

    @FXML TableView<GruppenKalenderTag> tbTabelle;

    @FXML TableColumn<GruppenKalenderTag, LocalDate> tcDatum;
    @FXML TableColumn<GruppenKalenderTag, Character> tcGruppenStatus;
    @FXML TableColumn<GruppenKalenderTag, Boolean> tcEssenVerfuegbar;


    final ArrayListStorage arrayListStorage = new ArrayListStorage();
    public void initialize() {

        configureTableView();

        tbTabelle.getItems().add(new GruppenKalenderTag(1, LocalDate.now(), 'B', false));
        tbTabelle.getItems().add(new GruppenKalenderTag(1, LocalDate.now().minusDays(5), 'O', false));
        tbTabelle.getItems().add(new GruppenKalenderTag(2, LocalDate.now(), 'P', true));
        tbTabelle.getItems().add(new GruppenKalenderTag(3, LocalDate.now().plusDays(1), 'U', false));
        tbTabelle.getItems().add(new GruppenKalenderTag(3,LocalDate.now().plusDays(2), 'A', false));

        ComboboxConfigurator comboboxConfigurator = new ComboboxConfigurator();
        comboboxConfigurator.configureCBMonatAuswahl(comboBoxMonatAuswahl);
        comboboxConfigurator.configureCBJahrAuswahl(comboBoxJahrAuswahl);

        comboboxConfigurator.configureCBStatusauswahl(comboBoxStatusAuswahl);

    }
    private void configureTableView() {
        TableColumnConfigurator tableConfigurator = new TableColumnConfigurator();
        tableConfigurator.configureBooleanTableColum(tcEssenVerfuegbar, "essenFuerGruppeVerfuegbar");
        tableConfigurator.configureLocalDateTableColum(tcDatum, "datum");
        tableConfigurator.configureGruppenStatusTableColum(tcGruppenStatus, "gruppenstatus");
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

        System.out.println("Klick Speichern");
    }

    @FXML protected void onBtUebernehmenClick() {
        System.out.println("Klick Übernehmen");
    }
}