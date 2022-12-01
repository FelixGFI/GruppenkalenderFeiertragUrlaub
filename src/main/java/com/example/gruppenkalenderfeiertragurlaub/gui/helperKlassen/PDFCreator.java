package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import java.io.FileNotFoundException;

public class PDFCreator {
    public static void writePDF() throws FileNotFoundException {
        PdfWriter writer = new PdfWriter("src/main/resources/PDFs/pdf.pdf");
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

        float [] pointColumnWidths = {200F, 200F, 200F};
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

        for (int i = 0; i < 500; i++) {
            Paragraph paragraphCell1 = new Paragraph();
            paragraphCell1.setFontSize(12);
            paragraphCell1.setTextAlignment(TextAlignment.CENTER);
            paragraphCell1.add("cell1");

            Cell cell1 = new Cell();;
            cell1.add(paragraphCell1);

            table.addCell(cell1);
        }

        document.add(headline);
        document.add(table);

        /*Paragraph name = new Paragraph();
        name.setTextAlignment(TextAlignment.CENTER);
        name.setFontSize(25);
        name.add("test");

        Paragraph platzierung = new Paragraph();
        platzierung.setTextAlignment(TextAlignment.CENTER);
        Text platz = new Text("Platz ");
        platz.setFontSize(16);
        Text platzZahl = new Text("moreTest");
        platzZahl.setFontSize(50);
        platzierung.add(platz);
        platzierung.add(platzZahl);

        Paragraph haupttext = new Paragraph();
        haupttext.setFontSize(16);
        haupttext.setTextAlignment(TextAlignment.CENTER);
        haupttext.add("evene more test" + "\n");

        document.add(platzierung);
        document.add(paragraphCell1);
        document.add(name);
        document.add(haupttext);*/

        // Closing the document
        document.close();
    }
}
