package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class ComboboxConfigurator {
    final ArrayListStorage arrayListStorage = new ArrayListStorage();
    public void configureCBJahrAuswahl(ComboBox<Integer> comboBoxJahrAuswahl) {
        comboBoxJahrAuswahl.getItems().addAll(arrayListStorage.getJahreList());
        comboBoxJahrAuswahl.getSelectionModel().select(arrayListStorage.getJahreList().indexOf(LocalDate.now().getYear()));
    }

    public void configureCBMonatAuswahl(ComboBox<String> comboBoxMonatAuswahl) {
        //fügt Alle benötigten Items den Comboxboxen Hinzu
        comboBoxMonatAuswahl.getItems().addAll(arrayListStorage.getMonateListInLocalDateFormat());

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

    public void configureCBStatusauswahl(ComboBox<Character> comboBoxStatusAuswahl) {
        comboBoxStatusAuswahl.getItems().addAll(arrayListStorage.getStatusListCharacterFormat());
        comboBoxStatusAuswahl.setCellFactory(colum -> {
            ListCell<Character> cell = new ListCell<>();
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if(newVal != null) {
                    cell.setText(getDisplayStatusString(newVal));
                }
            });
            return cell;
        });
        comboBoxStatusAuswahl.setConverter(new StringConverter<Character>() {
            @Override
            public String toString(Character statusCharacter) {
                String statusDisplayString = "";
                if(statusCharacter != null) {
                    statusDisplayString = getDisplayStatusString(statusCharacter);
                }
                return statusDisplayString;
            }

            @Override
            public Character fromString(String string) {
                return null;
            }
        });
    }

    private String getDisplayStatusString(Character newVal) {
        int statusIndex = arrayListStorage.getStatusListCharacterFormat().indexOf(newVal);
        String displayStatus = (statusIndex != -1) ? arrayListStorage.getStatusListDisplayFormat().get(statusIndex) : "";
        return displayStatus;
    }

    private String getAnzeigeMonatString(String localDateMonat) {
        int monatsIndex = arrayListStorage.getMonateListInLocalDateFormat().indexOf(localDateMonat);
        return (monatsIndex != -1) ? arrayListStorage.getMonatListAsDisplayText().get(monatsIndex) : "";
    }
    public ComboboxConfigurator() {}
}
