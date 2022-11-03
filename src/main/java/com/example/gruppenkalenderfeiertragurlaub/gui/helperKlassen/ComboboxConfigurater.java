package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class ComboboxConfigurater {

    final ArrayList<String> tageListInLocalDateFormat = new ArrayList<>(Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"));
    final ArrayList<String> monateListInLocalDateFormat = new ArrayList<>(Arrays.asList("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"));
    final ArrayList<String> monatListAsDisplayText = new ArrayList<>(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"));
    ArrayList<Integer> jahreList = new ArrayList<>(Arrays.asList(2022, 2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030, 2031, 2032, 2033, 2031, 2034, 2035, 2036, 2037, 2038, 2039, 2040));

    public void configureCBJahrAuswahl(ComboBox<Integer> comboBoxJahrAuswahl) {
        comboBoxJahrAuswahl.getItems().addAll(jahreList);
        comboBoxJahrAuswahl.getSelectionModel().select(jahreList.indexOf(LocalDate.now().getYear()));
    }

    public void configureCBMonatAuswahl(ComboBox<String> comboBoxMonatAuswahl) {
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

    public ComboboxConfigurater() {}
}
