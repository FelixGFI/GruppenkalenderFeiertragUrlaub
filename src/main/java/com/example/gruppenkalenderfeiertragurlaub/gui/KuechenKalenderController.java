package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.KuechenKalenderTag;
import javafx.collections.ObservableList;
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
    LocalDate firstOfCurrentMonth;
    @FXML protected void onBtVorherigerMonatClick() throws SQLException {
        firstOfCurrentMonth = changeMonthBackOrForthBy(-1, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
    }
    @FXML protected void onBtNaechsterMonatClick() throws SQLException {
        firstOfCurrentMonth = changeMonthBackOrForthBy(1, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
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
    @FXML protected void onDpVonAction() {
        toBeCalledInOnDpVonAction(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
    }
    @FXML protected void onDpBisAction() {
        toBeCalledInOnDpBisAction(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
    }

    //TODO connect with GUI and implement
    @FXML protected void onComboboxJahrAuswahlAction() throws SQLException {
        Integer year = comboBoxJahrAuswahl.getSelectionModel().getSelectedItem();
        firstOfCurrentMonth = firstOfCurrentMonth.withYear(year);
        scrollToSelectedMonth(firstOfCurrentMonth, tbTabelle);
        updateTableView();
    }
    @FXML protected void onComboboxMonatAuswahlAction() throws SQLException {
        int monthIndex = comboBoxMonatAuswahl.getSelectionModel().getSelectedIndex() + 1;
        firstOfCurrentMonth = firstOfCurrentMonth.withMonth(monthIndex);
        scrollToSelectedMonth(firstOfCurrentMonth, tbTabelle);
    }

    public void initialize() throws SQLException {

        configureBooleanTableColum(tcKuecheOffen, "kuecheGeoeffnet");
        configureLocalDateTableColum(tcDatum, "datum");

        configureCBMonatAuswahl(comboBoxMonatAuswahl);
        configureCBJahrAuswahl(comboBoxJahrAuswahl);

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
        ArrayList<KuechenKalenderTag> kuechenListe = DatenbankCommunicator.readKuechenKalenderTage(comboBoxJahrAuswahl.getSelectionModel().getSelectedItem());
        tbTabelle.getItems().setAll(kuechenListe);
    }
}

