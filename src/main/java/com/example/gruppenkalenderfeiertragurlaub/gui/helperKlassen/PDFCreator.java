package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import com.example.gruppenkalenderfeiertragurlaub.gui.Controller;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppeFuerKalender;
import com.example.gruppenkalenderfeiertragurlaub.speicherklassen.GruppenKalenderTag;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

public class PDFCreator {

    /**
     * Erstellt ein PDF dokument und schreibt es an eine vom Nutzer per FileChooser ausgewählte Stelle. Fügt dem PDF
     * File eine Überschrift, sowie eine Tabelle mit vier spalten, mit je einer Überschrift hinzu. Fügt die in der
     * tagesListe enthaltenen Daten in die Tabelle ein und erhält zusätzliche Informationen aus der Gruppenliste.
     * @param tagesListe Liste der Tage welche in dem PDF ausgedruckt werden sollen
     * @param parentStage Stage auf welcher der FileChooser aufgerufen werden soll. (stage des aufrufenden Dialogs)
     * @param gruppenListe Liste der Gruppen damit aus der gruppenID in Tagen der tagesliste der richtige Gruppenname
     *                     ermittelt werden kann.
     * @throws FileNotFoundException Wird geworfen, wenn der ausgewählte speicherort nicht gefunden werden konnte.
     */
    public static void writePDF(ObservableList<GruppenKalenderTag> tagesListe, Stage parentStage, ArrayList<GruppeFuerKalender> gruppenListe) throws FileNotFoundException {
        if (tagesListe.isEmpty()) return;
        String speicherortPfad = getSpeicherortVonUser(parentStage);
        if(speicherortPfad == null) return;

        PdfWriter writer = new PdfWriter(speicherortPfad);
        // Creating a PdfDocument
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.setDefaultPageSize(PageSize.A4);
        // Adding a new page
        pdfDocument.addNewPage();
        // Creating a Document
        Document document = new Document(pdfDocument);
        Paragraph headline = new Paragraph();
        headline.setFontSize(25);
        headline.setTextAlignment(TextAlignment.CENTER);
        headline.add("Monatsplan");

        float[] pointColumnWidths = {150F, 150F, 150F, 150F};
        Table table = new Table(pointColumnWidths);

        createPDFCell("Gruppe", 16, table);
        createPDFCell("Datum", 16, table);
        createPDFCell("Status", 16, table);
        createPDFCell("Essen Verfügbar", 16, table);

        for (GruppenKalenderTag tag : tagesListe) {
            createPDFCell(convertGruppenIDToName(tag, gruppenListe), 16, table);
            createPDFCell(tag.getDatum().toString(), 16, table);
            createPDFCell(Controller.getDisplayMessageForStatus(tag.getGruppenstatus()), 16, table);
            createPDFCell(convertEssenVerfuegbar(tag.getEssenFuerGruppeVerfuegbar()), 16, table);
        }
        document.add(headline);
        document.add(table);
        document.close();
    }

    /**
     * Liest aus dem übergebenen Tag die GruppenID aus und findet, falls vorhanden den Namen der Gruppe mit der
     * besagten GruppenID
     * @param tag Tag welcher die Gruppen ID enthält.
     * @param gruppenListe liste der Gruppen welche überprüft werden, um den richtigen Namen zur ID zu finden
     * @return den Gruppennamen als String falls vorhanden. Sonst "null"
     */
    private static String convertGruppenIDToName(GruppenKalenderTag tag, ArrayList<GruppeFuerKalender> gruppenListe) {
        for (GruppeFuerKalender gr : gruppenListe) {
            if(Objects.equals(gr.getGruppeId(), tag.getGruppenID())) {
                return gr.getGruppeName();
            }
        }
        return "null";
    }

    /**
     * Ruft einen FileChooser auf in dem der Nutzer auswählen kann, wo das zu erstellende PDF gespeichert werden soll.
     * Gibt den Pfad des Files zurück, welches der Nutzer für die Speicherung des PDFs ausgewählt hat zurück.
     * @param parentStage Stage auf welcher der FileChooser aufgerufen wird.
     * @return den Pfad des vom User ausgewählten Speicherortes (File).
     */
    private static String getSpeicherortVonUser(Stage parentStage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Speicherort Auswahl");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = chooser.showSaveDialog(parentStage);
        if(file == null) {
            return null;
        }
        return file.getAbsolutePath();
        //return "src/main/resources/PDFs/pdf.pdf";
    }

    /**
     * Erzeugt eine PDF-Zelle, setzt die Textgröße auf den übergebenen Font, schreibt den übergebenen Text
     * in die Zelle und fügt diese dan der übergebenen Tabelle hinzu
     * @param cellText text für die Zelle
     * @param fontSize Schriftgröße für die zu erstellende Zelle.
     * @param table Tabelle in welche die erzeugte Zelle hinzugefügt werden soll.
     */
    private static void createPDFCell(String cellText, int fontSize, Table table) {
        Paragraph paragraph = new Paragraph();
        paragraph.setFontSize(fontSize);
        paragraph.setTextAlignment(TextAlignment.CENTER);
        paragraph.add(cellText);
        Cell headlineCell = new Cell();
        headlineCell.add(paragraph);
        table.addCell(headlineCell);
    }

    /**
     * Gibt "Ja" zurück wenn der übergebene Boolean true ist. Ansonsten gibt die Methode "Nein" zurück.
     * @param essenVerfuegbar der zu überprüfende Boolean
     * @return "Ja" wenn true, ansonsten "Nein"
     */
    private static String convertEssenVerfuegbar(Boolean essenVerfuegbar) {
        if (essenVerfuegbar) return "Ja";
        return "Nein";
    }
}
