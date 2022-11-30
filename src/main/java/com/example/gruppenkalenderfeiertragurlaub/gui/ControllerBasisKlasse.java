package com.example.gruppenkalenderfeiertragurlaub.gui;

import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.DatenbankCommunicator;
import com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen.UsefulConstants;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class ControllerBasisKlasse {
    LocalDate firstOfCurrentMonth;
    Boolean dataHasBeenModified = false;
    Boolean jahrComboboxWurdeSoebenUmgestellt = false;
    Boolean dpVonOnActionCalledFromUpdateDatepickers = false;
    Boolean dpBisOnActionCalledFromUpdateDatepickers = false;
    Boolean scrollWasJustHandeld = false;

    /**
     * fügt in die Übergebene Combobox<Integer> alle in der verwendeten Arraylist enthalten Jahre hinzu
     * (at the time of writing alle Jahre von 2022 bis einschließlich 2040) wählt das akktuelle Jahra ls
     * Standard vorauswahl aus.
     * @param comboBoxJahrAuswahl die zu konfigurierende Combobox<Integer>
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
     * @param comboBoxMonatAuswahl die zu konfigurierende Combobox<String>
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
     * (d. h. was eine Gruppe an einem Bestimmten Tag laut kalender tut, Ausgenommen Gesetzliche Feiertage
     * Welche nicht in der Combobox auswählbar sein sollen). Legt fest das für jeden Character ein Entsprchender
     * String welcher den gruppenstatus beschreibt in der Gui angezeigt wird, sohwohl wenn ausgewählt
     * als auch in er Auswahl liste der Combobox
     * @param comboBoxStatusAuswahl die zu konfigurierende Combobox<Character>
     */
    public static void configureCBStatusauswahl(ComboBox<Character> comboBoxStatusAuswahl) {
        comboBoxStatusAuswahl.getItems().addAll(UsefulConstants.getStatusListCharacterFormatOhneFeiertag());
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
     * @param aktivitaetsStatus Status als Character für den der Passende String gefunden wwerden soll.
     * @return Text für die Ausgabe in der Gui
     */
    private static String getDisplayMessageForStatus(Character aktivitaetsStatus) {
        int statusIndex = UsefulConstants.getStatusListCharacterFormat().indexOf(aktivitaetsStatus);
        return (statusIndex != -1) ? UsefulConstants.getStatusListDisplayFormat().get(statusIndex) : "";
    }

    /**
     * konvertiert einen String welcher einen Monat in dem Format enthält wie die Classe LocalDate diesen Zurückgibt
     * (Monatsname in Capslock "JANUARY") in einen für String für die Gui Ausgabe.
     * @param localDateMonat Monat im Format wie von der LocalDate Klasse verwendet.
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
     * @param tableColumnBoolean zu Formatierende TableColum<Klassenname, Boolean>
     * @param columAttributeName Name des in der Colum anzuzeigenden Attributs in der Anzuzeigenden Klasse
     */
    public static void configureBooleanTableColum(TableColumn tableColumnBoolean, String columAttributeName) {
        /*
        IMPORTANT TO NOTE ABUT CellFactory class. Cells in Java are not generated anew every time a new Object
        needs to be displayed in the table. The Cells that are on Screen do not change (for the most part)
        but the Values displayed in them and the Objects this Values are obtained from do. Thererffore it is important
        to note that If you set a cell factory that cell factory is called ONLY if the VALUE that is displayed in a cell
        changes. it is NOT called when the OBJECT whos values are displayed Changes. This can cause problems if
        you want to alter what the cell factory does based on an attribute of the Object whos value is displayed
        becaus only a change in the displayed Value will trigger the cell factory no matter the changes that may have
        occured to other Values in that obejct. Example: if A new Object is displayed within this cell that previously
        displayed an other obejcts value true: if the new objects Value to be displayed in the cell is
        false or any other Value then true, which is currenlty being displayed in the cell allready the CellFactory IS called
        if the objects Value for this call is True just like the previous object's was then the CellFactory IS NOT called.
         */
        tableColumnBoolean.setCellValueFactory(new PropertyValueFactory<>(columAttributeName));
         /* sets the CellFactory (not to be confused with the CellValueFactory) which is responseble
        for determening the format in which the data (which are set using CellVlaueFactory) is displayed
         */
        tableColumnBoolean.setCellFactory(colum -> {
            TableCell<TagBasisKlasse, Boolean> cell = new TableCell<>();

             /* Because during at this point there are no Values in the table yet, because this is called during the
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
     * Konfiguriert die Übergebene TableColumn<Klassenname, Integer> so dass das als String übergebene Attribut der
     * anzuzeigenenden Klasse eingefügt wird. Sorgt dafür das für den Integer 0 "Ja", für 1 "Nein" und für
     * 3 "gesetzlicher Feiertag" anstatt des Integer Wertes angezeigt wird.
     * @param tableColumnInteger eine TableColumn des typs integer welche configuriert werden soll
     * @param columAttributeName name des Attributs innerhalb der klasse, welches in der TableColumn angezeigt werden soll
     */
    public static void configureIntegerTableColum(TableColumn tableColumnInteger, String columAttributeName) {
        tableColumnInteger.setCellValueFactory(new PropertyValueFactory<>(columAttributeName));
        tableColumnInteger.setCellFactory(colum -> {
            TableCell<TagBasisKlasse, Integer> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, old, newIntegerVal) -> {
                if (newIntegerVal != null) {
                    if(newIntegerVal == 0) {cell.setText("Nein");}
                    if(newIntegerVal == 1) {cell.setText("Ja");}
                    if(newIntegerVal == 2) {cell.setText("gesetzlicher Feiertag");}
                }
            });
            return cell;
        });
    }

    /**
     * Konfiguriert für die Übergebene TabelColum<Klassenname, LocalDate> das als String übergebenen Attributnamen
     * als das in dieser Colum anzuzeigende Attribut. Formatiert das anzuzeigende LocalDate entsprechend dem in Deutschland
     * üblichen Format (dd.MM.yyyy) so das es in diesem Format in der Gui angezeigt wird.
     * @param tableColumnLocalDate Zu KonfigurierendeTabelColum<Klassenname, LocalDate>
     * @param columAttributeName Name des in der Colum anzuzeigenden Attributs in der Anzuzeigenden Klasse
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
            TableCell<TagBasisKlasse, LocalDate> cell = new TableCell<>();
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
     * @param gruppenStatusColum Zu Konfigurierende TableColum<Klassenname, Character> zur Statusanzeige
     * @param columnAttributeName Name des in der Colum anzuzeigenden Attributs in der Anzuzeigenden Klasse
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
     * @param comboBoxGruppenAuswahl zu Konfigurierende und zu befüllende Combobox<Object>
     * @return ArrayList der Ausgelesenen Gruppenfamilien von denen Jede alle zu ihr gehörenden Gruppen enthält.
     * @throws SQLException wird geworfen fals in der zur Auslesung der Daten aufgerufenen Methode DatenbankCommunicator.getAllGruppenFamilienUndGruppen()
     * beim Auslesen der Daten ein Fehler auftrit.
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
        comboBoxGruppenAuswahl.setConverter(new StringConverter<>() {
            @Override
            public String toString(Object gruppeOrGruppenFamilie) {
                String gruppeNameString = "";
                if (gruppeOrGruppenFamilie != null) {
                    if (gruppeOrGruppenFamilie.getClass() == GruppeFuerKalender.class) {
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
     * @param tcGruppenBezeichung zu Configurierende TableColumn<GruppenKalenderTag, Integer>
     * @param columnAttributeName Name des in der Colum anzuzeigenden Attributs in der Anzuzeigenden Klasse
     * @param gruppenListe liste aller Gruppen sodas der Name der Gruppe mit der Entsprechenden Nummer ermittelt werden kann
     */
    public static void configureGruppenBezeichnungTableColum(TableColumn tcGruppenBezeichung, String columnAttributeName, ArrayList<GruppeFuerKalender> gruppenListe) {
        tcGruppenBezeichung.setCellValueFactory(new PropertyValueFactory<>(columnAttributeName));
        tcGruppenBezeichung.setCellFactory(colum -> {
            TableCell<TagBasisKlasse, Integer> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if (newVal != null) {
                    cell.setText("");
                    for (GruppeFuerKalender gr : gruppenListe) {
                        if(Objects.equals(gr.getGruppeId(), newVal)) {
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
     * @param vonDatum datum von dem aus gesucht wird
     * @param bisDatum datum bis zu dem gesucht wird
     * @param tbTabelle zu durchsuchende Tabelle
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
     * @param vonDatum zu suchendes Datum
     * @param tbTabelle zu durchsuchende Tabelle
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
     * @param dp DatePicker aus welchem ddas Datum ausgelesen werden soll
     * @return ausgelesenes LocalDate wenn erfolgreich, Null wenn nicht erfolgreich
     */
    protected LocalDate leseDatumAusDatePicker(DatePicker dp) {
        LocalDate datum;
        try {
            datum = dp.getValue();
        } catch (Exception e) {
            datum = null;
        }
        return datum;
    }

    /**
     * erhält ein Datum. liest den Monat dieses Datums aus und sucht darauf hin der der Tabelle nach dem ersten Tag
     * mit einem Datum in diesem Monat. scrollt zu diesem Tag in der Tabelle.
     * @param firstWerktagOfMonth Datum zu welchem gescrollt werden soll
     * @param tbTabelle Tabelle in der gescrollt wird.
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
     * @param changeNumber Anzahl der Monate die nach Vorne (positive Zahl) oder nach hinten (Negative zahl) geschoben werden sollen
     * @param comboBoxJahrAuswahl Combobox Jahrauswahl in denen das evtl. Geänderte jahr angezeigt werden soll
     * @param comboBoxMonatAuswahl Combobox Monat auswahl in der der Abgeänderte Monat angezeigt werden soll
     * @param firstOfCurrentMonth Datum von dem aus die änderung vorgenommen wird
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
     * @param firstOfCurrentMonth akktuelles Datum des Programms um ausgelesene Daten damit vergleichen zu können
     * @param dpVon Erster Datepicker
     * @param dpBis Zweiter Datepicker
     * @param tbTabelle TableView in der Zellen markiert werden sollen
     */
    protected void handleDatePickerVon(LocalDate firstOfCurrentMonth, DatePicker dpVon, DatePicker dpBis, TableView tbTabelle) {
        if(dpVonOnActionCalledFromUpdateDatepickers) {
            dpVonOnActionCalledFromUpdateDatepickers = false;
            return;
        }
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
     * @param firstOfCurrentMonth akktuelles Datum des Programms um ausgelesene Daten damit vergleichen zu können
     * @param dpVon Erster Datepicker
     * @param dpBis Zweiter Datepicker
     * @param tbTabelle TableView in der Zellen markiert werden sollen
     */
    protected void handleDatePickerBis(LocalDate firstOfCurrentMonth, DatePicker dpVon, DatePicker dpBis, TableView tbTabelle) {
        if(dpBisOnActionCalledFromUpdateDatepickers) {
            dpBisOnActionCalledFromUpdateDatepickers = false;
            return;
        }
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

    /**
     * öffnet ein neues Fenster als subfenster des Hauptmenüs, aus dem fxmlFile dessen Pfad als String übergeben wurde.
     * das Neu geöffnete Fenster muss erst geschlossen werden bis wieder mit dem Hauptmenü fenster interagiert werden kann
     * @param titel titel des Fensters
     * @param fxmlResource String welcher das zu öffnende FXML file enthält
     */
    protected static void openWindow(Stage parentStage, String titel, String fxmlResource) {
        FXMLLoader loader = new FXMLLoader(ControllerBasisKlasse.class.getResource(fxmlResource));
        Scene newScene;
        try {
            newScene = new Scene(loader.load());
        } catch (IOException ex) {
            // TODO: handle error
            return;
        }
        Stage stage = new Stage();
        stage.initOwner(parentStage);
        stage.setScene(newScene);
        stage.setTitle(titel);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    /**
     * erzeugt eine AlertBox welche den Nutzer fragte ob er Die Änderungen wirklcih verwerfen möchte. Liest die Antwort des
     * Nutzers ein und gibt Entsprechend true odre false zurück
     * @return true wenn Nutzerbestätigung erteilt wurde, false wenn nicht
     */
    protected Boolean getNutzerBestaetigung() {
        Boolean executeRequestedAction = false;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Beschtätigen");
        alert.setHeaderText("Sind sie Sicher?");
        alert.setContentText("Nicht Gespeicherte Daten gehen Verloren");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            executeRequestedAction = true;
        }
        return executeRequestedAction;
    }

    /**
     * Überprüft ob Änderungen an den Daten in der TableView Vorgenommen wurden (anhand der Globalen variable dataHasBeenModified)
     * und ob eine änderung in Monaten um den gegebenen Integer wert monthChange ausgehend vom firstOfCurrentMonth zum Wechsel
     * in eine Anderes Jahr führen würde. Wenn ja so wird mittels Methodenaufruf Nutzerbestätigung erbeten. Die Mehtode gibt einen
     * Boolen wert zurück welcher anzeigt ob die angefragete Operation zum Wechsel eines Monats bedenkenlos oder mit
     * Nutzerbestätigugn fortgesetzt werden soll oder nicht
     * @param firstOfCurrentMonth ausgangsdatum
     * @param monthChange anzahl der Monate um die nach vorn oder hinten verschoben werden soll
     * @return true wenn keine Daten Verändert wurden und/oder kein jahreswechsel ansteht oder durch Nutzerbestätigung. false
     * wenn Nutzerbestätigung angefragt aber nicht gewährt wurde.
     */
    protected boolean monthChangeOperationShouldbeContinued(LocalDate firstOfCurrentMonth, Integer monthChange) {
        if(dataHasBeenModified && changingMonthWouldChangeYear(firstOfCurrentMonth, monthChange)) {
            if(!getNutzerBestaetigung()) {
                return false;
            } else {
                dataHasBeenModified = false;
                return true;
            }
        }
        return true;
    }

    /**
     * Überpfrüft ob ausgehend vom Übergebenen Datum aus gerechnet, eine veränderung des Monats um den gegebenen Integer
     * monthChange eine Änderung des Jahres zur folge hätte
     * @param firstOfCurrentMonth datum von dem ausgehend die verschiebung geprüft wird
     * @param monthChange anzahl an Monaten für die Überprüft werdnen soll ob eine Datumsverschiebung das jahr ändern würde
     * @return true wenn eine Jahresanderung der Fall wäre. False wenn nicht
     */
    protected Boolean changingMonthWouldChangeYear(LocalDate firstOfCurrentMonth, Integer monthChange) {
        return (firstOfCurrentMonth.getYear() != firstOfCurrentMonth.plusMonths(monthChange).getYear());
    }

    /**
     * Die Methode überprüft welcher Code Abschnitt durchgeführt werden muss. Ist der Boolean jahrComboboxWurdeSoebenUmgestellt
     * true so bedeutet dies das die Methode als consequenz einer Automatischen zurückstellung des jahres nach verweigerter Nutzer
     * bestätigugn erneut aufgerufen wurde. In diesem fall muss nichts weiter getan werden und die Methode gibt false zurück um
     * anzuzeigen das die Aufrufende Methode sofort beendet werden kann. ist der boolean dataHasBeenModified true so wird
     * Nutzerbestätigung angefordert. ist diese negativ gibt die Methode false zurück. Ansonsten gibt sie true zurück
     * sodas der nachfolgende code in der aufrufenden Methode ausgeführt werden kann
     * @param comboBoxJahrAuswahl die comboboxJahresauswahl für Wleche die Methode aufgerufen wird
     * @return true wenn die aufrufende methode fortgesetzt werden soll, False wenn sie sofort beendet werden soll
     */
    protected boolean handleComboboxJahrauswahlShouldBeContinued(ComboBox<Integer> comboBoxJahrAuswahl) {
        if(jahrComboboxWurdeSoebenUmgestellt) {
            jahrComboboxWurdeSoebenUmgestellt = false;
            return false;
        }
        if(dataHasBeenModified) {
            if(!getNutzerBestaetigung()) {
                jahrComboboxWurdeSoebenUmgestellt = true;
                comboBoxJahrAuswahl.getSelectionModel().select(firstOfCurrentMonth.getYear() - UsefulConstants.getJahreList().get(0));
                return false;
            }
        }
        return true;
    }

    /**
     * Ändert das firstOfCurrentMonth datum zum Ausgewählten Jahr. scorllt zum firstOfCurrentMonth in der tabelle
     * @param comboBoxJahrAuswahl die comboboxJahresauswahl für Wleche die Methode aufgerufen wird
     * @param tbTabelle die Tabelle welche durch änderungen in der comboboxJahresauswahl beinflusst wird.
     */
    protected void handleOnComboboxJahrAuswahlAction(ComboBox<Integer> comboBoxJahrAuswahl, TableView tbTabelle) {
        Integer year = comboBoxJahrAuswahl.getSelectionModel().getSelectedItem();
        firstOfCurrentMonth = firstOfCurrentMonth.withYear(year);
        scrollToSelectedMonth(firstOfCurrentMonth, tbTabelle);
    }

    /**
     * ändert das Ausgewählte Datum der Übregebenen Datpicker auf das Übergebene LocalDate. Dies hat zur Folge das durch OnAction
     * methoden in der TabelleView eine auswahl getrofen wird, deswegen wird besagte Auswahl am ende der Methode entfernt.
     * @param firstOfCurrentMonth neues default datum der DatePicker
     * @param dpVon Erster DatePicker derssen Default Datum geändert werden soll
     * @param dpBis Zweiter DatePicker derssen Default Datum geändert werden soll
     * @param tbTabelle Tabelle deren Auswahl entfernt werdne soll
     */
    protected void updateDatpickers(LocalDate firstOfCurrentMonth, DatePicker dpVon, DatePicker dpBis, TableView tbTabelle) {
        dpVonOnActionCalledFromUpdateDatepickers = true;
        dpBisOnActionCalledFromUpdateDatepickers = true;
        dpBis.setValue(firstOfCurrentMonth);
        dpVon.setValue(firstOfCurrentMonth);
        tbTabelle.getSelectionModel().clearSelection();
    }

    /**
     * wird aufgerufen wenn von einem EventFilter in einem der Drei Abgeleiteten Klassen von ControllerBasisKlasse ein ScrollEvent
     * aufgegriffen wird. ermittelt zu welcem Tag in der Tabelle gescrollt wurde, passt die Globale Variable FirstOfCurrentMonth so
     * an das ihr Monat dem des Tages entspricht zu dem Gescrollt wurde, passt die ComboboxMonatAuswahl entsprechend an d. h. wählt
     * den richtigen Monat aus. Damit der dadurch Ausgelöste ActionHandler auf der Combobox Weis das er nicht tätig werden muss
     * setzt sie den Boolean scrolLWasJustHandeld auf true.
     * @param event scroll Event für welches die Methode aufgerufen wrude
     * @param comboBoxMonatAuswahl zu akktualisierende Combobox zur Monatauswahl
     */
    protected void handleScrollEvent(ScrollEvent event, ComboBox comboBoxMonatAuswahl) {
        try{
            TableCell cell = (TableCell) event.getTarget();
            TagBasisKlasse tag = (TagBasisKlasse) cell.getTableRow().getItem();
            firstOfCurrentMonth = firstOfCurrentMonth.withMonth(tag.getDatum().getMonthValue());
            scrollWasJustHandeld = true;
            comboBoxMonatAuswahl.getSelectionModel().select(firstOfCurrentMonth.getMonthValue() - 1);
        } catch (Exception e) {
            return;
        }
    }
}
