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
            for (int i = 0; i < 3; i++) {
                Paragraph paragraph = new Paragraph();
                paragraph.setFontSize(12);
                paragraph.setTextAlignment(TextAlignment.CENTER);
                if (i == 0) paragraph.add(tag.getDatum().toString());
                if (i == 1) paragraph.add(new Controller().getDisplayMessageForStatus(tag.getGruppenstatus()));
                if (i == 2) paragraph.add(convertEssenVerfuegbar(tag.getEssenFuerGruppeVerfuegbar()));

                Cell cell = new Cell();
                ;
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
        File file = chooser.showSaveDialog(parentStage);
        return file.getAbsolutePath();
        //return "src/main/resources/PDFs/pdf.pdf";
    }

    private static Table constructTableAndColumnHeadlines() {
        float[] pointColumnWidths = {200F, 200F, 200F};
        Table table = new Table(pointColumnWidths);
        Paragraph paragraphDatum = new Paragraph();
        paragraphDatum.setFontSize(16);
        paragraphDatum.setTextAlignment(TextAlignment.CENTER);
        paragraphDatum.add("Datum");
        Paragraph paragraphStatus = new Paragraph();
        paragraphStatus.setFontSize(16);
        paragraphStatus.setTextAlignment(TextAlignment.CENTER);
        paragraphStatus.add("Status");
        Paragraph paragraphEssenVerfuegbar = new Paragraph();
        paragraphEssenVerfuegbar.setFontSize(16);
        paragraphEssenVerfuegbar.setTextAlignment(TextAlignment.CENTER);
        paragraphEssenVerfuegbar.add("Essen ist Verfügbar");

        Cell cellDatum = new Cell();
        cellDatum.add(paragraphDatum);
        table.addCell(cellDatum);

        Cell cellStatus = new Cell();
        cellStatus.add(paragraphStatus);
        table.addCell(cellStatus);

        Cell cellEssenVerfuegbar = new Cell();
        cellEssenVerfuegbar.add(paragraphEssenVerfuegbar);
        table.addCell(cellEssenVerfuegbar);
        return table;
    }

    private static String convertEssenVerfuegbar(Boolean essenVerfuegbar) {
        if (essenVerfuegbar == true) return "Ja";
        if (essenVerfuegbar == false) return "Nein";
        return "null";
    }
}
