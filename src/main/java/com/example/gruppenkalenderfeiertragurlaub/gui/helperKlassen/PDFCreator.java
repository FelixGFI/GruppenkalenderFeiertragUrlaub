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

public class PDFCreator {

    /**
     * erstellt ein PDF dokument und schreibt es an eine vom Nutzer per Filechoser ausgewählte stelle. Fügt dem PDF
     * File eine Überschrift, sowie eine Tabelle mit vier spalten, mit je einer Überschrift hinzu. Fügt die in der
     * tagesListe enthaltenen Daten in die Tabelle ein und erhält zusätzliche Informationen aus der Gruppenliste.
     * @param tagesListe Liste der Tage welche in dem PDF ausgedruckt werden sollen
     * @param parentStage stage auf der der Filechoser aufgerufen werden soll. (stage des aufrufenden Dialogs)
     * @param gruppenListe liste der Grupppen damit aus der gruppenID in tagen der tagesliste der richtige Gruppenname
     *                     ermittelt werden kann
     * @throws FileNotFoundException Wird geworfen wenn der Ausgwählte speicherort nicht gefunden werden konnte.
     */
    public static void writePDF(ObservableList<GruppenKalenderTag> tagesListe, Stage parentStage, ArrayList<GruppeFuerKalender> gruppenListe) throws FileNotFoundException {
        if (tagesListe.isEmpty()) return;
        PdfWriter writer = new PdfWriter(getSpeicherortVonUser(parentStage));
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
            createPDFCell(new Controller().getDisplayMessageForStatus(tag.getGruppenstatus()), 16, table);
            createPDFCell(convertEssenVerfuegbar(tag.getEssenFuerGruppeVerfuegbar()), 16, table);
        }
        document.add(headline);
        document.add(table);
        document.close();
    }

    /**
     * ließt aus dem übergebenen Tag die GruppenID aus und findet falls vorhanden den namen der Gruppe mit der
     * besagten GruppenID
     * @param tag tag welcher die Gruppen ID Enthälts
     * @param gruppenListe liste der Gruppen welche überprüft werden um den richtigen Namen zur ID zu finden
     * @return den Gruppennamen als String falls vorhanden. Sonst "null"
     */
    private static String convertGruppenIDToName(GruppenKalenderTag tag, ArrayList<GruppeFuerKalender> gruppenListe) {
        for (GruppeFuerKalender gr : gruppenListe) {
            if(gr.getGruppeId() == tag.getGruppenID()) {
                return gr.getGruppeName();
            }
        }
        return "null";
    }

    /**
     * ruft einne Filechoser auf in dem der Nutzer auswaählen kann wo das zu erstellende PDF gespeichert werden soll.
     * Gibt den Filepfad zurück
     * @param parentStage stage auf der der FileChoser Aufgerufen wird
     * @return den Filepfad des vom User ausgewählten speicherortes
     */
    private static String getSpeicherortVonUser(Stage parentStage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Speicherort Auswahl");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = chooser.showSaveDialog(parentStage);
        return file.getAbsolutePath();
        //return "src/main/resources/PDFs/pdf.pdf";
    }

    /**
     * Erzeugt eine PDF Zelle, setzt die Textgröße auf den Übergebenen Font, schreibt den Übergebenen Text
     * in die Zelle und fügt diese dan der Übergebenen Tabelle hinzu
     * @param cellText text für die Zelle
     * @param fontSize schriftgrößer für die Zelle
     * @param table Tabelle in die die erzeugte zelle hinzugefügt werden soll
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
     * wandelt einen Boolean von true, false und null in Strings "Ja", "Nein", und "null" um
     * @param essenVerfuegbar der zu überprüfende Boolean
     * @return "Ja" wenn true "Nein" wenn false ansonsten "null"
     */
    private static String convertEssenVerfuegbar(Boolean essenVerfuegbar) {
        if (essenVerfuegbar == true) return "Ja";
        if (essenVerfuegbar == false) return "Nein";
        return "null";
    }
}
