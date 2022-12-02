package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import com.example.gruppenkalenderfeiertragurlaub.gui.Controller;
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

//TODO document all methods in class
public class PDFCreator {
    public static void writePDF(ObservableList<GruppenKalenderTag> tagesListe, Stage parentStage) throws FileNotFoundException {
        if (tagesListe.isEmpty()) return;
        PdfWriter writer = new PdfWriter(getFilename(parentStage));
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

        Table table = constructTableAndColumnHeadlines();

        for (GruppenKalenderTag tag : tagesListe) {
            for (int i = 0; i < 4; i++) {
                Paragraph paragraph = new Paragraph();
                paragraph.setFontSize(12);
                paragraph.setTextAlignment(TextAlignment.CENTER);
                if (i == 0) paragraph.add(tag.getGruppenID().toString());
                if (i == 1) paragraph.add(tag.getDatum().toString());
                if (i == 2) paragraph.add(new Controller().getDisplayMessageForStatus(tag.getGruppenstatus()));
                if (i == 3) paragraph.add(convertEssenVerfuegbar(tag.getEssenFuerGruppeVerfuegbar()));

                Cell cell = new Cell();
                cell.add(paragraph);
                table.addCell(cell);
            }
        }
        document.add(headline);
        document.add(table);
        document.close();
    }

    private static String getFilename(Stage parentStage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Speicherort Auswahl");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File file = chooser.showSaveDialog(parentStage);
        return file.getAbsolutePath();
        //return "src/main/resources/PDFs/pdf.pdf";
    }

    private static Table constructTableAndColumnHeadlines() {
        float[] pointColumnWidths = {150F, 150F, 150F, 150F};
        Table table = new Table(pointColumnWidths);

        for (int i = 0; i < 4; i++) {
            Paragraph headlineParagraph = new Paragraph();
            headlineParagraph.setFontSize(16);
            headlineParagraph.setTextAlignment(TextAlignment.CENTER);
            if(i == 0) headlineParagraph.add("Gruppe");
            if(i == 1) headlineParagraph.add("Datum");
            if(i == 2) headlineParagraph.add("Status");
            if(i == 3) headlineParagraph.add("Essen Verfügbar");

            Cell headlineCell = new Cell();
            headlineCell.add(headlineParagraph);
            table.addCell(headlineCell);
        }
        return table;
    }

    private static String convertEssenVerfuegbar(Boolean essenVerfuegbar) {
        if (essenVerfuegbar == true) return "Ja";
        if (essenVerfuegbar == false) return "Nein";
        return "null";
    }
}
