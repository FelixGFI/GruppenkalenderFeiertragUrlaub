package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.UsefulConstants;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenFamilieFuerKalender;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenKalenderTag;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class GruppenKalenderController extends Controller {
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
    @FXML Button btBetriebsurlaubUebernehmen;

    ArrayList<GruppenFamilieFuerKalender> gruppenFamilienListe;
    @FXML protected void onBtVorherigerMonatClick() {
        Integer monthChange = -1;
        if (!monthChangeOperationShouldbeContinued(firstOfCurrentMonth, monthChange)) return;
        firstOfCurrentMonth = changeMonthBackOrForthBy(monthChange, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
        //scrollToSelectedMonth(firstOfCurrentMonth);
    }
    @FXML protected void onBtNaechsterMonatClick() {
        Integer monthChange = 1;
        if (!monthChangeOperationShouldbeContinued(firstOfCurrentMonth, monthChange)) return;
        firstOfCurrentMonth = changeMonthBackOrForthBy(monthChange, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
        //scrollToSelectedMonth(firstOfCurrentMonth);
    }
    @FXML protected void onBtAbbrechenClick() {
        System.out.println("Called onBAbbrechenClick()");
        if(dataHasBeenModified) {
            if(!getNutzerBestaetigung()) return;
        }
        Stage stage = (Stage) (btAbbrechen.getScene().getWindow());
        stage.close();
    }
    @FXML protected void onBtSpeichernClick() throws SQLException {
        System.out.println("Called onBtSpeichernClick()");
        updateTableView();
    }
    @FXML protected void onBtUebernehmenClick() {
        Character ausgewaehlerStatus = comboBoxStatusAuswahl.getSelectionModel().getSelectedItem();
        if(ausgewaehlerStatus == null) return;
        ObservableList<GruppenKalenderTag> ausgewaelteTageListe = tbTabelle.getSelectionModel().getSelectedItems();
        for (GruppenKalenderTag tag : ausgewaelteTageListe) {
            if(tag.getGruppenstatus() != UsefulConstants.getStatusListCharacterFormat().get(6)) {
                tag.setGruppenstatus(ausgewaehlerStatus);
                dataHasBeenModified = true;
            }
            tbTabelle.refresh();
        }
    }
    @FXML protected void onDpVonAction() {
        handleDatePickerVon(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
    }
    @FXML protected void onDpBisAction() {
        handleDatePickerBis(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
    }

    @FXML protected void onComboboxGruppenAuswahlAction() throws SQLException {
        //Die Reihenfolge der methodenaufrufe sind ESSENZIELL WICHTIG FÜR DIE KORREKTE FUNKTIONSFÄHIGKEIT DES PROGRAMMSES!!!
        updateTableView();
        scrollToSelectedMonth(firstOfCurrentMonth, tbTabelle);
    }
    @FXML protected void onComboboxJahrAuswahlAction() throws SQLException {
        //Die Reihenfolge der methodenaufrufe sind ESSENZIELL WICHTIG FÜR DIE KORREKTE FUNKTIONSFÄHIGKEIT DES PROGRAMMSES!!!
        if (!handleComboboxJahrauswahlShouldBeContinued(comboBoxJahrAuswahl)) return;
        handleOnComboboxJahrAuswahlAction(comboBoxJahrAuswahl, tbTabelle);
        updateDatpickers(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
        updateTableView();
    }
    @FXML protected void onComboboxMonatAuswahlAction() {
        if(scrollWasJustHandeld) {
            scrollWasJustHandeld = false;
            return;
        }
        int monthIndex = comboBoxMonatAuswahl.getSelectionModel().getSelectedIndex() + 1;
        firstOfCurrentMonth = firstOfCurrentMonth.withMonth(monthIndex);
        scrollToSelectedMonth(firstOfCurrentMonth, tbTabelle);
        updateDatpickers(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
    }
    @FXML protected void onBtBetriebsurlaubUebernehmenClick () {
        //TODO Warnung das Daten Überschrieben werden
        for (GruppenKalenderTag tag : tbTabelle.getItems()) {
            if(tag.getGruppenstatus() == UsefulConstants.getStatusListCharacterFormat().get(6)) {
                continue;
            }
            if(tag.getBetriebsurlaub()) {
                tag.setGruppenstatus(UsefulConstants.getStatusListCharacterFormat().get(4));
            }
        }
        tbTabelle.refresh();
    }
    /**
     * Die Methode überprüft ob die Tabelle Leer ist. Wenn nicht sorgt sie für das speichern aller änderungen setzt
     * den Entsprechenden Boolean das es keine Uungespeicherten daten gibt. Anschließend liest sie anhand des firstOfCurrentMonth
     * Datums sowie der Ausgewählten Gruppe oder Gruppenfamilie alle Daten für das gewünschte Jahr aus und schreibt sie in die Tabelle
     * @throws SQLException wird geworfen wenn der Datenbankzugriff nicht Ordnungsgemäß funktioniert
     */
    private void updateTableView() throws SQLException {
        if(!tbTabelle.getItems().isEmpty()) {
            DatenbankCommunicator.saveGruppenKalender(tbTabelle.getItems());
            dataHasBeenModified = false;
        }
        //wenn die ComboboxGruppenAuswahl kein Ausgewähltes Item hat dann wird die Methode
        //mit Return abgebrochen. Durch die Verwendung von Return im If Statment wird die Komplexität
        //von zahllosen Verschachtelten if Statments vermieden.
        if(comboBoxGruppenAuswahl.getSelectionModel().isEmpty() ||
                comboBoxJahrAuswahl.getSelectionModel().isEmpty()) {
            return;
        }
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
        Label lblEssenverfuegbarHeader = new Label("Essensangebot");
        lblEssenverfuegbarHeader.setTooltip(new Tooltip("Den ausgewählten Gruppen kann heute Essen angeboten werden"));
        tcEssenVerfuegbar.setGraphic(lblEssenverfuegbarHeader);
        DatenbankCommunicator.establishConnection();
        firstOfCurrentMonth = LocalDate.now();
        firstOfCurrentMonth = firstOfCurrentMonth.withDayOfMonth(1);
        firstOfCurrentMonth = DatenbankCommunicator.getNextWerktag(firstOfCurrentMonth);
        tbTabelle.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tbTabelle.addEventFilter(ScrollEvent.SCROLL, event ->
                handleScrollEvent(event, comboBoxMonatAuswahl));
        tbTabelle.sceneProperty().addListener((obs, oldScene, newScene) -> {
            Platform.runLater(() -> {
                Stage stage = (Stage) newScene.getWindow();
                stage.setOnCloseRequest(e -> {
                    if(dataHasBeenModified) {
                        if(!getNutzerBestaetigung()) e.consume();
                    }
                });
            });
        });
    }
}