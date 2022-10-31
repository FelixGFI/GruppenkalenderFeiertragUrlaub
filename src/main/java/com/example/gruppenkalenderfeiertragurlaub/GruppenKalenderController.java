package com.example.gruppenkalenderfeiertragurlaub;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    String monate[] = { "Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember" };
    Integer jahre[] = {2022, 2023, 2024, 2025, 2026, 2027, 2028, 20229, 2030, 2031, 2032, 2033, 2031, 2034, 2035, 2036, 2037, 2038, 2039, 2040};
    String statusArray[] = {"Anwesend", "Online", "Abwesend", "Berufssschule", "Urlaub"};
    //create a list of integer type
    List<Integer> listJahresAuswahl = new ArrayList<Integer>();
    //create an observable list
    ObservableList<Integer> observableListMonatAuswahl;
//add listener method


    public void initialize() {
        /*observableListMonatAuswahl = FXCollections.observableList(listMonatAuswahl);
        observableListMonatAuswahl.addListener(new ListChangeListener() {
            @Override
//onChanged method
            public void onChanged(ListChangeListener.Change c) {
                System.out.println("Hey, a change occured. . .  ");
            }
        });
//add an item to the observable List
        observableListMonatAuswahl.add(22);
        System.out.println("Size of the observable list is: " + observableListMonatAuswahl.size() );
        listMonatAuswahl.add(44);
        System.out.println("Size of the observable list is: " + observableListMonatAuswahl.size());
        observableListMonatAuswahl.add(66);
        System.out.println("Size of the observable list is: " + observableListMonatAuswahl.size());

        comboBoxJahrAuswahl = new ComboBox<>(observableListMonatAuswahl);*/

        /*listJahresAuswahl.add(1);
        listJahresAuswahl.add(2);
        listJahresAuswahl.add(3);
        comboBoxJahrAuswahl = new ComboBox<>(FXCollections.observableList(listJahresAuswahl));
        System.out.println(comboBoxJahrAuswahl.getSelectionModel().getSelectedItem());*/


        // Create a combo box
        /*comboBoxMonatAuswahl =
                new ComboBox(FXCollections
                        .observableArrayList(monate));/*

         */
        //fügt Alle benötigten Items den Comboxboxen Hinzu
        comboBoxMonatAuswahl.getItems().addAll(monate);
        comboBoxJahrAuswahl.getItems().addAll(jahre);
        comboBoxStatusAuswahl.getItems().addAll(statusArray);

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