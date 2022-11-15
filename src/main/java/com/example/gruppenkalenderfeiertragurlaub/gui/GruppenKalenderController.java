package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.UsefulConstants;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenFamilieFuerKalender;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenKalenderTag;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

public class GruppenKalenderController extends ControllerBasisKlasse{
    @FXML Button btSpeichern;
    @FXML Button btAbbrechen;
    @FXML Button btUebernehmen;
    @FXML Button btVorherigerMonat;
    @FXML Button btNaechsterMonat;
    @FXML ComboBox<String> comboBoxMonatAuswahl;
    @FXML ComboBox<Integer> comboBoxJahrAuswahl;
    @FXML ComboBox<Object> comboBoxGruppenAuswahl;
    @FXML ComboBox<Character> comboBoxStatusAuswahl;
    @FXML DatePicker dpVon;
    @FXML DatePicker dpBis;
    @FXML TableView<GruppenKalenderTag> tbTabelle;
    @FXML TableColumn<GruppenKalenderTag, LocalDate> tcDatum;
    @FXML TableColumn<GruppenKalenderTag, Character> tcGruppenStatus;
    @FXML TableColumn<GruppenKalenderTag, Boolean> tcEssenVerfuegbar;
    @FXML TableColumn<GruppenKalenderTag, Integer> tcGruppenBezeichnung;

    ArrayList<GruppenFamilieFuerKalender> gruppenFamilienListe;
    LocalDate firstOfCurrentMonth;

    @FXML protected void onBtVorherigerMonatClick() throws SQLException {
        //TODO investigate why scrolling doesn't work properly when changing to previous year
        changeMonthBackOrForthByGivenNumber(-1);
        scrollToSelectedMonth(firstOfCurrentMonth);
    }
    @FXML protected void onBtNaechsterMonatClick() throws SQLException {
        changeMonthBackOrForthByGivenNumber(1);
        scrollToSelectedMonth(firstOfCurrentMonth);
    }
    @FXML protected void onBtAbbrechenClick() {
            System.out.println("Called onBAbbrechenClick()");
    }
    @FXML protected void onBtSpeichernClick() {
        System.out.println("Called onBtSpeichernClick()");
    }
    @FXML protected void onBtUebernehmenClick() {
        System.out.println("Called onBtuebernehmenClick()");
    }
    @FXML protected void onComboboxGruppenAuswahlAction() throws SQLException {
        update();
        scrollToSelectedMonth(firstOfCurrentMonth);
    }
    @FXML protected void onComboboxJahrAuswahlAction() throws SQLException {
        // TODO aktuelles Datum korrigieren
        firstOfCurrentMonth = firstOfCurrentMonth.withYear(comboBoxJahrAuswahl.getSelectionModel().getSelectedItem());
        update();
        scrollToSelectedMonth(firstOfCurrentMonth);
    }
    @FXML protected void onComboboxMonatAuswahlAction() throws SQLException {
        int monthIndex = comboBoxMonatAuswahl.getSelectionModel().getSelectedIndex() + 1;
        firstOfCurrentMonth = firstOfCurrentMonth.withMonth(monthIndex);
        scrollToSelectedMonth(firstOfCurrentMonth);
    }

    //TODO add Documentation
    private void update() throws SQLException {
        //wenn die ComboboxGruppenAuswahl kein Ausgewähltes Item hat dann wird die Methode
        //mit Return abgebrochen. Durch die Verwendung von Return im If Statment wird die Komplexität
        //von zahllosen Verschachtelten if Statments vermieden.
        if(comboBoxGruppenAuswahl.getSelectionModel().isEmpty() || comboBoxJahrAuswahl.getSelectionModel().isEmpty()) {
            return;
        }
        //TODO Implement Save or Discard Changes warning, Implement Save if selected.
        ArrayList<GruppenKalenderTag> tageListe = DatenbankCommunicator.readGruppenKalenderTage(
                firstOfCurrentMonth.getYear(),
                comboBoxGruppenAuswahl.getSelectionModel().getSelectedItem());
        tbTabelle.getItems().setAll(tageListe);
        tbTabelle.getSortOrder().clear();
        tbTabelle.getSortOrder().add(tcDatum);
    }

    public void initialize() throws SQLException {
        //IMPORTANT!: gruppenFamilenListe =  configureCBGruppenAuswahl MUST BE CALLED FIRST before configure tcGruppenBeziechnung!
        //Otherwise the needed gruppenFamilienListe will  be empty"!

        configureCBMonatAuswahl(comboBoxMonatAuswahl);
        configureCBJahrAuswahl(comboBoxJahrAuswahl);
        configureCBStatusauswahl(comboBoxStatusAuswahl);
        gruppenFamilienListe = configureCBGruppenAuswahl(comboBoxGruppenAuswahl);

        configureBooleanTableColum(tcEssenVerfuegbar, "essenFuerGruppeVerfuegbar");
        configureLocalDateTableColum(tcDatum, "datum");
        configureGruppenStatusTableColum(tcGruppenStatus, "gruppenstatus");
        configureGruppenBezeichnungTableColum(tcGruppenBezeichnung, "gruppenID", DatenbankCommunicator.getAlleGruppenAusFamilien(gruppenFamilienListe));

        DatenbankCommunicator.establishConnection();

        firstOfCurrentMonth = LocalDate.now();
        firstOfCurrentMonth = firstOfCurrentMonth.withDayOfMonth(1);
        firstOfCurrentMonth = DatenbankCommunicator.getNextWerktag(firstOfCurrentMonth);
    }

    //TODO add documentation
    private void changeMonthBackOrForthByGivenNumber(Integer changeNumber) throws SQLException {

        // Set new date
        firstOfCurrentMonth = firstOfCurrentMonth.plusMonths(changeNumber);

        int indexOfYear = comboBoxJahrAuswahl.getItems().indexOf(firstOfCurrentMonth.getYear());
        comboBoxJahrAuswahl.getSelectionModel().select(indexOfYear);
        int indexOfMonth = firstOfCurrentMonth.getMonthValue() - 1;
        comboBoxMonatAuswahl.getSelectionModel().select(indexOfMonth);
    }

    //TODO add documentation
    private void scrollToSelectedMonth(LocalDate firstWerktagOfMonth) {
        Month month = firstWerktagOfMonth.getMonth();
        ObservableList<GruppenKalenderTag> items = tbTabelle.getItems();
        for(GruppenKalenderTag tag : items) {
            if(tag.getDatum().getMonth().equals(month)) {
                tbTabelle.scrollTo(0);
                tbTabelle.scrollTo(items.indexOf(tag));
                break;
            }
        }
    }
}