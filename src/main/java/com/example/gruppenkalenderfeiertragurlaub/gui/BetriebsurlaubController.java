package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.BetriebsurlaubsTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class BetriebsurlaubController extends ControllerBasisKlasse{
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

    ArrayList<BetriebsurlaubsTag> betriebsurlaubsTage;

    @FXML protected void onBtVorherigerMonatClick() {
        System.out.println("Called onBtVorigerMonatClick()");
    }

    @FXML protected void onBtNaechsterMonatClick() {
        System.out.println("Called onBtNaechsterMonatClick()");
    }

    @FXML protected void onBtAbbrechenClick() {
        System.out.println("Called onBtAbbrechenClick()");
    }

    @FXML protected void onBtSpeichernClick() {
        try {
            System.out.println(tbTabelle.getSelectionModel().getSelectedItem().getIsBetriebsurlaub());
            System.out.println(tbTabelle.getSelectionModel().getSelectedItem().getDatum());
        } catch (Exception e) {

        }

        tbTabelle.getItems().add(new BetriebsurlaubsTag(LocalDate.now().plusDays(5), true));
        System.out.println("Called onBtSpeichernClick()");
    }
    @FXML protected void onBtUebernehmenClick() {
        System.out.println("Called onBtUebernehmenClick()");
    }
    public void initialize() throws SQLException {
        //Wichtig! FXML object (wie table Colums, Table View, Buttons etc. Nicht neu initialisieren/überschreiben
        //Weil das FXML object im code ja schon ein UI element referenziert.
        configureBooleanTableColum(tcIstBetriebsurlaub, "isBetriebsurlaub");
        configureLocalDateTableColum(tcDatum, "datum");

        configureCBJahrAuswahl(comboBoxJahrAuswahl);
        configureCBMonatAuswahl(comboBoxMonatAuswahl);

        DatenbankCommunicator.establishConnection();
        betriebsurlaubsTage = DatenbankCommunicator.readBetriebsurlaubTage(comboBoxJahrAuswahl.getSelectionModel().getSelectedItem());
        tbTabelle.getItems().addAll(betriebsurlaubsTage
        );
    }
}
