package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.ComboboxConfigurater;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenKalenderTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

public class GruppenKalenderController {

    @FXML Button btSpeichern;
    @FXML Button btAbbrechen;
    @FXML Button btUebernehmen;
    @FXML Button btVorherigerMonat;
    @FXML Button btNaechsterMonat;

    @FXML ComboBox<String> comboBoxMonatAuswahl;
    @FXML ComboBox<Integer> comboBoxJahrAuswahl;
    @FXML ComboBox<String> comboBoxGruppenAuswahl;
    @FXML ComboBox<String> comboBoxStatusAuswahl;

    @FXML DatePicker dpVon;
    @FXML DatePicker dpBis;

    @FXML TableView<GruppenKalenderTag> tbTabelle;

    @FXML TableColumn<GruppenKalenderTag, LocalDate> tcDatum;
    @FXML TableColumn<GruppenKalenderTag, Character> tcGruppenStatus;
    @FXML TableColumn<GruppenKalenderTag, Boolean> tcEssenVerfuegbar;

    final ArrayList<String> tageListInLocalDateFormat = new ArrayList<>(Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"));
    ArrayList<String> statusListDisplayFormat = new ArrayList<>(Arrays.asList("Present Anwesend", "Online Anwesend", "Auswärts", "Berufssschule", "Urlaub"));
    ArrayList<Character> statusListCharacterFormat = new ArrayList<>(Arrays.asList('P', 'O', 'A', 'B', 'U'));

    public void initialize() {

        configureTableView();

        tbTabelle.getItems().add(new GruppenKalenderTag(1, LocalDate.now(), 'B', false));
        tbTabelle.getItems().add(new GruppenKalenderTag(1, LocalDate.now().minusDays(5), 'O', false));
        tbTabelle.getItems().add(new GruppenKalenderTag(2, LocalDate.now(), 'P', true));
        tbTabelle.getItems().add(new GruppenKalenderTag(3, LocalDate.now().plusDays(1), 'U', false));
        tbTabelle.getItems().add(new GruppenKalenderTag(3,LocalDate.now().plusDays(2), 'A', false));

        ComboboxConfigurater comboboxConfigurater = new ComboboxConfigurater();
        comboboxConfigurater.configureCBMonatAuswahl(comboBoxMonatAuswahl);
        comboboxConfigurater.configureCBJahrAuswahl(comboBoxJahrAuswahl);

        comboBoxStatusAuswahl.getItems().addAll(statusListDisplayFormat);

    }
    private void configureTableView() {
        tcDatum.setCellValueFactory(new PropertyValueFactory<>("datum"));
        //for Documantation see BetriebsurlaubController
        tcDatum.setCellFactory(colum -> {
            TableCell<GruppenKalenderTag, LocalDate> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if(newVal != null) {
                    cell.setText(newVal.format(DateTimeFormatter
                            .ofPattern("dd.MM.yyy")));
                }
            });

            return cell;
        });

        tcEssenVerfuegbar.setCellValueFactory(new PropertyValueFactory<>("essenFuerGruppeVerfügbar"));
        //for Documantation see BetriebsurlaubController
        tcEssenVerfuegbar.setCellFactory(colum -> {
            TableCell<GruppenKalenderTag, Boolean> cell = new TableCell<>();
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

        tcGruppenStatus.setCellValueFactory(new PropertyValueFactory<>("gruppenstatus"));
        /*for Documentation to CellFactory see BetriebsurlaubController
        here the only diffrence is that depending on the carracter the full word it is suposed to
        represent is displayed in the cell instead of just the Character. For this it uses the
        statusCharacterArray which contains all the Possible Chars as well as the statusStringArray which
        contains all the possible equivalents as Strings in each with the same index as the coresponding
        Character in the statusCharacterArray
         */
        tcGruppenStatus.setCellFactory(colum -> {
            TableCell<GruppenKalenderTag, Character> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if(newVal != null) {
                    int statusIndex = statusListCharacterFormat.indexOf(newVal);
                    cell.setText((statusIndex != -1) ? statusListDisplayFormat.get(statusIndex) : "");
                }
            });
            return cell;
        });
    }

    @FXML protected void onBtVorherigerMonatClick() {
        System.out.println("Klick Vorheriger Monat");
    }

    @FXML protected void onBtNaechsterMonatClick() {
        System.out.println("Klick Naechster Monat");
    }

    @FXML protected void onBtAbbrechenClick() {
        System.out.println("Klick Abbrechen");
    }

    @FXML protected void onBtSpeichernClick() {

        System.out.println("Klick Speichern");
    }

    @FXML protected void onBtUebernehmenClick() {
        System.out.println("Klick Übernehmen");
    }
}