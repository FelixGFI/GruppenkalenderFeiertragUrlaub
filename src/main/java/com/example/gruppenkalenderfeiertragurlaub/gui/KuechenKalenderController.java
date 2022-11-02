package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.BetriebsurlaubsTag;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.KuechenKalenderTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class KuechenKalenderController {

    @FXML
    Button btSpeichern;
    @FXML
    Button btAbbrechen;
    @FXML
    Button btGeschlossen;
    @FXML
    Button btOffen;
    @FXML
    Button btNaechsterMonat;
    @FXML
    Button btVorigerMonat;

    @FXML
    ComboBox<String> comboBoxMonatAuswahl;
    @FXML
    ComboBox<Integer> comboBoxJahrAuswahl;

    @FXML
    DatePicker dpVon;
    @FXML
    DatePicker dpBis;

    @FXML TableView<KuechenKalenderTag> tbTabelle;

    @FXML TableColumn<BetriebsurlaubsTag, LocalDate> tcDatum;
    @FXML TableColumn<BetriebsurlaubsTag, Boolean> tcKuecheOffen;

    String monate[] = {"Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"};
    Integer jahre[] = {2022, 2023, 2024, 2025, 2026, 2027, 2028, 20229, 2030, 2031, 2032, 2033, 2031, 2034, 2035, 2036, 2037, 2038, 2039, 2040};

    public void initialize() {

        tcDatum.setCellValueFactory(new PropertyValueFactory<>("datum"));
        tcKuecheOffen.setCellValueFactory(new PropertyValueFactory<>("kuecheGeoeffnet"));


        //for Documentation on CellFactory ussage see BetriebsurlaubController
        tcDatum.setCellFactory(colum -> {
            TableCell<BetriebsurlaubsTag, LocalDate> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if(newVal != null) {
                    cell.setText(newVal.format(DateTimeFormatter
                            .ofPattern("dd.MM.yyy")));
                }
            });
            return cell;
        });

        tcKuecheOffen.setCellFactory(colum -> {
            TableCell<BetriebsurlaubsTag, Boolean> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if(newVal != null) {
                    if(newVal == true) {
                        cell.setText("Ja");
                    } else {
                        cell.setText("Nein");
                    }
                }
            });
            return cell;
        });

        tbTabelle.getItems().add(new KuechenKalenderTag(LocalDate.now().plusDays(10), true));
        tbTabelle.getItems().add(new KuechenKalenderTag(LocalDate.now(), false));

        //fügt Alle benötigten Items den Comboxboxen Hinzu
        comboBoxMonatAuswahl.getItems().addAll(monate);
        comboBoxJahrAuswahl.getItems().addAll(jahre);

        //Setzt den Akktuellen Monat/das Aktuelle Jahr als Vorauswahl
        comboBoxMonatAuswahl.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
        comboBoxJahrAuswahl.getSelectionModel().select(LocalDate.now().getYear() - jahre[0]);

    }

    @FXML
    protected void onBtVorherigerMonatClick() {
        System.out.println("Klick Vorheriger Monat");
    }

    @FXML
    protected void onBtNaechsterMonatClick() {
        System.out.println("Klick Naechster Monat");
    }

    @FXML
    protected void onBtAbbrechenClick() {
        System.out.println("Klick Abbrechen");
    }

    @FXML
    protected void onBtSpeichernClick() {
        System.out.println(comboBoxMonatAuswahl.getSelectionModel().getSelectedItem());
        System.out.println("Klick Speichern");
    }
    @FXML protected void onBtGeschlossenClick() {
        System.out.println("Klick Geschlossen");
    }
    @FXML protected void onBtOffenClick() {
        System.out.println("Klick Geschlossen");
    }
}

