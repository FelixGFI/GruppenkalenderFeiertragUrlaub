package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.BetriebsurlaubsTag;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.KuechenKalenderTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class KuechenKalenderController extends ControllerBasisKlasse {
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

    //TODO improve Printed Strings on Button Click
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
        System.out.println("Called onBtAbbrechenClick()");
    }

    @FXML
    protected void onBtSpeichernClick() {
        System.out.println(comboBoxMonatAuswahl.getSelectionModel().getSelectedItem());
        System.out.println("Klick Speichern");
    }
    @FXML protected void onBtGeschlossenClick() {
        System.out.println("Called onBtGeschlossenClick()");
    }
    @FXML protected void onBtOffenClick() {
        System.out.println("Called onBtOffenClick()");
    }

    public void initialize() {

        configureBooleanTableColum(tcKuecheOffen, "kuecheGeoeffnet");
        configureLocalDateTableColum(tcDatum, "datum");

        configureCBMonatAuswahl(comboBoxMonatAuswahl);
        configureCBJahrAuswahl(comboBoxJahrAuswahl);

        createTestData();
    }

    private void createTestData() {
        tbTabelle.getItems().add(new KuechenKalenderTag(LocalDate.now().plusDays(10), true));
        tbTabelle.getItems().add(new KuechenKalenderTag(LocalDate.now(), false));
    }
}

