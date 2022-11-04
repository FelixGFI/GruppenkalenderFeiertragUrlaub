package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.UsefulConstants;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

import java.time.LocalDate;

public class ControllerBasisKlasse {
    public static void configureCBJahrAuswahl(ComboBox<Integer> comboBoxJahrAuswahl) {

        comboBoxJahrAuswahl.getItems().addAll(UsefulConstants.getJahreList());
        comboBoxJahrAuswahl.getSelectionModel().select(UsefulConstants.getJahreList().indexOf(LocalDate.now().getYear()));
    }

    public static void configureCBMonatAuswahl(ComboBox<String> comboBoxMonatAuswahl) {

        //fügt Alle benötigten Items den Comboxboxen Hinzu
        comboBoxMonatAuswahl.getItems().addAll(UsefulConstants.getMonateListInLocalDateFormat());

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

    public static void configureCBStatusauswahl(ComboBox<Character> comboBoxStatusAuswahl) {

        comboBoxStatusAuswahl.getItems().addAll(UsefulConstants.getStatusListCharacterFormat());
        comboBoxStatusAuswahl.setCellFactory(colum -> {
            ListCell<Character> cell = new ListCell<>();
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if(newVal != null) {
                    cell.setText(getDisplayMessageForStatus(newVal));
                }
            });
            return cell;
        });
        comboBoxStatusAuswahl.setConverter(new StringConverter<Character>() {
            @Override
            public String toString(Character statusCharacter) {
                String statusDisplayString = "";
                if(statusCharacter != null) {
                    statusDisplayString = getDisplayMessageForStatus(statusCharacter);
                }
                return statusDisplayString;
            }

            @Override
            public Character fromString(String string) {
                return null;
            }
        });
    }

    /**
     * Die Methode bekommt einen Gruppenstatus (welcher Anzeigt was eine Gruppe an einem bestimmten Tag tut)
     * in Form eines Characters. Für diesen Character wird ein Text erzeugt, der in der Gui angezeigt werden kann.
     * @param aktivitaetsStatus
     * @return Text für die Ausgabe in der Gui
     */
    private static String getDisplayMessageForStatus(Character aktivitaetsStatus) {
        int statusIndex = UsefulConstants.getStatusListCharacterFormat().indexOf(aktivitaetsStatus);
        String displayStatus = (statusIndex != -1) ? UsefulConstants.getStatusListDisplayFormat().get(statusIndex) : "";
        return displayStatus;
    }

    /**
     * konvertiert einen String welcher einen Monat in dem Format enthält wie die Classe LocalDate diesen Zurückgibt
     * (Monatsname in Capslock "JANUARY") in einen für String für die Gui Ausgabe.
     * @param localDateMonat
     * @return Text für die Ausgabe in der Gui
     */
    private static String getAnzeigeMonatString(String localDateMonat) {
        int monatsIndex = UsefulConstants.getMonateListInLocalDateFormat().indexOf(localDateMonat);
        return (monatsIndex != -1) ? UsefulConstants.getMonatListAsDisplayText().get(monatsIndex) : "";
    }
}
