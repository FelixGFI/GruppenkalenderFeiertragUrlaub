package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.UsefulConstants;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenFamilieFuerKalender;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenKalenderTag;
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
        changeMonthBackOrForthByGivenNumber(-1);
        //scrollToSelectedMonth();
    }
    @FXML protected void onBtNaechsterMonatClick() throws SQLException {
        changeMonthBackOrForthByGivenNumber(1);
        //scrollToSelectedMonth();
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
        scrollToSelectedMonth();
    }
    @FXML protected void onComboboxJahrAuswahlAction() throws SQLException {
        update();
        scrollToSelectedMonth();
    }
    @FXML protected void onComboboxMonatAuswahlAction() throws SQLException {
        System.out.println("OnACTION!");
        scrollToSelectedMonth();
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
                comboBoxJahrAuswahl.getSelectionModel().getSelectedItem(),
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

        //TODO maybe make use of firstOfCurrentMonth to update things.
        firstOfCurrentMonth = DatenbankCommunicator.getNextWerktag(LocalDate.now());



    }
    //TODO add documentation
    private void changeMonthBackOrForthByGivenNumber(Integer changeNumber) throws SQLException {
        String selectedMonat = comboBoxMonatAuswahl.getSelectionModel().getSelectedItem();
        Boolean operationIsIncreaseMonat = (changeNumber > 0);
        Integer jahr = comboBoxJahrAuswahl.getSelectionModel().getSelectedItem();
        if(operationIsIncreaseMonat && selectedMonat == UsefulConstants.getMonateListInLocalDateFormat().get(11)) {
            Integer neuesJahr = jahr + 1;
            Integer neuerMonatIndex = 0;
            setNewYearAndMonth(neuesJahr, neuerMonatIndex);
        } else if (!operationIsIncreaseMonat && selectedMonat == UsefulConstants.getMonateListInLocalDateFormat().get(0)) {
            Integer neuesJahr = jahr - 1;
            Integer neuerMonatIndex = 11;
            setNewYearAndMonth(neuesJahr, neuerMonatIndex);
        } else {
            comboBoxMonatAuswahl.getSelectionModel().select(getSelectedMonatIndex() + changeNumber);
        }
    }
    //TODO add documentation
    private void setNewYearAndMonth(Integer neuesJahr, Integer neuerMonatIndex) throws SQLException {
        if(UsefulConstants.getJahreList().contains(neuesJahr)) {
            comboBoxJahrAuswahl.getSelectionModel().select(comboBoxJahrAuswahl.getItems().indexOf(neuesJahr));
            comboBoxMonatAuswahl.getSelectionModel().select(neuerMonatIndex);
            update();
        }
    }

    //TODO add documentation
    private void scrollToSelectedMonth() {
        LocalDate firstWerktagOfMonth = getFirstWerktagOfSelectedMonth();
        System.out.println(firstWerktagOfMonth.toString());
        for(GruppenKalenderTag gkTag : tbTabelle.getItems()) {
            //TODO ERROR HAPPENS HERE
            if(gkTag.getDatum().getMonth().equals(firstWerktagOfMonth.getMonth())) {
                System.out.println(gkTag.getDatum().toString() + " =? " + firstWerktagOfMonth.toString());
                tbTabelle.scrollTo(tbTabelle.getItems().indexOf(gkTag));
                break;
            }
        }
    }
    //TODO add documentation
    private LocalDate getFirstWerktagOfSelectedMonth() {
        Integer monatIndex = getSelectedMonatIndex();
        LocalDate datum = LocalDate.parse(comboBoxJahrAuswahl.getSelectionModel().getSelectedItem() + "-01-01");
        datum = datum.plusMonths(monatIndex);
        DatenbankCommunicator.getNextWerktag(datum);
        return datum;
    }

    //TODO add documentation
    private Integer getSelectedMonatIndex() {
        String selectedMonat = comboBoxMonatAuswahl.getSelectionModel().getSelectedItem();
        Integer selectedMonatIndex = UsefulConstants.getMonateListInLocalDateFormat().indexOf(selectedMonat
        );
        return selectedMonatIndex;
    }
}