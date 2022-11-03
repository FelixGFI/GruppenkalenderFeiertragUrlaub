package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayListStorage {

    final ArrayList<String> tageListInLocalDateFormat = new ArrayList<>(Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"));
    final ArrayList<String> statusListDisplayFormat = new ArrayList<>(Arrays.asList("Present Anwesend", "Online Anwesend", "Auswärts", "Berufssschule", "Urlaub"));
    final ArrayList<Character> statusListCharacterFormat = new ArrayList<>(Arrays.asList('P', 'O', 'A', 'B', 'U'));
    final ArrayList<String> monateListInLocalDateFormat = new ArrayList<>(Arrays.asList("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"));
    final ArrayList<String> monatListAsDisplayText = new ArrayList<>(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"));
    final ArrayList<Integer> jahreList = new ArrayList<>(Arrays.asList(2022, 2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030, 2031, 2032, 2033, 2031, 2034, 2035, 2036, 2037, 2038, 2039, 2040));

    public ArrayListStorage() {}

    public ArrayList<String> getStatusListDisplayFormat() {
        return statusListDisplayFormat;
    }

    public ArrayList<Character> getStatusListCharacterFormat() {
        return statusListCharacterFormat;
    }

    public ArrayList<String> getMonateListInLocalDateFormat() {
        return monateListInLocalDateFormat;
    }

    public ArrayList<String> getMonatListAsDisplayText() {
        return monatListAsDisplayText;
    }

    public ArrayList<Integer> getJahreList() {
        return jahreList;
    }

    public ArrayList<String> getTageListInLocalDateFormat() {
        return tageListInLocalDateFormat;
    }
}
