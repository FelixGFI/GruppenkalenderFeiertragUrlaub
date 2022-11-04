package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.BetriebsurlaubsTag;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenKalenderTag;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TableColumnConfigurator {
    final UsefulConstants usefulConstants = new UsefulConstants();

    public TableColumnConfigurator() {}

    public void configureBooleanTableColum(TableColumn tableColumnBoolean, String columAttributeName ) {

        tableColumnBoolean.setCellValueFactory(new PropertyValueFactory<>(columAttributeName));
         /* sets the CellFactory (not to be confused with the CellValueFactory) which is responseble
        for determening the format in which the data (which are set using CellVlaueFactory) is displayed
         */
        tableColumnBoolean.setCellFactory(colum -> {
            TableCell<BetriebsurlaubsTag, Boolean> cell = new TableCell<>();
             /* Because during at this point there are no Values in the table yet, becaus this is the
        initilize method, we add a Listener on the cell which we are settign the format on
        If i understand it correctly this listens for any action, e. g. if a value is inserted
        it then checks this value and if the value is not null it it procedes
        this day is a day of Betriebsurlaub then it displays the word "Ja" in the cell instaed of the
        acctual value "true" Otherwise it displays the word "Nein" instead of the Value "false"
         */
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if(newVal != null) {
                    //Ternärer Ausdruck
                    //TODO add Ternärer Ausdruck into Other Controller Classes
                    cell.setText( (newVal == true) ? "Ja" : "Nein");
                }
            });
            return cell;
        });
    }

    public void configureLocalDateTableColum(TableColumn tableColumnLocalDate, String columAttributeName) {
        tableColumnLocalDate.setCellValueFactory(new PropertyValueFactory<>(columAttributeName));
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
        tableColumnLocalDate.setCellFactory(colum -> {
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
    public void configureGruppenStatusTableColum(TableColumn gruppenStatusColum, String columnAttributeName) {
        gruppenStatusColum.setCellValueFactory(new PropertyValueFactory<>(columnAttributeName));
        /*for Documentation to CellFactory see BetriebsurlaubController
        here the only diffrence is that depending on the carracter the full word it is suposed to
        represent is displayed in the cell instead of just the Character. For this it uses the
        statusCharacterArray which contains all the Possible Chars as well as the statusStringArray which
        contains all the possible equivalents as Strings in each with the same index as the coresponding
        Character in the statusCharacterArray
         */
        gruppenStatusColum.setCellFactory(colum -> {
            TableCell<GruppenKalenderTag, Character> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if(newVal != null) {
                    int statusIndex = usefulConstants.getStatusListCharacterFormat().indexOf(newVal);
                    cell.setText((statusIndex != -1) ? usefulConstants.getStatusListDisplayFormat().get(statusIndex) : "");
                }
            });
            return cell;
        });
    }
}
