package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.BetriebsurlaubsTag;
import javafx.collections.ObservableList;
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

    LocalDate firstOfCurrentMonth;
    @FXML protected void onBtVorherigerMonatClick() throws SQLException {
        changeMonthBackOrForthBy(-1);
    }
    @FXML protected void onBtNaechsterMonatClick() throws SQLException {
        changeMonthBackOrForthBy(1);
    }
    @FXML protected void onBtAbbrechenClick() {
        System.out.println("Called onBtAbbrechenClick()");
    }
    @FXML protected void onBtSpeichernClick() {
        System.out.println("Called onBtSpeichernClick()");
    }
    @FXML protected void onComboboxJahrAuswahlAction() throws SQLException {
        Integer year = comboBoxJahrAuswahl.getSelectionModel().getSelectedItem();
        firstOfCurrentMonth = firstOfCurrentMonth.withYear(year);
        scrollToSelectedMonth(firstOfCurrentMonth);
        updateTableView();
    }
    @FXML protected void onComboboxMonatAuswahlAction() throws SQLException {
        int monthIndex = comboBoxMonatAuswahl.getSelectionModel().getSelectedIndex() + 1;
        firstOfCurrentMonth = firstOfCurrentMonth.withMonth(monthIndex);
        scrollToSelectedMonth(firstOfCurrentMonth);
    }
    @FXML protected void onBtUebernehmenClick() {
        System.out.println("Called onBtUebernehmenClick()");
    }
    public void initialize() throws SQLException, InterruptedException {
        //Wichtig! FXML object (wie table Colums, Table View, Buttons etc. Nicht neu initialisieren/überschreiben
        //Weil das FXML object im code ja schon ein UI element referenziert.

        configureBooleanTableColum(tcIstBetriebsurlaub, "isBetriebsurlaub");
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
        if(comboBoxJahrAuswahl.getSelectionModel().isEmpty()) {
            return;
        }
        //TODO add save and warning window and code
        ArrayList<BetriebsurlaubsTag> betriebsurlaubsTage = DatenbankCommunicator.readBetriebsurlaubTage(firstOfCurrentMonth.getYear());
        tbTabelle.getItems().setAll(betriebsurlaubsTage);
    }
    private void scrollToSelectedMonth(LocalDate firstWerktagOfMonth) {
        String month = firstWerktagOfMonth.getMonth().toString();
        System.out.println(month);
        ObservableList<BetriebsurlaubsTag> items = tbTabelle.getItems();
        for(BetriebsurlaubsTag tag : items) {
            if(tag.getDatum().getMonth().toString().equals(month)) {
                System.out.println(tag.getDatum().getMonth().toString() + items.indexOf(tag));
                tbTabelle.scrollTo(items.indexOf(tag));
                break;
            }
        }
    }
    //TODO add documentation
    private void changeMonthBackOrForthBy(Integer changeNumber) throws SQLException {
        // Set new date
        firstOfCurrentMonth = firstOfCurrentMonth.plusMonths(changeNumber);
        if(comboBoxJahrAuswahl.getItems().contains(firstOfCurrentMonth.getYear())) {
            int indexOfYear = comboBoxJahrAuswahl.getItems().indexOf(firstOfCurrentMonth.getYear());
            int indexOfMonth = firstOfCurrentMonth.getMonthValue() - 1;
            comboBoxMonatAuswahl.getSelectionModel().select(indexOfMonth);
            comboBoxJahrAuswahl.getSelectionModel().select(indexOfYear);
        } else {
            firstOfCurrentMonth = firstOfCurrentMonth.minusMonths(changeNumber);
        }
    }
}

