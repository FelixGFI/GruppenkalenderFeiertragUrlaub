package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.UsefulConstants;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.*;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ControllerBasisKlasse {

    /**
     * fügt in die Übergebene Combobox<Integer> alle in der verwendeten Arraylist enthalten Jahre hinzu
     * (at the time of writing alle Jahre von 2022 bis einschließlich 2040) wählt das akktuelle Jahra ls
     * Standard vorauswahl aus.
     * @param comboBoxJahrAuswahl
     */
    public static void configureCBJahrAuswahl(ComboBox<Integer> comboBoxJahrAuswahl) {

        comboBoxJahrAuswahl.getItems().addAll(UsefulConstants.getJahreList());
        comboBoxJahrAuswahl.getSelectionModel().select(UsefulConstants.getJahreList().indexOf(LocalDate.now().getYear()));
    }

    /**
     * Fügt alle Zwölf Monat in dem Format wie die Klasse LocalDate diese Verwendet (englisch und Capslock "JANUARY") in
     * die übergebene Combobox<String> hinzu.
     * Sorgt dafür das für Jeden Monat sowohl wenn augewählt als auch wenn in der Auswahlliste angezeigt,
     * String mit dem Deutschen Monatsnamen angezeigt wird. (Januar). Wählt den Akktulen Monat als default aus.
     * @param comboBoxMonatAuswahl
     */
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
        comboBoxMonatAuswahl.setConverter(new StringConverter<>() {
            @Override
            public String toString(String localDateMonat) {
                String anzeigeMonat = "";
                if (localDateMonat != null) {
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

    /**
     * fügt der übergbenen Combobox<Character> Character hinzu welche die auswählbaren Gruppenstatuse representieren
     * (d. h. was eine Gruppe an einem Bestimmten Tag laut kalender tut). Legt fest das für jeden
     * Character ein Entsprchender String welcher den gruppenstatus beschreibt in der Gui angezeigt wird, sohwohl
     * wenn ausgewählt als auch in er Auswahl liste der Combobox
     * @param comboBoxStatusAuswahl
     */
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
        comboBoxStatusAuswahl.setConverter(new StringConverter<>() {
            @Override
            public String toString(Character statusCharacter) {
                String statusDisplayString = "";
                if (statusCharacter != null) {
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
        return (statusIndex != -1) ? UsefulConstants.getStatusListDisplayFormat().get(statusIndex) : "";
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

    /**
     * Konfiguriert die Übergebene TableColum<Klassenname, Boolean> so das der als String übergebene Attributname
     * als anzuzeigendes Attribut festgelegt wird. stellt sicher das in jeder Zelle der TableColum für den
     * Boolean wert "true" der String "Ja" und für den Boolean wert "false" der String "Nein" angezeigt wird
     * @param tableColumnBoolean
     * @param columAttributeName
     */
    public static void configureBooleanTableColum(TableColumn tableColumnBoolean, String columAttributeName) {

        tableColumnBoolean.setCellValueFactory(new PropertyValueFactory<>(columAttributeName));
         /* sets the CellFactory (not to be confused with the CellValueFactory) which is responseble
        for determening the format in which the data (which are set using CellVlaueFactory) is displayed
         */

        tableColumnBoolean.setCellFactory(colum -> {
            TableCell<BetriebsurlaubsTag, Boolean> cell = new TableCell<>();

             /* Because during at this point there are no Values in the table yet, because this is the
               initialize method, we add a Listener on the cell which we are setting the format on
        If i understand it correctly this listens for any action, e.g. if a value is inserted
        it then checks this value and if the value is not null  it proceeds
        this day is a day of Betriebsurlaub then it displays the word "Ja" in the cell instead of the
        actual value "true" Otherwise it displays the word "Nein" instead of the Value "false"
         */
            cell.itemProperty().addListener((obs, old, newBooleanVal) -> {
                if (newBooleanVal != null) {
                    //Ternärer Ausdruck
                    cell.setText((newBooleanVal) ? "Ja" : "Nein");
                }
            });
            return cell;
        });
    }

    /**
     * Konfiguriert für die Übergebene tabelColum<Klassenname, LocalDate> das als String übergebenen Attributnamen
     * als das in dieser Colum anzuzeigende Attribut. Formatiert das anzuzeigende LocalDate entsprechend dem in Deutschland
     * üblichen Format (dd.MM.yyyy) so das es in diesem Format in der Gui angezeigt wird.
     * @param tableColumnLocalDate
     * @param columAttributeName
     */
    public static void configureLocalDateTableColum(TableColumn tableColumnLocalDate, String columAttributeName) {
        tableColumnLocalDate.setCellValueFactory(new PropertyValueFactory<>(columAttributeName));
        /*
        This works in a much simmular manner then mentiond above. The only diffrence beeing
        that instead of setting a text directly based on conditions, this time, once the
        Listener detacts an action, it checks if the Value detected in the cell
        is != null and then, asuming it is indeed != null, it takes the value (which is a LocalDate)
        and formats it using a dateTimeFormatter and sets it as the text to display in the cell.
        The best thing is, becaus this sets a Listener, It also Automatically works with
        any rows you add later! Isn't that wonderfull? (newVal is in this case the new Value of the cell.
        As I understand it, the Listener ist allways called if there is a change to the cell so it
        should work with updating things as well I assume)
         */
        tableColumnLocalDate.setCellFactory(colum -> {
            TableCell<BetriebsurlaubsTag, LocalDate> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if (newVal != null) {
                    cell.setText(newVal.format(DateTimeFormatter
                            .ofPattern("dd.MM.yyy")));
                }
            });
            return cell;
        });
    }

    /**
     * Konfiguriert die Übergebene TableColum<Klassenname, Character>. Setzt den Übergebenen String als anzuzeigendes
     * Attribut und findet für Jeden angezeigten Character, welcher den Gruppenstatus definiert
     * (Was die Gruppe an einem Tag gemacht hat) den Entsprechenden String der in der Gui angezeigt werden soll.
     * Dieser String wird statt des Characters in der jeweiligen Zelle angezeigt.
     * @param gruppenStatusColum
     * @param columnAttributeName
     */
    public static void configureGruppenStatusTableColum(TableColumn gruppenStatusColum, String columnAttributeName) {
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
                if (newVal != null) {
                    cell.setText(getDisplayMessageForStatus(newVal));
                }
            });
            return cell;
        });
    }

    /**
     * Erhält eine Combobox<Object>. Füllt die ArrayList<GruppenFamileienFuerkalender> gruppenFamilienListe mit Allen Gruppenfamilien aus der Datenbank
     * In den Gruppenfamilien sind alle zu ihnen Gehörigen Gruppen bereits enthalten. Fügt der Combobox alle Gruppenfamilien sowie die zugehörigen
     * Gruppen hinzo wobei die Gruppenfamilien stets in der Liste über den Gruppen stehen die zu Ihnen gehören. Stellt sicher das in der
     * Liste für Jede Gruppe/Gruppenfamilie dernen Name dem Nutzer Angeziegt wird, sowohl in der Liste als auch wenn Ausgewählt.
     * Stellt sicher das in der Liste die Gruppenfamilien Fett Gedruckt geschrieben sind sodas sie von Gruppen leicht zu
     * unterscheiden sind.
     * @param comboBoxGruppenAuswahl
     * @return ArrayList der Ausgelesenen Gruppenfamilien von denen Jede alle zu ihr gehörenden Gruppen enthält.
     * @throws SQLException
     */
    public static ArrayList<GruppenFamilieFuerKalender> configureCBGruppenAuswahl(ComboBox comboBoxGruppenAuswahl) throws SQLException {
        DatenbankCommunicator.establishConnection();
        ArrayList<GruppenFamilieFuerKalender> gruppenFamilienListe = DatenbankCommunicator.getAllGruppenFamilienUndGruppen();
        for (GruppenFamilieFuerKalender grFa : gruppenFamilienListe) {
            comboBoxGruppenAuswahl.getItems().add(grFa);
            for (GruppeFuerKalender gr : grFa.getGruppenDerFamilie()) {
                comboBoxGruppenAuswahl.getItems().add(gr);
            }
        }

        comboBoxGruppenAuswahl.setCellFactory(colum -> {
            ListCell<Object> cell = new ListCell<>();
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if(newVal != null) {
                    if(newVal.getClass() == GruppeFuerKalender.class) {
                        cell.setText(((GruppeFuerKalender) newVal).getGruppeName());
                        cell.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, Font.getDefault().getSize()));
                    } else {
                        cell.setText(((GruppenFamilieFuerKalender) newVal).getFamilieName());
                        cell.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));
                    }

                }
            });
            return cell;
        });

        comboBoxGruppenAuswahl.setConverter(new StringConverter<Object>() {
            @Override
            public String toString(Object gruppeOrGruppenFamilie) {
                String gruppeNameString = "";
                if(gruppeOrGruppenFamilie != null) {
                    if(gruppeOrGruppenFamilie.getClass() == GruppeFuerKalender.class) {
                        gruppeNameString = ((GruppeFuerKalender) gruppeOrGruppenFamilie).getGruppeName();

                    } else {
                        gruppeNameString = ((GruppenFamilieFuerKalender) gruppeOrGruppenFamilie).getFamilieName();

                    }
                }

                return gruppeNameString;
            }

            @Override
            public Character fromString(String string) {
                return null;
            }
        });

        return gruppenFamilienListe;
    }

    /**
     * erhält eine TableColumn<GruppenKalenderTag, Integer> und configuriert diese sodas sie die GruppeID enthält
     * verwendet außerdem eine CellFactory um für Jede gruppenID, aus der Übergebenen ArrayList, den Richtigen
     * Gruppennamen auszulesen und besagten in der Tabelle anzuzeigen.
     * @param tcGruppenBezeichung
     * @param columnAttributeName
     * @param gruppenListe
     */
    public static void configureGruppenBezeichnungTableColum(TableColumn tcGruppenBezeichung, String columnAttributeName, ArrayList<GruppeFuerKalender> gruppenListe) {

        tcGruppenBezeichung.setCellValueFactory(new PropertyValueFactory<>(columnAttributeName));
        tcGruppenBezeichung.setCellFactory(colum -> {
            TableCell<BetriebsurlaubsTag, Integer> cell = new TableCell<>();

            cell.itemProperty().addListener((obs, old, newVal) -> {
                if (newVal != null) {
                    cell.setText("");
                    for (GruppeFuerKalender gr : gruppenListe) {
                        if(gr.getGruppeId() == newVal) {
                            cell.setText(gr.getGruppeName());
                            break;
                        }
                    }
                }
            });
            return cell;
        });
    }
    /**
     * Die Methode soll in der Tabelle eine Reihe von einträgen welche Zwischen zwei LocalDate Daten liegen auswählen
     * (einschließlich der Zwei Daten selbst). Hierfür überprüft es bei Jedem Objekt in der Tabelle ob dessen datum Entweder
     * dem vonDatum oder bisDatum entspricht oder zwischen den Beiden liegt. Jedes so gefunden Objekt wird selected.
     * Scrollt zum Ersten Gefundenen Reihe Welches das VonDatum enthält.
     * @param vonDatum
     */
    protected void markAllRowsVonBis(LocalDate vonDatum, LocalDate bisDatum, TableView tbTabelle) {
        tbTabelle.getSelectionModel().clearSelection();
        ObservableList<TagBasisKlasse> tabellenEintraege = tbTabelle.getItems();
        boolean istErstesGefundenesDatum = true;
        for(TagBasisKlasse tag : tabellenEintraege) {
            LocalDate tagesDatum = tag.getDatum();
            boolean tagIsInIbetweenRange = (tagesDatum.isAfter(vonDatum) && tagesDatum.isBefore(bisDatum));
            boolean tagIsVonOrBisDatum = (tagesDatum.toString().equals(vonDatum.toString()) || tagesDatum.toString().equals(bisDatum.toString()));
            if(tagIsInIbetweenRange || tagIsVonOrBisDatum) {
                tbTabelle.getSelectionModel().select(tag);
                if(istErstesGefundenesDatum) {
                    tbTabelle.scrollTo(tabellenEintraege.indexOf(tag));
                    istErstesGefundenesDatum = false;
                }
            }
        }

    }

    /**
     * erhält ein Einzelnes LocalDate und selected in der Tabelle alle Zeilen deren GruppenKalenderTag Objekte dieses Datum enhalten.
     * Scrollt zum Ersten Gefundenen Reihe mit diesem Datum.
     * @param vonDatum
     */
    protected void markRowsOfOneDate(LocalDate vonDatum, TableView tbTabelle) {
        tbTabelle.getSelectionModel().clearSelection();
        ObservableList<TagBasisKlasse> tabellenEintraege = tbTabelle.getItems();
        boolean istErstesGefundenesDatum = true;
        for (TagBasisKlasse tag : tabellenEintraege) {
            if(tag.getDatum().toString().equals(vonDatum.toString())) {
                tbTabelle.getSelectionModel().select(tag);
                if(istErstesGefundenesDatum) {
                    tbTabelle.scrollTo(tabellenEintraege.indexOf(tag));
                    istErstesGefundenesDatum = false;
                }
            }
        }
    }
    /**
     * erhält einen DatePicker und versucht aus diesem ein LocalDate auszulesen. Ist dies erfolgreich so gibt die Methode
     * besagtes datum zurück, ist dies nicht erfolgreich gibt die Methode null zurück
     * @param dp
     * @return ausgelesenes LocalDate wenn erfolgreich, Null wenn nicht erfolgreich
     */
    protected LocalDate leseDatumAusDatePicker(DatePicker dp) {
        LocalDate datum;
        try {
            datum = dp.getValue();
            System.out.println("GruppenKalenderController.onDpVonAction() " + datum);
        } catch (Exception e) {
            datum = null;
        }
        return datum;
    }
    /**
     * erhält ein Datum. liest den Monat dieses Datums aus und sucht darauf hin der der Tabelle nach dem ersten Tag
     * mit einem Datum in diesem Monat. scrollt zu diesem Tag in der Tabelle.
     * @param firstWerktagOfMonth
     */
    protected void scrollToSelectedMonth(LocalDate firstWerktagOfMonth, TableView tbTabelle) {
        String month = firstWerktagOfMonth.getMonth().toString();
        ObservableList<TagBasisKlasse> items = tbTabelle.getItems();
        for(TagBasisKlasse tag : items) {
            if(tag.getDatum().getMonth().toString().equals(month)) {
                tbTabelle.scrollTo(items.indexOf(tag));
                break;
            }
        }
    }
    /**
     * Erhält einen Integer changeNumber und verändert das Datum firstOfCurrentMonth um eine Anzahl von Monaten die
     * der übergebenen ChangNumber Entspricht. Passt die Comboboxen comboBoxMonatAuswahl und gegebenen Falls die
     * comboBoxJahrAuswahl entsprchend dem Verändertne firstOfCurrentMonth an (wodruch die entsprechenden
     * onActions dieser Comboboxen Ausgelöst werden)
     * @param changeNumber
     */
    protected LocalDate changeMonthBackOrForthBy(Integer changeNumber, LocalDate firstOfCurrentMonth, ComboBox comboBoxMonatAuswahl, ComboBox comboBoxJahrAuswahl) {
        // Set new date
        firstOfCurrentMonth = firstOfCurrentMonth.plusMonths(changeNumber);
        if(comboBoxJahrAuswahl.getItems().contains(firstOfCurrentMonth.getYear())) {
            int indexOfYear = comboBoxJahrAuswahl.getItems().indexOf(firstOfCurrentMonth.getYear());
            int indexOfMonth = firstOfCurrentMonth.getMonthValue() - 1;
            comboBoxMonatAuswahl.getSelectionModel().select(indexOfMonth);
            comboBoxJahrAuswahl.getSelectionModel().select(indexOfYear);
        } else {
            firstOfCurrentMonth = firstOfCurrentMonth.minusMonths(changeNumber);
        }
        return firstOfCurrentMonth;
    }

    /**
     * Die Methode ist dazu gedacht von Allen Drei Controllern Innerhalb eines OnAction auf dem DatePicker mit dem Namen dpVon aufgerufen zu
     * werden da der Code hinter diesem In allen Drei Controller Klassen gleich ist. Die Methode liest die Übergebenen datePicker
     * dpVon und dpBis aus und ruft je nach Inhalt der Selben entweder Methoden zum Markieren eines Eizigen Datums oder mehrere Daten in der
     * übergebenen TableView tbTabelle auf oder returned ohne etwas zu tun.
     * @param firstOfCurrentMonth
     * @param dpVon
     * @param dpBis
     * @param tbTabelle
     */

    protected void toBeCalledInOnDpVonAction(LocalDate firstOfCurrentMonth, DatePicker dpVon, DatePicker dpBis, TableView tbTabelle) {
        LocalDate vonDatum = leseDatumAusDatePicker(dpVon);
        if(vonDatum == null) {
            return;
        }
        if(vonDatum.getYear() != firstOfCurrentMonth.getYear()) {
            return;
        }
        LocalDate bisDatum = leseDatumAusDatePicker(dpBis);
        if(bisDatum == null || bisDatum.getYear() != firstOfCurrentMonth.getYear()) {
            markRowsOfOneDate(vonDatum, tbTabelle);
        } else {
            if(!bisDatum.isAfter(vonDatum)) {
                return;
            }
            markAllRowsVonBis(vonDatum, bisDatum, tbTabelle);
        }
    }

    /**
     * Die Methode ist dazu gedacht von Allen Drei Controllern Innerhalb eines OnAction auf dem DatePicker mit dem Namen dpBis aufgerufen zu
     * werden da der Code hinter diesem In allen Drei Controller Klassen gleich ist. Die Methode liest die Übergebenen datePicker
     * dpVon und dpBis aus und ruft je nach Inhalt der Selben entweder eine Methode zum Markieren mehrerer Daten in der
     * übergebenen TableView tbTabelle auf oder returned ohne etwas zu tun.
     * @param firstOfCurrentMonth
     * @param dpVon
     * @param dpBis
     * @param tbTabelle
     */
    protected void toBeCalledInOnDpBisAction(LocalDate firstOfCurrentMonth, DatePicker dpVon, DatePicker dpBis, TableView tbTabelle) {
        LocalDate bisDatum = leseDatumAusDatePicker(dpBis);
        LocalDate vonDatum = leseDatumAusDatePicker(dpVon);
        if(bisDatum == null || vonDatum == null) {
            return;
        }
        if(bisDatum.getYear() != firstOfCurrentMonth.getYear() || vonDatum.getYear() != firstOfCurrentMonth.getYear()) {
            return;
        }
        if(!bisDatum.isAfter(vonDatum)) {
            return;
        }
        markAllRowsVonBis(vonDatum, bisDatum, tbTabelle);
    }
}
