package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.ComboboxConfigurater;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.BetriebsurlaubsTag;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BetriebsurlaubController {

    @FXML Button btSpeichern;
    @FXML Button btAbbrechen;
    @FXML Button btUebernehmen;
    @FXML Button btVorigerMonat;
    @FXML Button btNaechesterMonat;

    @FXML ComboBox<String> comboBoxMonatAuswahl;
    @FXML ComboBox<Integer> comboBoxJahrAuswahl;

    @FXML DatePicker dpVon;
    @FXML DatePicker dpBis;

    @FXML TableColumn<BetriebsurlaubsTag, LocalDate> tcDatum;

    @FXML TableColumn<BetriebsurlaubsTag, Boolean> tcIstBetriebsurlaub;

    @FXML TableView<BetriebsurlaubsTag> tbTabelle;

 public void initialize() {

     configureTableView();


     tbTabelle.getItems().add(new BetriebsurlaubsTag(LocalDate.now(), false));
     tbTabelle.getItems().add(new BetriebsurlaubsTag(LocalDate.now().minusMonths(1), true));

     ComboboxConfigurater comboboxConfigurater = new ComboboxConfigurater();
     comboboxConfigurater.configureCBJahrAuswahl(comboBoxJahrAuswahl);
     comboboxConfigurater.configureCBMonatAuswahl(comboBoxMonatAuswahl);
    }

    private void configureTableView() {
        //Wichtig! FXML object (wie table Colums, Table View, Buttons etc. Nicht neu initialisieren/überschreiben
        //Weil das FXML object im code ja schon ein UI element referenziert.

        tcDatum.setCellValueFactory(
                new PropertyValueFactory<>("datum"));

        tcIstBetriebsurlaub.setCellValueFactory(
                new PropertyValueFactory<>("isBetriebsurlaub"));

        /* sets the CellFactory (not to be confused with the CellValueFactory) which is responseble
        for determening the format in which the data (which are set using CellVlaueFactory) is displayed
         */
        tcIstBetriebsurlaub.setCellFactory(colum -> {
            TableCell<BetriebsurlaubsTag, Boolean> cell = new TableCell<>();
        /* Because during at this point there are no Values in the table yet, becaus this is the
        initilize method, we add a Listener on the cell which we are settign the format on
        If i understand it correctly this listens for any action, e. g. if a value is inserted
        it then checks this value and if the value is not null it it procedes
        this day is a day of Betriebsurlaub then it displays the word "Ja" in the cell instaed of the
        acctual value "true" Otherwise it displays the word "Nein" instead of the Value "false"
         */
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if (newVal != null) {
                    if(newVal == true) {
                        cell.setText("Ja");
                    } else {
                        cell.setText("Nein");
                    }
                }
            });

            return cell;
        });

        /*
        This works in a much simmular manner then mentiond above. The only diffrence beeing
        that instead of setting a text directly based on conditions, this time, once the
        Listener detacts an action, it checks if the Value detected in the cell
        is != null and then, asuming it is indeed != null, it takes the value (which is a LocalDate)
        and formats it using a dateTimeFormatter and sets it as the text to display in the cell.
        The best thing is, becaus this sets a Listener, It also Automaticaly works with
        any rows you add later! Isn't that wonderfull? (newVal is in this case the new Value of the cell.
        As I understand it, the Listener ist allways called if there is a change to the cell so it
        should work with updating things as well I assume)
         */
        tcDatum.setCellValueFactory(
                new PropertyValueFactory<>("datum"));

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
        try {
            System.out.println(tbTabelle.getSelectionModel().getSelectedItem().getIsBetriebsurlaub());
            System.out.println(tbTabelle.getSelectionModel().getSelectedItem().getDatum());
        } catch (Exception e) {

        }

        tbTabelle.getItems().add(new BetriebsurlaubsTag(LocalDate.now().plusDays(5), true));
        System.out.println("Klick Speichern");
    }
    @FXML protected void onBtUebernehmenClick() {
        System.out.println("Klick Übernehmen");
    }
}
