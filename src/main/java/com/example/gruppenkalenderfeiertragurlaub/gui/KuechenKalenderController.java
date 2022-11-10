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
    @FXML TableColumn<KuechenKalenderTag, LocalDate> tcDatum;
    @FXML TableColumn<KuechenKalenderTag, Boolean> tcKuecheOffen;
    ArrayList<KuechenKalenderTag> kuechenListe;
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
        System.out.println("Klick Speichern");
    }
    @FXML protected void onBtGeschlossenClick() {
        System.out.println("Called onBtGeschlossenClick()");
    }
    @FXML protected void onBtOffenClick() {
        System.out.println("Called onBtOffenClick()");
    }

    //TODO connect with GUI and implement
    @FXML protected void onComboboxJahrAuswahlAction() throws SQLException {
        update();
    }

    public void initialize() throws SQLException {

        configureBooleanTableColum(tcKuecheOffen, "kuecheGeoeffnet");
        configureLocalDateTableColum(tcDatum, "datum");

        configureCBMonatAuswahl(comboBoxMonatAuswahl);
        configureCBJahrAuswahl(comboBoxJahrAuswahl);

        DatenbankCommunicator.establishConnection();
        update();
    }

    //TODO write documentation
    private void update() throws SQLException {
        if(comboBoxJahrAuswahl.getSelectionModel().isEmpty()) {
            return;
        }
        //TODO add save and warning window and code
        ArrayList<KuechenKalenderTag> kuechenListe = DatenbankCommunicator.readKuechenKalenderTage(comboBoxJahrAuswahl.getSelectionModel().getSelectedItem());
        tbTabelle.getItems().setAll(kuechenListe);
        tbTabelle.getSortOrder().clear();
        tbTabelle.getSortOrder().add(tcDatum);
    }
}

