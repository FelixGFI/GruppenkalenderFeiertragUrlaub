package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.KuechenKalenderTag;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class KuechenKalenderController extends Controller {
    @FXML Button btSpeichern;
    @FXML Button btAbbrechen;
    @FXML Button btGeschlossen;
    @FXML Button btOffen;
    @FXML Button btNaechsterMonat;
    @FXML Button btVorigerMonat;
    @FXML ComboBox<String> comboBoxMonatAuswahl;
    @FXML ComboBox<Integer> comboBoxJahrAuswahl;
    @FXML DatePicker dpVon;
    @FXML DatePicker dpBis;
    @FXML TableView<KuechenKalenderTag> tbTabelle;
    @FXML TableColumn<KuechenKalenderTag, LocalDate> tcDatum;
    @FXML TableColumn<KuechenKalenderTag, Integer> tcKuecheOffen;

    /**
     * Wenn der Button geklickt wird so wird das firstOfCurrentMonth zum vorherigen Monat geändert und entsprechend,
     * die ComboBox zur Monatsauswahl als auch gegebenenfalls die zur Jahresauswahl angepasst und die damit
     * verbundenen OnActions ausgeführt (was scrollen zum entsprechenden monat als auch
     * ggf. aktualisieren der Daten der Tabelle beinhaltet).
     *
     */
    @FXML protected void onBtVorherigerMonatClick() {
        Integer monthChange = -1;
        if (!monthChangeOperationShouldBbeContinued(firstOfCurrentMonth, monthChange)) return;
        firstOfCurrentMonth = changeMonthBackOrForthBy(monthChange, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
    }

    /**
     * Wenn der Button geklickt wird so wird das firstOfCurrentMonth zum nächsten Monat geändert und entsprechend,
     * die ComboBox zur Monatsauswahl als auch gegebenenfalls die zur Jahresauswahl angepasst und die damit
     * verbundenen OnActions ausgeführt (was scrollen zum entsprechenden monat als auch
     * ggf. aktualisieren der Daten der Tabelle beinhaltet).
     *
     */
    @FXML protected void onBtNaechsterMonatClick() {
        Integer monthChange = 1;
        if (!monthChangeOperationShouldBbeContinued(firstOfCurrentMonth, monthChange)) return;
        firstOfCurrentMonth = changeMonthBackOrForthBy(monthChange, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
    }

    /**
     * Wenn der Button btAbbrechen geklickt so wird, überprüft, ob die Daten in der Tabelle bearbeitet worden sind.
     * falls Ja, wird Nutzerbestätigung mittels Pop-up-Fenster erbeten. Wird diese gewährt werden die Daten gespeichert
     * und die Stage geschlossen, wird diese nicht gewährt tut die Methode nichts weiter, wurden die Daten nicht
     * bearbeitet wird das Fenster ohne Frage nach Nutzerbestätigung geschlossen.
     */
    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("Called onBtAbbrechenClick()");
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
    @FXML
    protected void onBtSpeichernClick() throws SQLException {
        System.out.println("Klick Speichern");
        updateTableView();
    }

    //TODO add Dokumentation
    @FXML protected void onBtGeschlossenClick() {
        for (KuechenKalenderTag tag : tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getKuecheCurrentlyGeoeffnet() != 2) {
                tag.setKuecheCurrentlyGeoeffnet(0);
                dataHasBeenModified = true;
            }
        }
        tbTabelle.refresh();
    }

    //TODO add Dokumentation
    @FXML protected void onBtOffenClick() {
        for (KuechenKalenderTag tag : tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getKuecheCurrentlyGeoeffnet() != 2) {
                tag.setKuecheCurrentlyGeoeffnet(1);
                dataHasBeenModified = true;
            }
        }
        tbTabelle.refresh();
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
     * Die Methode überprüft zuerst, ob die Methode weiter fortgesetzt, oder abgebrochen werden sollte. Soll
     * die Methode fortgesetzt werden so sorgt sie dafür das die Daten in der Tabelle gespeichert und auf das
     * neu ausgewählte Jahr aktualisiert werden sowie das zum korrekten Monat gescrollt und die DatePicker
     * entsprechend aktualisiert werden
     * @throws SQLException sollte beim Datenbankzugriff ein Fehler auftreten wird diese geworfen.
     */
    @FXML protected void onComboboxJahrAuswahlAction() throws SQLException {
        if (!handleComboBoxJahrAuswahlShouldBeContinued(comboBoxJahrAuswahl)) return;
        handleOnComboBoxJahrAuswahlAction(comboBoxJahrAuswahl, tbTabelle);
        updateDatePickers(firstOfCurrentMonth, dpVon, dpBis, tbTabelle);
        updateTableView();
    }

    /**
     * Diese methode wird jedes Mal aufgerufen, wenn im Dialog auf der ComboBox zur Monatsauswahl ein Action event erzeugt
     * wird. Da dies auch der Fall ist, wenn die ComboBox manuel umgestellt wird, überprüft diese Action Handler Methode
     * erst ob der entsprechende Boolean true ist. Wenn ja, muss die Method nicht weiter aktiv werden. Wenn nein passt
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
        tbTabelle.addEventFilter(ScrollEvent.SCROLL, event ->
                handleScrollEvent(event, comboBoxMonatAuswahl));
        //TODO figure out how/why the code below works
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

    /**
     * Die Methode überprüft, ob die Tabelle Leer ist. Wenn nicht, sorgt sie für das Speichern aller Änderungen und setzt
     * den entsprechenden Boolean, welcher angibt, dass es keine ungespeicherten daten gibt. Anschließend liest sie anhand des firstOfCurrentMonth
     * Datums alle Daten für das gewünschte Jahr aus und schreibt sie in die Tabelle
     * @throws SQLException wird geworfen, wenn der Datenbankzugriff nicht Ordnungsgemäß funktioniert
     */
    private void updateTableView() throws SQLException {
        if(!tbTabelle.getItems().isEmpty()) {
            DatenbankCommunicator.saveKuechenKalender(tbTabelle.getItems());
            dataHasBeenModified = false;
        }
        if(comboBoxJahrAuswahl.getSelectionModel().isEmpty()) return;
        ArrayList<KuechenKalenderTag> kuechenListe = DatenbankCommunicator.readKuechenKalenderTage(comboBoxJahrAuswahl.getSelectionModel().getSelectedItem());
        tbTabelle.getItems().setAll(kuechenListe);
    }
}

