package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenKalenderTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    String monate[] = { "Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember" };
    Integer jahre[] = {2022, 2023, 2024, 2025, 2026, 2027, 2028, 20229, 2030, 2031, 2032, 2033, 2031, 2034, 2035, 2036, 2037, 2038, 2039, 2040};
    String statusStringArray[] = {"Present Anwesend", "Online Anwesend", "Auswärts", "Berufssschule", "Urlaub"};
    Character statusCharacterArray[] = {'P', 'O', 'A', 'B', 'U'};

    public void initialize() {

        tcDatum.setCellValueFactory(new PropertyValueFactory<>("datum"));
        tcGruppenStatus.setCellValueFactory(new PropertyValueFactory<>("gruppenstatus"));
        tcEssenVerfuegbar.setCellValueFactory(new PropertyValueFactory<>("essenFuerGruppeVerfügbar"));

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
                    for (int i = 0; i < statusCharacterArray.length; i++) {
                        if(newVal == statusCharacterArray[i]) {
                            cell.setText(statusStringArray[i]);
                            break;
                        }
                    }
                }
            });
            return cell;
        });

        tbTabelle.getItems().add(new GruppenKalenderTag(1, LocalDate.now(), 'B', false));
        tbTabelle.getItems().add(new GruppenKalenderTag(1, LocalDate.now().minusDays(5), 'O', false));
        tbTabelle.getItems().add(new GruppenKalenderTag(2, LocalDate.now(), 'P', true));
        tbTabelle.getItems().add(new GruppenKalenderTag(3, LocalDate.now().plusDays(1), 'U', false));
        tbTabelle.getItems().add(new GruppenKalenderTag(3,LocalDate.now().plusDays(2), 'A', false));

        //fügt Alle benötigten Items den Comboxboxen Hinzu
        comboBoxMonatAuswahl.getItems().addAll(monate);
        comboBoxJahrAuswahl.getItems().addAll(jahre);
        comboBoxStatusAuswahl.getItems().addAll(statusStringArray);

        //Setzt den Akktuellen Monat/das Aktuelle Jahr als Vorauswahl
        comboBoxMonatAuswahl.getSelectionModel().select(LocalDate.now().getMonthValue()-1);
        comboBoxJahrAuswahl.getSelectionModel().select(LocalDate.now().getYear()-jahre[0]);

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
        System.out.println(comboBoxMonatAuswahl.getSelectionModel().getSelectedItem());
        System.out.println("Klick Speichern");
    }

    @FXML protected void onBtUebernehmenClick() {
        System.out.println("Klick Übernehmen");
    }
}