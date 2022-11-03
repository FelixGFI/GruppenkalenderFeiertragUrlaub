package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.BetriebsurlaubsTag;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.KuechenKalenderTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

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

    final ArrayList<String> tageListInLocalDateFormat = new ArrayList<>(Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"));
    final ArrayList<String> monateListInLocalDateFormat = new ArrayList<>(Arrays.asList("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"));
    final ArrayList<String> monatListAsDisplayText = new ArrayList<>(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"));
    ArrayList<Integer> jahreList = new ArrayList<>(Arrays.asList(2022, 2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030, 2031, 2032, 2033, 2031, 2034, 2035, 2036, 2037, 2038, 2039, 2040));

    public void initialize() {

        configureTableView();

        tbTabelle.getItems().add(new KuechenKalenderTag(LocalDate.now().plusDays(10), true));
        tbTabelle.getItems().add(new KuechenKalenderTag(LocalDate.now(), false));

        configureCBMonatAuswahl();
        configureCBJahrAuswahl();
    }

    private void configureCBJahrAuswahl() {
        comboBoxJahrAuswahl.getItems().addAll(jahreList);
        comboBoxJahrAuswahl.getSelectionModel().select(jahreList.indexOf(LocalDate.now().getYear()));
    }

    private void configureCBMonatAuswahl() {
        //fügt Alle benötigten Items den Comboxboxen Hinzu
        comboBoxMonatAuswahl.getItems().addAll(monateListInLocalDateFormat);

        comboBoxMonatAuswahl.setCellFactory(colum -> {
            ListCell<String> cell = new ListCell<>();
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if(newVal != null) {
                    cell.setText(getAnzeigeMonatString(newVal));
                }
            });
            return cell;
        });
        comboBoxMonatAuswahl.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String localDateMonat) {
                String anzeigeMonat = "";
                if(localDateMonat != null) {
                    anzeigeMonat = getAnzeigeMonatString(localDateMonat);
                }
                return anzeigeMonat;
            }

            @Override
            public String fromString(String string) {
                return null;
            }
        });
        //Setzt den Akktuellen Monat/das Aktuelle Jahr als Vorauswahl
        comboBoxMonatAuswahl.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
    }

    private String getAnzeigeMonatString(String localDateMonat) {
        int monatsIndex = monateListInLocalDateFormat.indexOf(localDateMonat);
        return (monatsIndex != -1) ? monatListAsDisplayText.get(monatsIndex) : "";
    }

    private void configureTableView() {
        tcDatum.setCellValueFactory(new PropertyValueFactory<>("datum"));
        //for Documentation on CellFactory usage see BetriebsurlaubController
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

        tcKuecheOffen.setCellValueFactory(new PropertyValueFactory<>("kuecheGeoeffnet"));
        tcKuecheOffen.setCellFactory(colum -> {
            TableCell<BetriebsurlaubsTag, Boolean> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if(newVal != null) {
                    //Ternärer Ausdruck
                    //TODO add Ternärer Ausdruck into Other Controller Classes
                    cell.setText( (newVal == true) ? "Ja" : "Nein");
                }
            });
            return cell;
        });
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

