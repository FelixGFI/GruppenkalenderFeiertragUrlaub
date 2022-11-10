package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
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
    @FXML TableColumn<GruppenKalenderTag, Integer> tcGruppe;

    ArrayList<GruppenFamilieFuerKalender> gruppenFamilienListe;

    @FXML protected void onBtVorherigerMonatClick() {
        System.out.println("Called onBtVorigerMonatClick()");
    }

    @FXML protected void onBtNaechsterMonatClick() {
        System.out.println("Called onBtNaechsterMonatClick()");
    }

    @FXML protected void onBtAbbrechenClick() {
        System.out.println("Called onBAbbrechenClick()");
    }

    @FXML protected void onBtSpeichernClick() {

        System.out.println("Called onBtSpeichernClick()");
    }
    @FXML protected void onBtUebernehmenClick() {
        System.out.println("Called onBtuebernehmenClick()");
        tbTabelle.getItems().clear();
    }

    @FXML protected void onComboboxGruppenAuswahlAction() throws SQLException {
        System.out.println("Called onComboboxGruppenAuswahlAction()");
        update();
    }

    @FXML protected void onComboboxJahrAuswahlAction() throws SQLException {
        System.out.println("Called onComboboxJahrAuswahlAction()");
        update();
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
    }

    public void initialize() throws SQLException {
        configureBooleanTableColum(tcEssenVerfuegbar, "essenFuerGruppeVerfuegbar");
        configureLocalDateTableColum(tcDatum, "datum");
        configureGruppenStatusTableColum(tcGruppenStatus, "gruppenstatus");

        configureCBMonatAuswahl(comboBoxMonatAuswahl);
        configureCBJahrAuswahl(comboBoxJahrAuswahl);
        configureCBStatusauswahl(comboBoxStatusAuswahl);
        gruppenFamilienListe = configureCBGruppenAuswahl(comboBoxGruppenAuswahl);
        //TODO Default sort Table by datum

        DatenbankCommunicator.establishConnection();

    }

    private void createTestdata() {
        tbTabelle.getItems().add(new GruppenKalenderTag(1, LocalDate.now(), 'B', false));
        tbTabelle.getItems().add(new GruppenKalenderTag(1, LocalDate.now().minusDays(5), 'O', false));
        tbTabelle.getItems().add(new GruppenKalenderTag(2, LocalDate.now(), 'P', true));
        tbTabelle.getItems().add(new GruppenKalenderTag(3, LocalDate.now().plusDays(1), 'U', false));
        tbTabelle.getItems().add(new GruppenKalenderTag(3,LocalDate.now().plusDays(2), 'A', false));
    }
}