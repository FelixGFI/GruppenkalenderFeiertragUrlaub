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
    @FXML protected void onBtVorherigerMonatClick() {
        Integer monthChange = -1;
        if (!monthChangeOperationShouldbeContinued(firstOfCurrentMonth, monthChange)) return;
        firstOfCurrentMonth = changeMonthBackOrForthBy(monthChange, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
    }
    @FXML protected void onBtNaechsterMonatClick() {
        Integer monthChange = 1;
        if (!monthChangeOperationShouldbeContinued(firstOfCurrentMonth, monthChange)) return;
        firstOfCurrentMonth = changeMonthBackOrForthBy(monthChange, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
    }
    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("Called onBtAbbrechenClick()");
        if(dataHasBeenModified) {
            if(!getNutzerBestaetigung()) return;
        }
        Stage stage = (Stage) (btAbbrechen.getScene().getWindow());
        stage.close();
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
                dataHasBeenModified = true;
            }
        }
        tbTabelle.refresh();
    }
    @FXML protected void onBtOffenClick() {
        for (KuechenKalenderTag tag : tbTabelle.getSelectionModel().getSelectedItems()) {
            if(tag.getKuecheCurrentlyGeoeffnet() != 2) {
                tag.setKuecheCurrentlyGeoeffnet(1);
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
    @FXML protected void onComboboxJahrAuswahlAction() throws SQLException {
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
     * Die Methode überprüft ob die Tabelle Leer ist. Wenn nicht sorgt sie für das speichern aller änderungen setzt
     * den Entsprechenden Boolean das es keine Uungespeicherten daten gibt. Anschließend liest sie anhand des firstOfCurrentMonth
     * Datums alle Daten für das gewünschte Jahr aus und schreibt sie in die Tabelle
     * @throws SQLException wird geworfen wenn der Datenbankzugriff nicht Ordnungsgemäß funktioniert
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

