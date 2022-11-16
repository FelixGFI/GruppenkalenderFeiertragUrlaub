package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenFamilieFuerKalender;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenKalenderTag;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.time.LocalDate;
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

    @FXML protected void onBtVorherigerMonatClick() {
        firstOfCurrentMonth = changeMonthBackOrForthBy(-1, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
        //scrollToSelectedMonth(firstOfCurrentMonth);
    }
    @FXML protected void onBtNaechsterMonatClick() {
        firstOfCurrentMonth = changeMonthBackOrForthBy(1, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
        //scrollToSelectedMonth(firstOfCurrentMonth);
    }
    @FXML protected void onBtAbbrechenClick() {
            System.out.println("Called onBAbbrechenClick()");
    }
    @FXML protected void onBtSpeichernClick() {
        System.out.println("Called onBtSpeichernClick()");
    }
    @FXML protected void onBtUebernehmenClick() {
        System.out.println("Called onBtuebernehmenClick()");
        Character ausgewaehlerStatus = comboBoxStatusAuswahl.getSelectionModel().getSelectedItem();
        if(ausgewaehlerStatus != null) {

            ObservableList<GruppenKalenderTag> ausgewaelteTageListe = tbTabelle.getSelectionModel().getSelectedItems();

            System.out.println("GruppenKalenderController.onBtUebernehmenClick()" + ausgewaehlerStatus + "\n" +
                    "" + ausgewaelteTageListe.get(0).toString());
            for (GruppenKalenderTag tag : ausgewaelteTageListe) {
                tag.setGruppenstatus(ausgewaehlerStatus);
            }
            tbTabelle.refresh();
        }
    }
    @FXML protected void onDpVonAction() {
        toBeCalledInOnDpVonAction(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
    }
    @FXML protected void onDpBisAction() {
        toBeCalledInOnDpBisAction(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
    }

    @FXML protected void onComboboxGruppenAuswahlAction() throws SQLException {
        //Die Reihenfolge der methodenaufrufe sind ESSENZIELL WICHTIG FÜR DIE KORREKTE FUNKTIONSFÄHIGKEIT DES PROGRAMMSES!!!
        updateTableView();
        scrollToSelectedMonth(firstOfCurrentMonth, tbTabelle);
    }
    @FXML protected void onComboboxJahrAuswahlAction() throws SQLException {
        //Die Reihenfolge der methodenaufrufe sind ESSENZIELL WICHTIG FÜR DIE KORREKTE FUNKTIONSFÄHIGKEIT DES PROGRAMMSES!!!
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

    //TODO add Documentation
    private void updateTableView() throws SQLException {
        //wenn die ComboboxGruppenAuswahl kein Ausgewähltes Item hat dann wird die Methode
        //mit Return abgebrochen. Durch die Verwendung von Return im If Statment wird die Komplexität
        //von zahllosen Verschachtelten if Statments vermieden.
        if(comboBoxGruppenAuswahl.getSelectionModel().isEmpty() ||
                comboBoxJahrAuswahl.getSelectionModel().isEmpty()) {
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

        tbTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        //TODO Formatieren von Date Pickern so das das Akktuelle Datum (firstOfCurrentMonth) Standard Jahr und Monat angibt
    }
}