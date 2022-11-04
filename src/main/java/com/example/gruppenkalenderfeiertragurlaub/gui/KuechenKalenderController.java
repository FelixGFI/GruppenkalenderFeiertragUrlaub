package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.ArrayListStorage;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.ComboboxConfigurator;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.TableColumnConfigurator;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.BetriebsurlaubsTag;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.KuechenKalenderTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class KuechenKalenderController {

    @FXML
    Button btSpeichern;
    @FXML
    Button btAbbrechen;
    @FXML
    Button btGeschlossen;
    @FXML
    Button btOffen;
    @FXML
    Button btNaechsterMonat;
    @FXML
    Button btVorigerMonat;

    @FXML
    ComboBox<String> comboBoxMonatAuswahl;
    @FXML
    ComboBox<Integer> comboBoxJahrAuswahl;

    @FXML
    DatePicker dpVon;
    @FXML
    DatePicker dpBis;

    @FXML TableView<KuechenKalenderTag> tbTabelle;

    @FXML TableColumn<BetriebsurlaubsTag, LocalDate> tcDatum;
    @FXML TableColumn<BetriebsurlaubsTag, Boolean> tcKuecheOffen;

    final ArrayListStorage arrayListStorage = new ArrayListStorage();
    final DatenbankCommunicator datenbankCommunicator = new DatenbankCommunicator();

    public void initialize() {

        configureTableView();

        //TODO boolean connectionEstablished = datenbankCommunicator.establishConnection();

        tbTabelle.getItems().add(new KuechenKalenderTag(LocalDate.now().plusDays(10), true));
        tbTabelle.getItems().add(new KuechenKalenderTag(LocalDate.now(), false));

        ComboboxConfigurator cbConfigurator = new ComboboxConfigurator();
        cbConfigurator.configureCBMonatAuswahl(comboBoxMonatAuswahl);
        cbConfigurator.configureCBJahrAuswahl(comboBoxJahrAuswahl);
    }

    private void configureTableView() {
        TableColumnConfigurator tcConfigurator = new TableColumnConfigurator();
        tcConfigurator.configureBooleanTableColum(tcKuecheOffen, "kuecheGeoeffnet");
        tcConfigurator.configureLocalDateTableColum(tcDatum, "datum");
    }

    @FXML
    protected void onBtVorherigerMonatClick() {
        System.out.println("Klick Vorheriger Monat");
    }

    @FXML
    protected void onBtNaechsterMonatClick() {
        System.out.println("Klick Naechster Monat");
    }

    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("Klick Abbrechen");
    }

    @FXML
    protected void onBtSpeichernClick() {
        System.out.println(comboBoxMonatAuswahl.getSelectionModel().getSelectedItem());
        System.out.println("Klick Speichern");
    }
    @FXML protected void onBtGeschlossenClick() {
        System.out.println("Klick Geschlossen");
    }
    @FXML protected void onBtOffenClick() {
        System.out.println("Klick Geschlossen");
    }
}

