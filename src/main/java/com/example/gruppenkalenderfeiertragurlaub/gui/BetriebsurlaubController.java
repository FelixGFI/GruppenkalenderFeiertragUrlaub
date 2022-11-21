package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.BetriebsurlaubsTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class BetriebsurlaubController extends ControllerBasisKlasse {
    @FXML
    Button btSpeichern;
    @FXML
    Button btAbbrechen;
    @FXML
    Button btUrlaub;
    @FXML
    Button btArbeit;
    @FXML
    Button btVorigerMonat;
    @FXML
    Button btNaechesterMonat;
    @FXML
    ComboBox<String> comboBoxMonatAuswahl;
    @FXML
    ComboBox<Integer> comboBoxJahrAuswahl;
    @FXML
    DatePicker dpVon;
    @FXML
    DatePicker dpBis;
    @FXML
    TableColumn<BetriebsurlaubsTag, LocalDate> tcDatum;
    @FXML
    TableColumn<BetriebsurlaubsTag, Integer> tcIstBetriebsurlaub;
    @FXML
    TableView<BetriebsurlaubsTag> tbTabelle;
    LocalDate firstOfCurrentMonth;
    @FXML
    protected void onBtVorherigerMonatClick() {
        firstOfCurrentMonth = changeMonthBackOrForthBy(-1, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
    }
    @FXML
    protected void onBtNaechsterMonatClick() {
        firstOfCurrentMonth = changeMonthBackOrForthBy(1, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
    }
    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("Called onBtAbbrechenClick()");
    }

    @FXML
    protected void onBtSpeichernClick() {
        System.out.println("Called onBtSpeichernClick()");
    }

    @FXML
    protected void onComboboxJahrAuswahlAction() throws SQLException {
        Integer year = comboBoxJahrAuswahl.getSelectionModel().getSelectedItem();
        firstOfCurrentMonth = firstOfCurrentMonth.withYear(year);
        scrollToSelectedMonth(firstOfCurrentMonth, tbTabelle);
        updateTableView();
    }

    @FXML
    protected void onComboboxMonatAuswahlAction() {
        int monthIndex = comboBoxMonatAuswahl.getSelectionModel().getSelectedIndex() + 1;
        firstOfCurrentMonth = firstOfCurrentMonth.withMonth(monthIndex);
        scrollToSelectedMonth(firstOfCurrentMonth, tbTabelle);
    }

    @FXML
    protected void onBtUrlaubClick() {
        System.out.println("Called onBtUrlaubClick()");
        for (BetriebsurlaubsTag tag : tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getBetriebsurlaub() != 2) {
                tag.setBetriebsurlaub(1);
            }
        }
        tbTabelle.refresh();
    }
    @FXML
    protected void onBtArbeitClick() {
        System.out.println("Called onBtArbeitClick()");
        for (BetriebsurlaubsTag tag : tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getBetriebsurlaub() != 2) {
                tag.setBetriebsurlaub(0);
            }
        }
        tbTabelle.refresh();
    }
    @FXML protected void onDpVonAction() {
        handleDatePickerVon(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
    }
    @FXML protected void onDpBisAction() {
        handleDatePickerBis(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
    }
    public void initialize() throws SQLException {
        //Wichtig! FXML object (wie table Colums, Table View, Buttons etc. Nicht neu initialisieren/überschreiben
        //Weil das FXML object im code ja schon ein UI element referenziert.

        configureIntegerTableColum(tcIstBetriebsurlaub, "betriebsurlaub");
        configureLocalDateTableColum(tcDatum, "datum");

        configureCBJahrAuswahl(comboBoxJahrAuswahl);
        configureCBMonatAuswahl(comboBoxMonatAuswahl);

        DatenbankCommunicator.establishConnection();

        firstOfCurrentMonth = LocalDate.now();
        firstOfCurrentMonth = firstOfCurrentMonth.withDayOfMonth(1);
        firstOfCurrentMonth = DatenbankCommunicator.getNextWerktag(firstOfCurrentMonth);

        updateTableView();

        tbTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    //TODO write documentation
    private void updateTableView() throws SQLException {
        if (comboBoxJahrAuswahl.getSelectionModel().isEmpty()) {
            return;
        }
        //TODO add save and warning window and code
        ArrayList<BetriebsurlaubsTag> betriebsurlaubsTage = DatenbankCommunicator.readBetriebsurlaubTage(firstOfCurrentMonth.getYear());
        tbTabelle.getItems().setAll(betriebsurlaubsTage);
    }
}

