package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import java.util.ArrayList;
import java.util.Arrays;

public class UsefulConstants {

    final static ArrayList<String> tageListInLocalDateFormat = new ArrayList<>(Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"));
    final static ArrayList<String> statusListDisplayFormat = new ArrayList<>(Arrays.asList("Present Anwesend", "Online Anwesend", "Auswärts", "Berufssschule", "Urlaub"));
    final static ArrayList<Character> statusListCharacterFormat = new ArrayList<>(Arrays.asList('P', 'O', 'A', 'B', 'U'));
    final static ArrayList<String> monateListInLocalDateFormat = new ArrayList<>(Arrays.asList("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"));
    final static ArrayList<String> monatListAsDisplayText = new ArrayList<>(Arrays.asList("Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"));
    final static ArrayList<Integer> jahreList = new ArrayList<>(Arrays.asList(2022, 2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030, 2031, 2032, 2033, 2031, 2034, 2035, 2036, 2037, 2038, 2039, 2040));

    public static ArrayList<String> getStatusListDisplayFormat() {
        return statusListDisplayFormat;
    }

    public static ArrayList<Character> getStatusListCharacterFormat() {
        return statusListCharacterFormat;
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
}
