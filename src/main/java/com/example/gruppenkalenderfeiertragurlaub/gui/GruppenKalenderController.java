package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.PDFCreator;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.UsefulConstants;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppeFuerKalender;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenFamilieFuerKalender;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenKalenderTag;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
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
    @FXML Button btPDFErstellen;
    Object aktuelleGruppeOderGruppenfamilie;
    Boolean gruppenAuswahlWasJustHandled = false;
    ArrayList<GruppenFamilieFuerKalender> gruppenFamilienListe;

    /**
     * Wenn der Button geklickt wird so wird das firstOfCurrentMonth zum vorherigen Monat geändert und entsprechend,
     * die ComboBox zur Monatsauswahl als auch gegebenenfalls die zur Jahresauswahl angepasst und die damit
     * verbundenen OnActions ausgeführt (was scrollen zum entsprechenden monat als auch
     * ggf. aktualisieren der Daten der Tabelle beinhaltet).
     */
    @FXML protected void onBtVorherigerMonatClick() {
        Integer monthChange = -1;
        incrementOrDecrementMonatViaButton(monthChange, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
    }

    /**
     * Wenn der Button geklickt wird so wird das firstOfCurrentMonth zum nächsten Monat geändert und entsprechend,
     * die ComboBox zur Monatsauswahl als auch gegebenenfalls die zur Jahresauswahl angepasst und die damit
     * verbundenen OnActions ausgeführt (was scrollen zum entsprechenden monat als auch
     * ggf. aktualisieren der Daten der Tabelle beinhaltet).
     */
    @FXML protected void onBtNaechsterMonatClick() {
        Integer monthChange = 1;
        incrementOrDecrementMonatViaButton(monthChange, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
    }

    /**
     * Wenn der Button btAbbrechen geklickt so wird, überprüft, ob die Daten in der Tabelle bearbeitet worden sind.
     * falls Ja, wird Nutzerbestätigung mittels Pop-up-Fenster erbeten. Wird diese gewährt werden die Daten gespeichert
     * und die Stage geschlossen, wird diese nicht gewährt tut die Methode nichts weiter, wurden die Daten nicht
     * bearbeitet wird das Fenster ohne Frage nach Nutzerbestätigung geschlossen.
     */
    @FXML protected void onBtAbbrechenClick() {
        if(dataHasBeenModified) {
            if(!getNutzerBestaetigung()) return;
        }
        Stage stage = (Stage) (btAbbrechen.getScene().getWindow());
        stage.close();
    }

    /**
     * Ruft auf Knopfdruck auf btSpeichern die Methode updateTable zur abspeicherung der Daten und neu laden auf.
     * @throws SQLException sollte beim Datenbankzugriff ein Fehler auftreten wird diese geworfen.
     */
    @FXML protected void onBtSpeichernClick() throws SQLException {
        updateTableView();
    }

    /**
     * Überprüft, ob in der ComboBox zur Statusauswahl ein Status ausgewählt ist. Wenn nein, wird die Methode beendet.
     * Nimmt den ausgelesenen Status und setzt ihn für alle in der Tabelle ausgewählten Tage als Status. Bei Änderung
     * des Gruppenstatus überprüft das Tag Objekt automatisch die verfügbarkeit des Essen, anhand dessen ob die
     * Küche offen ist und anhand des neuen Status.
     */
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

    /**
     * Wenn der Knopf btPDFErstellen gedrückt wird, liest die Methode die ausgewählte Gruppe oder
     * die ausgewählte Gruppenfamilie aus der ComboBox zur Gruppenauswahl aus und holt die Datensätze
     * aus der tableView. Diese werden dan dem PDFCreator übergeben der aus den gegebenen Daten ein PDF erstellt
     * so fern Daten vorhanden sind un die Tabelle nicht leer ist.
     * @throws FileNotFoundException Sollte es ein Problem mit dem Auffinden des Files geben, wohin die Daten als
     * PDF gespeichert werden sollen, wird diese Exception geworfen.
     */
    @FXML protected void onBtPDFErstellenClick() throws FileNotFoundException {
        ArrayList<GruppeFuerKalender> gruppenListe = new ArrayList<>();
        try{
            GruppenFamilieFuerKalender gruppenFamilie = (GruppenFamilieFuerKalender) comboBoxGruppenAuswahl.getSelectionModel().getSelectedItem();
            gruppenListe.addAll(gruppenFamilie.getGruppenDerFamilie());
        } catch (Exception e) {
            GruppeFuerKalender gruppe = (GruppeFuerKalender) comboBoxGruppenAuswahl.getSelectionModel().getSelectedItem();
            gruppenListe.add(gruppe);
        }
        PDFCreator.writePDF(tbTabelle.getItems(), (Stage) this.btSpeichern.getScene().getWindow(), gruppenListe);
    }

    /**
     * ruft die entsprechende Methode in der klasse Controller auf welche überprüft ob, und wenn ja welche, Zeilen
     * in der Tabelle anhand des Datums ausgewählt werden sollen und diese dan auswählt.
     */
    @FXML protected void onDpVonAction() {
        handleDatePickerVon(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
    }

    /**
     * ruft die entsprechende Methode in der klasse Controller auf welche überprüft ob, und wenn ja welche, Zeilen
     * in der Tabelle anhand des Datums ausgewählt werden sollen und diese dan auswählt.
     */
    @FXML protected void onDpBisAction() {
        handleDatePickerBis(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
    }

    /**
     * Diese methode wird jedes Mal aufgerufen, wenn im Dialog auf der ComboBox zur Gruppenauswahl ein Action event erzeugt
     * wird. Da dies auch der Fall ist, wenn die ComboBox manuel umgestellt wird, überprüft diese Action Handler Methode
     *  erst ob der entsprechende Boolean true ist. Ist dies der Fall, fährt die Methode nicht fort. Andernfalls überprüft
     *  die Methode ob Änderungen an den angezeigten Daten vorgenommen wurden. Ist dies der Fall, erbittet sie per
     *  Pop-up-Fenster Nutzerbestätigung. Wird diese nicht gewährt, fährt die Methode nicht weiter fort. Wird die
     *  Nutzerbestätigung verweigert oder es wurden keine Änderungen vorgenommen, so ruft die Methode updateTableView()
     *  auf um Daten für die neue ausgewählte Gruppe oder Gruppenfamilie anzuzeigen.
     * @throws SQLException sollte beim Datenbankzugriff ein Fehler auftreten wird diese geworfen.
     */
    @FXML protected void onComboboxGruppenAuswahlAction() throws SQLException {
        //Die Reihenfolge der methodenaufrufe sind ESSENZIELL WICHTIG FÜR DIE KORREKTE FUNKTIONSFÄHIGKEIT DES PROGRAMMSES!!!
        if(gruppenAuswahlWasJustHandled) {
            gruppenAuswahlWasJustHandled = false;
            return;
        }
        if(aktuelleGruppeOderGruppenfamilie != null)  {
            if(dataHasBeenModified) {
                if(!getNutzerBestaetigung()){
                    gruppenAuswahlWasJustHandled = true;
                    comboBoxGruppenAuswahl.getSelectionModel().select(aktuelleGruppeOderGruppenfamilie);
                    return;
                }
            }
        }
        aktuelleGruppeOderGruppenfamilie = comboBoxGruppenAuswahl.getSelectionModel().getSelectedItem();
        updateTableView();
        scrollToSelectedMonth(firstOfCurrentMonth, tbTabelle);
    }

    /**
     * Die Methode überprüft zuerst, ob die Methode weiter fortgesetzt, oder abgebrochen werden sollte. Soll
     * die Methode fortgesetzt werden so sorgt sie dafür das die Daten in der Tabelle gespeichert und auf das
     * neu ausgewählte Jahr aktualisiert werden sowie das zum korrekten Monat gescrollt und die DatePicker
     * entsprechend aktualisiert werden
     * @throws SQLException sollte beim Datenbankzugriff ein Fehler auftreten wird diese geworfen.
     */
    @FXML protected void onComboboxJahrAuswahlAction() throws SQLException {
        //Die Reihenfolge der methodenaufrufe sind ESSENZIELL WICHTIG FÜR DIE KORREKTE FUNKTIONSFÄHIGKEIT DES PROGRAMMSES!!!
        if (!handleComboBoxJahrAuswahlShouldBeContinued(comboBoxJahrAuswahl)) return;
        handleOnComboBoxJahrAuswahlAction(comboBoxJahrAuswahl, tbTabelle);
        updateDatePickers(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
        updateTableView();
    }

    /**
     * Diese methode wird jedes Mal aufgerufen, wenn im Dialog auf der ComboBox zur Monatsauswahl ein Action event erzeugt
     * wird. Da dies auch der Fall ist, wenn die ComboBox manuel umgestellt wird, überprüft diese Action Handler Methode
     * erst ob der entsprechende Boolean true ist. Wenn ja, muss die Method nicht weiter aktiv werden. Wenn nein, passt
     * sie den das firstOfCurrentDate entsprechend der Nutzerauswahl an und scrollt zum ausgewählten Monat.
     */
    @FXML protected void onComboboxMonatAuswahlAction() {
        if(scrollWasJustHandled) {
            scrollWasJustHandled = false;
            return;
        }
        int monthIndex = comboBoxMonatAuswahl.getSelectionModel().getSelectedIndex() + 1;
        firstOfCurrentMonth = firstOfCurrentMonth.withMonth(monthIndex);
        scrollToSelectedMonth(firstOfCurrentMonth, tbTabelle);
        updateDatePickers(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
    }

    /**
     * Übernimmt für die ausgewählten Tage den in der Datenbank für dieses Jahr hinterlegten Betriebsurlaub.
     */
    @FXML protected void onBtBetriebsurlaubUebernehmenClick () {
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
     * Konfiguriert alle im Dialog enthaltenen SQL-Elemente. Etabliert eine Datenbankverbindung. Setzt das firstOfCurrentMonth
     * auf den ersten Tag des aktuellen Monats. läd die Daten für das aktuelle Jahr aus der Datenbank in die Tabelle. Setzt einen
     * EventFilter welcher scrollEvents erkennt und darauf reagiert. Setzt mittels eines Listeners auf die Scene des aufgerufenen
     * Dialogs eine onCloseRequest (methode die Ausgeführt wird, wenn der Dialog mittels des kleinen Kreuzchens oben
     * links geschlossen wird), welche, sobald versucht wird den Dialog zu mittels Kreuzchen zu schließen
     * überprüft, ob die Daten in der tabelle vom Nutzer editiert wurden. Wenn ja, so wird Nutzerbestätigung erbeten.
     * Wird diese gegeben oder wurden keine Daten editiert schließt sich der Dialog, wird diese verweigert so
     * wird die Methode abgebrochen, indem das durch die Close Request erzeugte Event consumed wird.
     * @throws SQLException sollte beim Datenbankzugriff ein Fehler auftreten wird diese geworfen.
     */
    public void initialize() throws SQLException {
        //IMPORTANT!: gruppenFamilenListe =  configureCBGruppenAuswahl MUST BE CALLED FIRST before configure tcGruppenBeziechnung!
        //Otherwise the needed gruppenFamilienListe will  be empty"!
        Label lblPlacholderText = new Label("Momentan sind keine Daten ausgewählt.\nBitte wählen Sie eine Gruppe oder Gruppenfamilie in der Dropdownliste aus.");
        lblPlacholderText.setTextAlignment(TextAlignment.CENTER);
        tbTabelle.setPlaceholder(lblPlacholderText);
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
        tbTabelle.sceneProperty().addListener((obs, oldScene, newScene) -> Platform.runLater(() -> {
            Stage stage = (Stage) newScene.getWindow();
            stage.setOnCloseRequest(e -> {
                if(dataHasBeenModified) {
                    if(!getNutzerBestaetigung()) e.consume();
                }
            });
        }));
    }

    /**
     * Die Methode überprüft, ob die Tabelle Leer ist. Wenn nicht, sorgt sie für das Speichern aller Änderungen und setzt
     * den Entsprechenden Boolean, welcher angibt, dass es keine ungespeicherten daten gibt. Anschließend liest sie anhand des firstOfCurrentMonth
     * Datums sowie der ausgewählten Gruppe oder Gruppenfamilie alle Daten für das gewünschte Jahr aus und schreibt sie in die Tabelle
     * @throws SQLException wird geworfen, wenn der Datenbankzugriff nicht Ordnungsgemäß funktioniert
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
}