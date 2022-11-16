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

    @FXML protected void onBtVorherigerMonatClick() throws SQLException {
        changeMonthBackOrForthBy(-1);
        //scrollToSelectedMonth(firstOfCurrentMonth);
    }
    @FXML protected void onBtNaechsterMonatClick() throws SQLException {
        changeMonthBackOrForthBy(1);
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
        System.out.println("Called onDpVonAction()");
        LocalDate vonDatum = leseDatumAusDatePicker(dpVon);
        if(vonDatum == null) {
            return;
        }
        if(vonDatum.getYear() != firstOfCurrentMonth.getYear()) {
            return;
        }
        LocalDate bisDatum = leseDatumAusDatePicker(dpBis);
        if(bisDatum == null || bisDatum.getYear() != firstOfCurrentMonth.getYear()) {
            markRowsOfOneDate(vonDatum);
        } else {
            if(!bisDatum.isAfter(vonDatum)) {
                return;
            }
            markAllRowsVonBis(vonDatum, bisDatum);
        }
    }
    @FXML protected void onDpBisAction() {
        System.out.println("Called onDpBisAction()");
        LocalDate bisDatum = leseDatumAusDatePicker(dpBis);
        LocalDate vonDatum = leseDatumAusDatePicker(dpVon);
        if(bisDatum == null || vonDatum == null) {
            return;
        }
        if(bisDatum.getYear() != firstOfCurrentMonth.getYear() || vonDatum.getYear() != firstOfCurrentMonth.getYear()) {
            return;
        }
        if(!bisDatum.isAfter(vonDatum)) {
            return;
        }
        markAllRowsVonBis(vonDatum, bisDatum);
    }
    @FXML protected void onComboboxGruppenAuswahlAction() throws SQLException {
        //Die Reihenfolge der methodenaufrufe sind ESSENZIELL WICHTIG FÜR DIE KORREKTE FUNKTIONSFÄHIGKEIT DES PROGRAMMSES!!!
        updateTableView();
        scrollToSelectedMonth(firstOfCurrentMonth);
    }
    @FXML protected void onComboboxJahrAuswahlAction() throws SQLException {
        //Die Reihenfolge der methodenaufrufe sind ESSENZIELL WICHTIG FÜR DIE KORREKTE FUNKTIONSFÄHIGKEIT DES PROGRAMMSES!!!
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

    //TODO add documentation
    private void scrollToSelectedMonth(LocalDate firstWerktagOfMonth) {
        String month = firstWerktagOfMonth.getMonth().toString();
        ObservableList<GruppenKalenderTag> items = tbTabelle.getItems();
        for(GruppenKalenderTag tag : items) {
            if(tag.getDatum().getMonth().toString().equals(month)) {
                tbTabelle.scrollTo(items.indexOf(tag));
                break;
            }
        }
    }
    /**
     * erhält einen DatePicker und versucht aus diesem ein LocalDate auszulesen. Ist dies erfolgreich so gibt die Methode
     * besagtes datum zurück, ist dies nicht erfolgreich gibt die Methode null zurück
     * @param dp
     * @return ausgelesenes LocalDate wenn erfolgreich, Null wenn nicht erfolgreich
     */
    private LocalDate leseDatumAusDatePicker(DatePicker dp) {
        LocalDate datum;
        try {
            datum = dp.getValue();
            System.out.println("GruppenKalenderController.onDpVonAction() " + datum);

        } catch (Exception e) {
            datum = null;
        }
        return datum;
    }
    //TODO add Documentation
    private void markAllRowsVonBis(LocalDate vonDatum, LocalDate bisDatum) {
        tbTabelle.getSelectionModel().clearSelection();
        ObservableList<GruppenKalenderTag> tabellenEintraege = tbTabelle.getItems();
        Boolean istErstesGefundenesDatum = true;
        for(GruppenKalenderTag tag : tabellenEintraege) {
            LocalDate tagesDatum = tag.getDatum();
            Boolean tagIsInIbetweenRange = (tagesDatum.isAfter(vonDatum) && tagesDatum.isBefore(bisDatum));
            Boolean tagIsVonOrBisDatum = (tagesDatum.toString().equals(vonDatum.toString()) || tagesDatum.toString().equals(bisDatum.toString()));
            if(tagIsInIbetweenRange || tagIsVonOrBisDatum) {
                tbTabelle.getSelectionModel().select(tag);
                if(istErstesGefundenesDatum) {
                    tbTabelle.scrollTo(tabellenEintraege.indexOf(tag));
                    istErstesGefundenesDatum = false;
                }
            }
        }

    }
    //TODO add Documentation
    private void markRowsOfOneDate(LocalDate vonDatum) {
        tbTabelle.getSelectionModel().clearSelection();
        ObservableList<GruppenKalenderTag> tabellenEintraege = tbTabelle.getItems();
        Boolean istErstesGefundenesDatum = true;
        for (GruppenKalenderTag tag : tabellenEintraege) {
            if(tag.getDatum().toString().equals(vonDatum.toString())) {
                tbTabelle.getSelectionModel().select(tag);
                if(istErstesGefundenesDatum) {
                    tbTabelle.scrollTo(tabellenEintraege.indexOf(tag));
                    istErstesGefundenesDatum = false;
                }
            }
        }
    }
}