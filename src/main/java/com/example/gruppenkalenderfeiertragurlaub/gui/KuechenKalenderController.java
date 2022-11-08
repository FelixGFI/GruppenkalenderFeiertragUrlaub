package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.BetriebsurlaubsTag;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.KuechenKalenderTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

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
    ArrayList<KuechenKalenderTag> kuechenListe;
    //TODO improve Printed Strings on Button Click
    @FXML
    protected void onBtVorherigerMonatClick() {
        System.out.println("Called onBtVorigerMonatClick()");
    }

    @FXML
    protected void onBtNaechsterMonatClick() {
        System.out.println("Called onBtNaechsterMonatClick()");
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

    @FXML protected void onComboboxJahrAuswahlAction() {
        System.out.println("Called onComboboxJahresAuswahlAction()");
    }

    public void initialize() throws SQLException {

        configureBooleanTableColum(tcKuecheOffen, "kuecheGeoeffnet");
        configureLocalDateTableColum(tcDatum, "datum");

        configureCBMonatAuswahl(comboBoxMonatAuswahl);
        configureCBJahrAuswahl(comboBoxJahrAuswahl);



        DatenbankCommunicator.establishConnection();
        kuechenListe = DatenbankCommunicator.readKuechenKalenderTage(comboBoxJahrAuswahl.getSelectionModel().getSelectedItem());
        tbTabelle.getItems().addAll(kuechenListe);
    }
}

