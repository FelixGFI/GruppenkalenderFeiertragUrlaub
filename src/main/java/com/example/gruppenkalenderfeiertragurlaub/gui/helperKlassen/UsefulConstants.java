package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.BetriebsurlaubsTag;

import java.util.ArrayList;
import java.util.Arrays;

public class UsefulConstants {
    /**
     * diese Klasse enthält eine Reihe von Konstanten für das Programm
     * -
     * TageListeInLocalDateFormat:              die Fünf Arbeitstage der Woche in dem Format wie sie die Klasse LocalDate verwendet
     * statusListCharacterFormat:               Liste der möglichen Gruppenstatuse (Was eine bestimmte Gruppe/Gruppenfamilie dem Klander
     *                                          entsprechend an einem Tag tut) im Character Format, zur verwendung in Objekten der Klasse
     *                                          GruppenKalender Tagsowie in der Datenbank.
     * statusListCharacterFormatOhneFeiertag    beihnahe Identisch zu statusListCharacterFormat mit der Ausnahme das der Character 'F' für
     *                                          "Feiertag" nicht vorkommt. Diese ArrayList wird für die Configuration der Statusauswahl
     *                                          Combobox verwendet bei der man aus offensichtlichem Grund nicht den Status gesetzlicher Feirtag
     *                                          einstellen können soll.
     * statusListDisplayFormat:                 liste von Strings zur Anzeige in der Gui welche den Gruppenstatus verständlich beschrieben
     * monateListInLocalDateFormat:             Enthält die Zwölf Monate in dem Format wie sie die Klasse LocalDate verwendet
     * monatListAsDisplayText:                  Enthält die Zwölf Monate wie sie in der Gui angezeigt werden sollen
     * jahreList:                               Enthält alle auswählbaren Jahre (at the time of Writing alle jahre von 2022 bis einschließlich 2040
     */
    final static ArrayList<String> tageListInLocalDateFormat = new ArrayList<>(Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"));
    final static ArrayList<Character> statusListCharacterFormat = new ArrayList<>(Arrays.asList('P', 'O', 'A', 'B', 'U', 'N', 'F'));
    final static ArrayList<Character> statusListCharacterFormatOhneFeiertag = new ArrayList<>(Arrays.asList('P', 'O', 'A', 'B', 'U', 'N'));
    final static ArrayList<String> statusListDisplayFormat = new ArrayList<>(Arrays.asList("Present Anwesend", "Online Anwesend", "Auswärts", "Berufssschule", "Urlaub", "N/A", "Gesetzlicher Feiertag"));
    final static ArrayList<String> monateListInLocalDateFormat = new ArrayList<>(Arrays.asList("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"));
    final static ArrayList<String> monatListAsDisplayText = new ArrayList<>(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"));
    final static ArrayList<Integer> jahreList = new ArrayList<>(Arrays.asList(2022, 2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030, 2031, 2032, 2033, 2031, 2034, 2035, 2036, 2037, 2038, 2039, 2040));
    final static Character defaultStatus = getStatusListCharacterFormat().get(5);

    public static ArrayList<String> getStatusListDisplayFormat() {
        return statusListDisplayFormat;
    }
    public static ArrayList<Character> getStatusListCharacterFormat() {
        return statusListCharacterFormat;
    }
    public static ArrayList<Character> getStatusListCharacterFormatOhneFeiertag() {
        return statusListCharacterFormatOhneFeiertag;
    }
    public static ArrayList<String> getMonateListInLocalDateFormat() {
        return monateListInLocalDateFormat;
    }
    public static ArrayList<String> getMonatListAsDisplayText() {
        return monatListAsDisplayText;
    }
    public static ArrayList<Integer> getJahreList() {
        return jahreList;
    }
    public static ArrayList<String> getTageListInLocalDateFormat() {
        return tageListInLocalDateFormat;
    }
    public static Character getDefaultStatus() {return defaultStatus;}

}
