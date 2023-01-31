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

public class Controller {
    LocalDate firstOfCurrentMonth;
    Boolean dataHasBeenModified = false;
    Boolean jahrComboBoxWurdeSoebenUmgestellt = false;
    Boolean dpVonOnActionCalledFromUpdateDatePickers = false;
    Boolean dpBisOnActionCalledFromUpdateDatePickers = false;
    Boolean scrollWasJustHandled = false;

    /**
     * fügt in die übergebene ComboBox<Integer> alle in der verwendeten Arraylist enthalten Jahre hinzu
     * (at the time of writing alle Jahre von 2022 bis einschließlich 2040) wählt das aktuelle Jahr als
     * Standard vorauswahl aus.
     * @param comboBoxJahrAuswahl die zu konfigurierende ComboBox<Integer>
     */
    public static void configureCBJahrAuswahl(ComboBox<Integer> comboBoxJahrAuswahl) {
        comboBoxJahrAuswahl.getItems().addAll(UsefulConstants.getJahreList());
        comboBoxJahrAuswahl.getSelectionModel().select(UsefulConstants.getJahreList().indexOf(LocalDate.now().getYear()));
    }

    /**
     * Fügt alle Zwölf Monat in dem Format wie die Klasse LocalDate diese Verwendet (englisch und Capslock "JANUARY") in
     * die übergebene ComboBox<String> hinzu.
     * Sorgt dafür das für jeden Monat sowohl wenn ausgewählt als, auch wenn in der Auswahlliste angezeigt,
     * String mit dem Deutschen Monatsnamen angezeigt wird. (Januar). Wählt den aktuellen Monat als default aus.
     * @param comboBoxMonatAuswahl die zu konfigurierende ComboBox<String>
     */
    public static void configureCBMonatAuswahl(ComboBox<String> comboBoxMonatAuswahl) {
        //fügt alle benötigten Items den ComboBoxen Hinzu
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
        //Setzt den aktuellen Monat/das aktuelle Jahr als Vorauswahl
        comboBoxMonatAuswahl.getSelectionModel().select(LocalDate.now().getMonthValue() - 1);
    }

    /**
     * fügt der übergebenen ComboBox<Character> Character hinzu welche die auswählbaren Gruppenstatus representieren
     * (d. h. was eine Gruppe an einem bestimmten Tag laut kalender tut, ausgenommen gesetzliche Feiertage
     * welche nicht in der ComboBox auswählbar sein sollen). Legt fest das für jeden Character ein entsprechender
     * String welcher den gruppenstatus beschreibt in der Gui angezeigt wird sowohl, wenn ausgewählt
     * als auch in er Auswahl liste der ComboBox
     * @param comboBoxStatusAuswahl die zu konfigurierende ComboBox<Character>
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
     * @param aktivitaetsStatus Status als Character für den der Passende String gefunden werden soll.
     * @return Text für die Ausgabe in der Gui
     */
    public static String getDisplayMessageForStatus(Character aktivitaetsStatus) {
        int statusIndex = UsefulConstants.getStatusListCharacterFormat().indexOf(aktivitaetsStatus);
        return (statusIndex != -1) ? UsefulConstants.getStatusListDisplayFormat().get(statusIndex) : "";
    }

