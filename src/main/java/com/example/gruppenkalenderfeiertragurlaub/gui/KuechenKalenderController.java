package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.KuechenKalenderTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

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
    @FXML TableColumn<KuechenKalenderTag, Integer> tcKuecheOffen;
    LocalDate firstOfCurrentMonth;
    @FXML protected void onBtVorherigerMonatClick() {
        firstOfCurrentMonth = changeMonthBackOrForthBy(-1, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
    }
    @FXML protected void onBtNaechsterMonatClick() {
        firstOfCurrentMonth = changeMonthBackOrForthBy(1, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
    }
    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("Called onBtAbbrechenClick()");
        Boolean verwerfenUndSchließen = false;
        if(dataHasBeenAltered()) {
            verwerfenUndSchließen = createAndShowAlert();
        } else {
            verwerfenUndSchließen = true;
        }
        if(verwerfenUndSchließen) {
            Stage stage = (Stage) (btAbbrechen.getScene().getWindow());
            stage.close();
        }

    }
    @FXML
    protected void onBtSpeichernClick() throws SQLException {
        System.out.println("Klick Speichern");
        updateTableView();
    }
    @FXML protected void onBtGeschlossenClick() {
        for (KuechenKalenderTag tag : tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getKuecheCurrentlyGeoeffnet() != 2) {
                tag.setKuecheCurrentlyGeoeffnet(0);
            }
        }
        tbTabelle.refresh();
    }
    @FXML protected void onBtOffenClick() {
        for (KuechenKalenderTag tag : tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getKuecheCurrentlyGeoeffnet() != 2) {
                tag.setKuecheCurrentlyGeoeffnet(1);
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
    @FXML protected void onComboboxJahrAuswahlAction() throws SQLException {

        //TODO make sure that user is asked if he wants to continue
        Integer year = comboBoxJahrAuswahl.getSelectionModel().getSelectedItem();
        firstOfCurrentMonth = firstOfCurrentMonth.withYear(year);
        scrollToSelectedMonth(firstOfCurrentMonth, tbTabelle);
        updateTableView();
    }
    @FXML protected void onComboboxMonatAuswahlAction() {
        int monthIndex = comboBoxMonatAuswahl.getSelectionModel().getSelectedIndex() + 1;
        firstOfCurrentMonth = firstOfCurrentMonth.withMonth(monthIndex);
        scrollToSelectedMonth(firstOfCurrentMonth, tbTabelle);
    }

    public void initialize() throws SQLException {

        configureIntegerTableColum(tcKuecheOffen, "kuecheCurrentlyGeoeffnet");
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
        if(!tbTabelle.getItems().isEmpty()) {
            DatenbankCommunicator.saveKuechenKalender(tbTabelle.getItems());
        }


        if(comboBoxJahrAuswahl.getSelectionModel().isEmpty()) return;

        //TODO add save and warning window and code
        ArrayList<KuechenKalenderTag> kuechenListe = DatenbankCommunicator.readKuechenKalenderTage(comboBoxJahrAuswahl.getSelectionModel().getSelectedItem());
        tbTabelle.getItems().setAll(kuechenListe);
    }
    //TODO add Documentation
    private Boolean dataHasBeenAltered() {
        for (KuechenKalenderTag tag : tbTabelle.getItems()) {
            Boolean kuecheCurrentlyGeoffnet = (tag.getKuecheCurrentlyGeoeffnet() == 1);
            if (tag.getKuecheOriginallyGeoeffnet() != kuecheCurrentlyGeoffnet) return true;
        }
        return false;
    }

}

