package org.example.pdflib;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ReportGenerator {

    /**
     * Generuje prosty raport PDF.
     *
     * @param reportName  nazwa raportu (np. "Raport sprzedaży")
     * @param filters     mapa filtrów (klucz → wartość)
     * @return plik PDF z raportem
     * @throws Exception w przypadku błędów IO lub PDF
     */
    public static File generate(String reportName, Map<String, String> filters) throws Exception {
        // ścieżka katalogu do zapisu (z ConfigManager)
        String dirPath = ConfigManager.getReportPath();
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Niepoprawna ścieżka raportów: " + dirPath);
        }

        // tworzymy nazwę pliku: typ + znacznik czasu
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String safeName = reportName.toLowerCase()
                .replace(" ", "_")
                .replaceAll("[^a-z0-9_]", "");
        String fileName = safeName + "_" + timestamp + ".pdf";

        File outFile = new File(dir, fileName);
        outFile.getParentFile().mkdirs();

        // ustawienie writer + document
        PdfWriter writer = new PdfWriter(outFile);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // nagłówek
        document.add(new Paragraph(reportName)
                .setFontSize(18)
                .setBold());
        document.add(new Paragraph("Wygenerowano: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        document.add(new Paragraph(" "));

        // treść filtrów
        document.add(new Paragraph("Użyte filtry:").setBold());
        filters.forEach((k, v) -> {
            document.add(new Paragraph(String.format("• %s: %s", k, v != null ? v : "(brak)")));
        });

        document.add(new Paragraph(" "));
        document.add(new Paragraph("<< Tutaj dodaj swoje dane raportu >>"));

        document.close();
        return outFile;
    }
}