    /**
     * konvertiert einen String welcher einen Monat in dem Format enthält wie die Klasse LocalDate diesen Zurückgibt
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
        but the Values displayed in them and the Objects these Values are obtained from do. Therefore, it is important
        to note that If you set a cell factory that cell factory is called ONLY if the VALUE that is displayed in a cell
        changes. it is NOT called when the OBJECT whose values are displayed Changes. This can cause problems if
        you want to alter what the cell factory does based on an attribute of the Object whose value is displayed
        because only a change in the displayed Value will trigger the cell factory no matter the changes that may have
        occurred to other Values in that object. Example: if A new Object is displayed within this cell that previously
        displayed another objects value true: if the new objects Value to be displayed in the cell is
        false or any other Value then true, which is currently being displayed in the cell already the CellFactory IS called
        if the objects Value for this call is True just like the previous object's was then the CellFactory IS NOT called.
         */
        tableColumnBoolean.setCellValueFactory(new PropertyValueFactory<>(columAttributeName));
         /* sets the CellFactory (not to be confused with the CellValueFactory) which is responsible
        for determining the format in which the data (which are set using CellValueFactory) is displayed
         */
        tableColumnBoolean.setCellFactory(colum -> {
            TableCell<Tag, Boolean> cell = new TableCell<>();

             /* Because during at this point there are no Values in the table yet, because this is called during the
               initialize method, we add a Listener on the cell which we are setting the format on
        If I understand it correctly this listens for any action, e.g. if a value is inserted
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
     * Konfiguriert die Übergebene TableColumn<Klassenname, Integer> sodass das als String übergebene Attribut der
     * anzuzeigenden Klasse eingefügt wird. Sorgt dafür das für den Integer 0 "Ja", für 1 "Nein" und für
     * 3 "gesetzlicher Feiertag" anstatt des Integer-Wertes angezeigt wird.
     * @param tableColumnInteger eine TableColumn des typs integer welche konfiguriert werden soll
     * @param columAttributeName name des Attributs innerhalb der klasse, welches in der TableColumn angezeigt werden soll
     */
    public static void configureIntegerTableColum(TableColumn tableColumnInteger, String columAttributeName) {
        tableColumnInteger.setCellValueFactory(new PropertyValueFactory<>(columAttributeName));
        tableColumnInteger.setCellFactory(colum -> {
            TableCell<Tag, Integer> cell = new TableCell<>();
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
     * Konfiguriert für die Übergebene TableColum<Klassenname, LocalDate> das als String übergebenen Attributnamen
     * als das in dieser Colum anzuzeigende Attribut. Formatiert das anzuzeigende LocalDate entsprechend dem in Deutschland
     * üblichen Format (dd.MM.yyyy) so das es in diesem Format in der Gui angezeigt wird.
     * @param tableColumnLocalDate Zu KonfigurierendeTableColum<Klassenname, LocalDate>
     * @param columAttributeName Name des in der Colum anzuzeigenden Attributs in der Anzuzeigenden Klasse
     */
    public static void configureLocalDateTableColum(TableColumn tableColumnLocalDate, String columAttributeName) {
        tableColumnLocalDate.setCellValueFactory(new PropertyValueFactory<>(columAttributeName));
        /*
        This works in a much similar manner as mentioned above. The only difference being
        that instead of setting a text directly based on conditions, this time, once the
        Listener detects an action, it checks if the Value detected in the cell
        is != null and then, assuming it is indeed != null, it takes the value (which is a LocalDate)
        and formats it using a dateTimeFormatter and sets it as the text to display in the cell.
        The best thing is, because this sets a Listener, It also Automatically works with
        any rows you add later! Isn't that wonderfully? (newVal is in this case the new Value of the cell.
        As I understand it, the Listener ist always called if there is a change to the cell, so it
        should work with updating things as well I assume)
         */
        tableColumnLocalDate.setCellFactory(colum -> {
            TableCell<Tag, LocalDate> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, old, newVal) -> {
                if (newVal != null) {
                    String cellText;

                    switch(newVal.getDayOfWeek().toString().trim()) {
                        case "MONDAY":
                            cellText = "Mo,\t";
                            break;
                        case "TUESDAY":
                            cellText = "Di,\t";
                            break;
                        case "WEDNESDAY":
                            cellText = "Mi,\t";
                            break;
                        case "THURSDAY":
                            cellText = "Do,\t";
                            break;
                        case "FRIDAY":
                            cellText = "Fr,\t";
                            break;
                        default:
                            cellText = "";
                    }
                    cellText += newVal.format(DateTimeFormatter.ofPattern("dd.MM.yyy"));
                    cell.setText(cellText);
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
        here the only difference is that depending on the character the full word it is supposed to
        represent is displayed in the cell instead of just the Character. For this it uses the
        statusCharacterArray which contains all the Possible Chars as well as the statusStringArray which
        contains all the possible equivalents as Strings in each with the same index as the corresponding
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
     * Erhält eine ComboBox<Object>. Füllt die ArrayList<GruppenFamilienFuerKalender> gruppenFamilienListe mit allen Gruppenfamilien aus der Datenbank
     * in den Gruppenfamilien sind alle zu ihnen gehörigen Gruppen bereits enthalten. Fügt der ComboBox alle Gruppenfamilien sowie die zugehörigen
     * Gruppen hinzu wobei die Gruppenfamilien stets in der Liste über den Gruppen stehen, die zu Ihnen gehören. Stellt sicher das in der
     * Liste für jede Gruppe/Gruppenfamilie deren Name dem Nutzer angezeigt wird, sowohl in der Liste als, auch wenn ausgewählt.
     * Stellt sicher das in der Liste die Gruppenfamilien Fett gedruckt geschrieben sind sodas sie von Gruppen leicht zu
     * unterscheiden sind.
     * @param comboBoxGruppenAuswahl zu Konfigurierende und zu befüllende ComboBox<Object>
     * @return ArrayList der ausgelesenen Gruppenfamilien von denen Jede alle zu ihr gehörenden Gruppen enthält.
     * @throws SQLException wird geworfen, falls in der zur Auslesung der Daten aufgerufenen Methode DatenbankCommunicator.getAllGruppenFamilienUndGruppen()
     * beim Auslesen der Daten ein Fehler auftritt.
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
     * erhält eine TableColumn<GruppenKalenderTag, Integer> und konfiguriert diese sodas sie die GruppeID enthält
     * verwendet außerdem eine CellFactory um für Jede gruppenID, aus der Übergebenen ArrayList, den richtigen
     * Gruppennamen auszulesen und besagten in der Tabelle anzuzeigen.
     * @param tcGruppenBezeichnung zu konfigurierende TableColumn<GruppenKalenderTag, Integer>
     * @param columnAttributeName Name des in der Colum anzuzeigenden Attributs in der Anzuzeigenden Klasse
     * @param gruppenListe Liste aller Gruppen sodas der Name der Gruppe mit der entsprechenden Nummer ermittelt werden kann
     */
    public static void configureGruppenBezeichnungTableColum(TableColumn tcGruppenBezeichnung, String columnAttributeName, ArrayList<GruppeFuerKalender> gruppenListe) {
        tcGruppenBezeichnung.setCellValueFactory(new PropertyValueFactory<>(columnAttributeName));
        tcGruppenBezeichnung.setCellFactory(colum -> {
            TableCell<Tag, Integer> cell = new TableCell<>();
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
     * (einschließlich der Zwei Daten selbst). Hierfür überprüft es bei jedem Objekt in der Tabelle ob dessen datum Entweder
     * dem vonDatum oder bisDatum entspricht oder zwischen den Beiden liegt. Jedes so gefunden Objekt wird selected.
     * Scrollt zum Ersten gefundenen Reihe welches das VonDatum enthält.
     * @param vonDatum datum von dem aus gesucht wird
     * @param bisDatum datum bis zu dem gesucht wird
     * @param tbTabelle zu durchsuchende Tabelle
     */
    protected void markAllRowsVonBis(LocalDate vonDatum, LocalDate bisDatum, TableView tbTabelle) {
        tbTabelle.getSelectionModel().clearSelection();
        ObservableList<Tag> tabellenEintraege = tbTabelle.getItems();
        boolean istErstesGefundenesDatum = true;
        for(Tag tag : tabellenEintraege) {
            LocalDate tagesDatum = tag.getDatum();
            boolean tagIsInBetweenRange = (tagesDatum.isAfter(vonDatum) && tagesDatum.isBefore(bisDatum));
            boolean tagIsVonOrBisDatum = (tagesDatum.toString().equals(vonDatum.toString()) || tagesDatum.toString().equals(bisDatum.toString()));
            if(tagIsInBetweenRange || tagIsVonOrBisDatum) {
                tbTabelle.getSelectionModel().select(tag);
                if(istErstesGefundenesDatum) {
                    tbTabelle.scrollTo(tabellenEintraege.indexOf(tag));
                    istErstesGefundenesDatum = false;
                }
            }
        }
    }

    /**
     * erhält ein Einzelnes LocalDate und selected in der Tabelle alle Zeilen deren GruppenKalenderTag Objekte dieses Datum enthalten.
     * Scrollt zum Ersten gefundenen Reihe mit diesem Datum.
     * @param vonDatum zu suchendes Datum
     * @param tbTabelle zu durchsuchende Tabelle
     */
    protected void markRowsOfOneDate(LocalDate vonDatum, TableView tbTabelle) {
        tbTabelle.getSelectionModel().clearSelection();
        ObservableList<Tag> tabellenEintraege = tbTabelle.getItems();
        boolean istErstesGefundenesDatum = true;
        for (Tag tag : tabellenEintraege) {
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
     * @param dp DatePicker aus welchem das Datum ausgelesen werden soll
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
     * erhält ein Datum. Liest den Monat dieses Datums aus und sucht darauf hin in der Tabelle nach dem ersten Tag
     * mit einem Datum in diesem Monat. scrollt zu diesem Tag in der Tabelle.
     * @param firstWerktagOfMonth Datum zu welchem gescrollt werden soll
     * @param tbTabelle Tabelle in der gescrollt wird.
     */
    protected void scrollToSelectedMonth(LocalDate firstWerktagOfMonth, TableView tbTabelle) {
        String month = firstWerktagOfMonth.getMonth().toString();
        ObservableList<Tag> items = tbTabelle.getItems();
        for(Tag tag : items) {
            if(tag.getDatum().getMonth().toString().equals(month)) {
                tbTabelle.scrollTo(items.indexOf(tag));
                break;
            }
        }
    }

    /**
     * Erhält einen Integer changeNumber und verändert das Datum firstOfCurrentMonth um eine Anzahl von Monaten die
     * der übergebenen ChangNumber entspricht. Passt die ComboBoxen comboBoxMonatAuswahl und gegebenen Falls die
     * comboBoxJahrAuswahl entsprechend dem veränderten firstOfCurrentMonth an (wodurch die entsprechenden
     * onActions dieser ComboBoxen ausgelöst werden)
     * @param changeNumber Anzahl der Monate die nach vorne (positive Zahl) oder nach hinten (Negative zahl) geschoben werden sollen
     * @param comboBoxJahrAuswahl ComboBox zur Jahr-Auswahl in der das eventuell. Geänderte jahr angezeigt werden soll
     * @param comboBoxMonatAuswahl ComboBox Monat auswahl in welcher der abgeänderte Monat angezeigt werden soll
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
     * Die Methode ist dazu gedacht von allen Drei Controllern innerhalb eines OnAction auf dem DatePicker mit dem Namen dpVon aufgerufen zu
     * werden da der Code hinter diesem In allen Drei Controller Klassen gleich ist. Die Methode liest die Übergebenen datePicker
     * dpVon und dpBis aus und ruft je nach Inhalt derselben entweder Methoden zum Markieren eines einzigen Datums oder mehrere Daten in der
     * übergebenen TableView tbTabelle auf oder returned ohne etwas zu tun.
     * @param firstOfCurrentMonth aktuelles Datum des Programms um ausgelesene Daten damit vergleichen zu können
     * @param dpVon Erster DatePicker
     * @param dpBis Zweiter DatePicker
     * @param tbTabelle TableView in der Zellen markiert werden sollen
     */
    protected void handleDatePickerVon(LocalDate firstOfCurrentMonth, DatePicker dpVon, DatePicker dpBis, TableView tbTabelle) {
        if(dpVonOnActionCalledFromUpdateDatePickers) {
            dpVonOnActionCalledFromUpdateDatePickers = false;
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
     * Die Methode ist dazu gedacht von allen Drei Controllern innerhalb eines OnAction auf dem DatePicker mit dem Namen dpBis aufgerufen zu
     * werden da der Code hinter diesem In allen Drei Controller Klassen gleich ist. Die Methode liest die Übergebenen datePicker
     * dpVon und dpBis aus und ruft je nach Inhalt derselben entweder eine Methode zum Markieren mehrerer Daten in der
     * übergebenen TableView tbTabelle auf oder returned ohne etwas zu tun.
     * @param firstOfCurrentMonth aktuelles Datum des Programms um ausgelesene Daten damit vergleichen zu können
     * @param dpVon Erster DatePicker
     * @param dpBis Zweiter DatePicker
     * @param tbTabelle TableView in der Zellen markiert werden sollen
     */
    protected void handleDatePickerBis(LocalDate firstOfCurrentMonth, DatePicker dpVon, DatePicker dpBis, TableView tbTabelle) {
        if(dpBisOnActionCalledFromUpdateDatePickers) {
            dpBisOnActionCalledFromUpdateDatePickers = false;
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
        FXMLLoader loader = new FXMLLoader(Controller.class.getResource(fxmlResource));
        Scene newScene;
        try {
            newScene = new Scene(loader.load());
        } catch (IOException ex) {

            DatenbankCommunicator.generateErrorAlert(   "Öffnen des Dialogs Fehlgeschlagen",
                                                        "Beim Laden und Öffnen des Dialogfensters ist ein Fehler Aufgetreten." +
                                                                "Bitte Starten Sie das Programm neu und überprüfen Sie die Programmfiles");
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
     * erzeugt eine AlertBox welche den Nutzer fragte, ob er die Änderungen wirklich verwerfen möchte. Liest die Antwort des
     * Nutzers ein und gibt entsprechend true oder false zurück
     * @return True, wenn Nutzerbestätigung erteilt wurde, false, wenn nicht
     */
    protected Boolean getNutzerBestaetigung() {
        boolean executeRequestedAction = false;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Bestätigen");
        alert.setHeaderText("Sind Sie sicher?");
        alert.setContentText("Nicht gespeicherte Daten gehen verloren.");
        DialogPane pane = alert.getDialogPane();
        //setzt den Cancle Button als default Button und alle anderen Buttons (in diesem Fall Ok Button) als nicht default
        for(ButtonType buttonType : alert.getButtonTypes()) {
            Button button = (Button) pane.lookupButton(buttonType);
            button.setDefaultButton(buttonType == ButtonType.CANCEL);
        }
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            executeRequestedAction = true;
        }
        return executeRequestedAction;
    }

    /**
     * Überprüft ob Änderungen an den Daten in der TableView vorgenommen wurden (anhand der Globalen variable dataHasBeenModified)
     * und ob eine änderung in Monaten um den gegebenen Integer wert monthChange ausgehend vom firstOfCurrentMonth zum Wechsel
     * in ein anderes Jahr führen würde. Wenn ja, so wird mittels Methodenaufruf Nutzerbestätigung erbeten. Die Method gibt einen
     * Boolean wert zurück welcher anzeigt, ob die angefragte Operation zum Wechsel eines Monats bedenkenlos oder mit
     * Nutzerbestätigung fortgesetzt werden soll oder nicht
     * @param firstOfCurrentMonth ausgangsdatum
     * @param monthChange anzahl der Monate um die nach vorn oder hinten verschoben werden soll
     * @return True, wenn keine Daten verändert wurden und/oder kein jahreswechsel ansteht oder durch Nutzerbestätigung. false,
     * wenn Nutzerbestätigung angefragt aber nicht gewährt wurde.
     */
    protected boolean monthChangeOperationShouldBeContinued(LocalDate firstOfCurrentMonth, Integer monthChange) {
        if(dataHasBeenModified && changingMonthWouldChangeYear(firstOfCurrentMonth, monthChange)) {
            if(!getNutzerBestaetigung()) {
                return false;
            }
            return true;
        }
        return true;
    }

    /**
     * Überprüft, ob ausgehend vom übergebenen Datum aus gerechnet, eine veränderung des Monats um den gegebenen Integer
     * monthChange eine Änderung des Jahres zur Folge hätte
     * @param firstOfCurrentMonth datum von dem ausgehend die verschiebung geprüft wird
     * @param monthChange Anzahl an Monaten für die Überprüft werden soll, ob eine Datumsverschiebung das Jahr ändern würde
     * @return True, wenn eine Jahresänderung der Fall wäre. False, wenn nicht
     */
    protected Boolean changingMonthWouldChangeYear(LocalDate firstOfCurrentMonth, Integer monthChange) {
        return (firstOfCurrentMonth.getYear() != firstOfCurrentMonth.plusMonths(monthChange).getYear());
    }

    /**
     * Die Methode überprüft welcher Code Abschnitt durchgeführt werden muss. Ist der Boolean jahrComboBoxWurdeSoebenUmgestellt
     * true so bedeutet dies das die Methode als Konsequenz einer Automatischen zurückstellung des jahres nach verweigerter
     * Nutzerbestätigung erneut aufgerufen wurde. In diesem Fall muss nichts weiter getan werden und die Methode gibt false zurück, um
     * anzuzeigen, dass die aufrufende Methode sofort beendet werden kann. ist der boolean dataHasBeenModified true so wird
     * Nutzerbestätigung angefordert. Ist diese negativ gibt die Methode false zurück ansonsten gibt sie true zurück
     * sodas der nachfolgende code in der aufrufenden Methode ausgeführt werden kann
     * @param comboBoxJahrAuswahl die ComboBoxJahresauswahl für Welche die Methode aufgerufen wird
     * @return True, wenn die aufrufende methode fortgesetzt werden soll, False, wenn sie sofort beendet werden soll
     */
    protected boolean handleComboBoxJahrAuswahlShouldBeContinued(ComboBox<Integer> comboBoxJahrAuswahl) {
        if(jahrComboBoxWurdeSoebenUmgestellt) {
            jahrComboBoxWurdeSoebenUmgestellt = false;
            return false;
        }
        if(dataHasBeenModified) {
            if(!getNutzerBestaetigung()) {
                jahrComboBoxWurdeSoebenUmgestellt = true;
                comboBoxJahrAuswahl.getSelectionModel().select(firstOfCurrentMonth.getYear() - UsefulConstants.getJahreList().get(0));
                return false;
            }
        }
        return true;
    }

    /**
     * Ändert das firstOfCurrentMonth datum zum ausgewählten Jahr. Scrollt zum firstOfCurrentMonth in der tabelle
     * @param comboBoxJahrAuswahl die ComboBoxJahresauswahl für Welche die Methode aufgerufen wird
     * @param tbTabelle die Tabelle welche durch änderungen in der ComboBoxJahresauswahl beeinflusst wird.
     */
    protected void handleOnComboBoxJahrAuswahlAction(ComboBox<Integer> comboBoxJahrAuswahl, TableView tbTabelle) {
        Integer year = comboBoxJahrAuswahl.getSelectionModel().getSelectedItem();
        firstOfCurrentMonth = firstOfCurrentMonth.withYear(year);
        scrollToSelectedMonth(firstOfCurrentMonth, tbTabelle);
    }

    /**
     * Ändert das ausgewählte Datum der übergebenen DatePicker auf das Übergebene LocalDate. Dies hat zur Folge, dass durch OnAction
     * methoden in der TabelleView eine auswahl getroffen wird, deswegen wird besagte Auswahl am Ende der Methode entfernt.
     * @param firstOfCurrentMonth neues default datum der DatePicker
     * @param dpVon Erster DatePicker dessen Default Datum geändert werden soll
     * @param dpBis Zweiter DatePicker dessen Default Datum geändert werden soll
     * @param tbTabelle Tabelle deren Auswahl entfernt werden soll
     */
    protected void updateDatePickers(LocalDate firstOfCurrentMonth, DatePicker dpVon, DatePicker dpBis, TableView tbTabelle) {
        dpVonOnActionCalledFromUpdateDatePickers = true;
        dpBisOnActionCalledFromUpdateDatePickers = true;
        dpBis.setValue(firstOfCurrentMonth);
        dpVon.setValue(firstOfCurrentMonth);
        tbTabelle.getSelectionModel().clearSelection();
    }

    /**
     * wird aufgerufen, wenn von einem EventFilter in einem der Drei abgeleiteten Klassen von ControllerBasisKlasse ein ScrollEvent
     * aufgegriffen wird. ermittelt zu welchem Tag in der Tabelle gescrollt wurde, passt die globale Variable FirstOfCurrentMonth so
     * an das ihr Monat, dem des Tages entspricht, zu dem Gescrollt wurde, passt die ComboBoxMonatAuswahl entsprechend an d. h. wählt
     * den richtigen Monat aus. Damit der dadurch ausgelöste ActionHandler auf der ComboBox weis, das er nicht tätig werden muss,
     * setzt sie den Boolean scrollLWasJustHandled auf true.
     * @param event ScrollEvent für welches die Methode aufgerufen wurde
     * @param comboBoxMonatAuswahl zu aktualisierende ComboBox zur Monat-Auswahl
     */
    protected void handleScrollEvent(ScrollEvent event, ComboBox comboBoxMonatAuswahl) {
        try{
            TableCell cell = (TableCell) event.getTarget();
            Tag tag = (Tag) cell.getTableRow().getItem();
            firstOfCurrentMonth = firstOfCurrentMonth.withMonth(tag.getDatum().getMonthValue());
            scrollWasJustHandled = true;
            comboBoxMonatAuswahl.getSelectionModel().select(firstOfCurrentMonth.getMonthValue() - 1);
        } catch (Exception ignored) {

        }
    }

    /**
     * Die Methode wird aufgerufen, wenn in einem der abgeleiteten Klassen der btNaechsterMonat oder btVorigerMonat geklickt wird.
     * Wenn der Button geklickt wird so wird das firstOfCurrentMonth zum vorherigen Monat geändert und entsprechend,
     * die ComboBox zur Monatsauswahl als auch gegebenenfalls die zur Jahresauswahl angepasst und die damit
     * verbundenen OnActions ausgeführt (was scrollen zum entsprechenden monat als auch ggf. aktualisieren der Daten der Tabelle beinhaltet).
     * Sollte das Jahr gewechselt werden, so wird ggf. Nutzerbestätigung eingeholt.
     * @param monthChange Anzahl um der Monate um die nach vorne oder hinten verschoben werden soll (+1 oder -1)
     * @param comboBoxMonatAuswahl comboBox zur Monatsauswahl
     * @param comboBoxJahrAuswahl combBox zur Jahresauswahl
     */
    protected void incrementOrDecrementMonatViaButton(Integer monthChange, ComboBox comboBoxMonatAuswahl, ComboBox comboBoxJahrAuswahl) {
        if (!monthChangeOperationShouldBeContinued(firstOfCurrentMonth, monthChange)) {
            return;
        }
        if(changingMonthWouldChangeYear(firstOfCurrentMonth, monthChange)) {
            dataHasBeenModified = false;
        }
        firstOfCurrentMonth = changeMonthBackOrForthBy(monthChange, firstOfCurrentMonth, comboBoxMonatAuswahl, comboBoxJahrAuswahl);
    }
}
