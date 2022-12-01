package com.example.gruppenkalenderfeiertragurlaub.gui.helperKlassen;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
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
        headline.setFontSize(50);
        headline.setTextAlignment(TextAlignment.CENTER);
        headline.add("Monatsplan");

        Paragraph fuer = new Paragraph();
        fuer.setFontSize(16);
        fuer.setTextAlignment(TextAlignment.CENTER);
        fuer.add("für");

        Paragraph name = new Paragraph();
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

        document.add(headline);
        document.add(platzierung);
        document.add(fuer);
        document.add(name);
        document.add(haupttext);

        // Closing the document
        document.close();
    }
}
