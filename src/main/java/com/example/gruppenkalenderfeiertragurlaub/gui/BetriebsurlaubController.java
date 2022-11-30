package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.BetriebsurlaubsTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class BetriebsurlaubController extends ControllerBasisKlasse {
    @FXML Button btSpeichern;
    @FXML Button btAbbrechen;
    @FXML Button btUrlaub;
    @FXML Button btArbeit;
    @FXML Button btVorigerMonat;
    @FXML Button btNaechesterMonat;
    @FXML ComboBox<String> comboBoxMonatAuswahl;
    @FXML ComboBox<Integer> comboBoxJahrAuswahl;
    @FXML DatePicker dpVon;
    @FXML DatePicker dpBis;
    @FXML TableColumn<BetriebsurlaubsTag, LocalDate> tcDatum;
    @FXML TableColumn<BetriebsurlaubsTag, Integer> tcIstBetriebsurlaub;
    @FXML TableView<BetriebsurlaubsTag> tbTabelle;
    @FXML
    protected void onBtVorherigerMonatClick() {
        Integer monthChange = -1;
        if (!monthChangeOperationShouldbeContinued(firstOfCurrentMonth, monthChange)) return;
        firstOfCurrentMonth = changeMonthBackOrForthBy(monthChange, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
    }
    @FXML
    protected void onBtNaechsterMonatClick() {
        Integer monthChange = 1;
        if (!monthChangeOperationShouldbeContinued(firstOfCurrentMonth, monthChange)) return;
        firstOfCurrentMonth = changeMonthBackOrForthBy(monthChange, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
    }
    @FXML
    protected void onBtAbbrechenClick() {
        if(dataHasBeenModified) {
            if(!getNutzerBestaetigung()) return;
        }
        Stage stage = (Stage) (btAbbrechen.getScene().getWindow());
        stage.close();
    }
    @FXML
    protected void onBtSpeichernClick() throws SQLException {
        System.out.println("Called onBtSpeichernClick()");
        updateTableView();
    }
    @FXML
    protected void onComboboxJahrAuswahlAction() throws SQLException {
        if (!handleComboboxJahrauswahlShouldBeContinued(comboBoxJahrAuswahl)) return;
        handleOnComboboxJahrAuswahlAction(comboBoxJahrAuswahl, tbTabelle);
        updateDatpickers(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
        updateTableView();
    }
    @FXML
    protected void onComboboxMonatAuswahlAction() {
        int monthIndex = comboBoxMonatAuswahl.getSelectionModel().getSelectedIndex() + 1;
        firstOfCurrentMonth = firstOfCurrentMonth.withMonth(monthIndex);
        updateDatpickers(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
        scrollToSelectedMonth(firstOfCurrentMonth, tbTabelle);

    }
    @FXML
    protected void onBtUrlaubClick() {
        for (BetriebsurlaubsTag tag : tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getIsCurrentlyBetriebsurlaub() != 2) {
                tag.setIsCurrentlyBetriebsurlaub(1);
                dataHasBeenModified = true;
            }
        }
        tbTabelle.refresh();
    }
    @FXML
    protected void onBtArbeitClick() {
        for (BetriebsurlaubsTag tag : tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getIsCurrentlyBetriebsurlaub() != 2) {
                tag.setIsCurrentlyBetriebsurlaub(0);
                dataHasBeenModified = true;
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
        configureIntegerTableColum(tcIstBetriebsurlaub, "isCurrentlyBetriebsurlaub");
        configureLocalDateTableColum(tcDatum, "datum");
        configureCBJahrAuswahl(comboBoxJahrAuswahl);
        configureCBMonatAuswahl(comboBoxMonatAuswahl);
        DatenbankCommunicator.establishConnection();
        firstOfCurrentMonth = LocalDate.now();
        firstOfCurrentMonth = firstOfCurrentMonth.withDayOfMonth(1);
        firstOfCurrentMonth = DatenbankCommunicator.getNextWerktag(firstOfCurrentMonth);
        updateTableView();
        tbTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tbTabelle.addEventFilter(ScrollEvent.SCROLL, event ->
                handleScrollEvent(event));
    }

    private void handleScrollEvent(ScrollEvent event) {
        //System.out.println("handle reached");
        try{
            BetriebsurlaubsTag tag = (BetriebsurlaubsTag)((TableCell) event.getTarget()).getItem();
            System.out.println(tag.getDatum());
        } catch (Exception e) {
            return;
        }
    }

    /**
     * Die Methode überprüft ob die Tabelle Leer ist. Wenn nicht sorgt sie für das speichern aller änderungen setzt
     * den Entsprechenden Boolean das es keine Uungespeicherten daten gibt. Anschließend liest sie anhand des firstOfCurrentMonth
     * Datums alle Daten für das gewünschte Jahr aus und schreibt sie in die Tabelle
     * @throws SQLException wird geworfen wenn der Datenbankzugriff nicht Ordnungsgemäß funktioniert
     */
    private void updateTableView() throws SQLException {
        if (!tbTabelle.getItems().isEmpty()) {
            DatenbankCommunicator.saveBetriebsurlaub(tbTabelle.getItems());
            dataHasBeenModified = false;
        }
        if (comboBoxJahrAuswahl.getSelectionModel().isEmpty()) {
            return;
        }
        ArrayList<BetriebsurlaubsTag> betriebsurlaubsTage = DatenbankCommunicator.readBetriebsurlaubTage(firstOfCurrentMonth.getYear());
        tbTabelle.getItems().setAll(betriebsurlaubsTage);
    }
}

