package org.example.pdflib;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**
 * Generator raportu obciążenia pracowników – progi godzin
 * oraz ścieżkę do logo można konfigurować setterami.
 */
public class WorkloadReportGenerator {

    /* ---------- konfiguracja progów ---------- */
    private double lowerThreshold = 120;
    private double upperThreshold = 160;

    /* ----------  ścieżka do pliku z logo  ---------- */
    /** Domyślna lokalizacja logo (można ją zmienić setterem) */
    private String logoPath = "src/main/resources/logo.png";

    /* ======  GETTERY / SETTERY  ================================================= */

    public double getLowerThreshold()              { return lowerThreshold; }
    public double getUpperThreshold()              { return upperThreshold; }
    public String  getLogoPath()                   { return logoPath;     }

    public void setLowerThreshold(double value) {
        if (value < 0 || value >= upperThreshold) {
            throw new IllegalArgumentException("lowerThreshold musi być ≥0 i < upperThreshold");
        }
        this.lowerThreshold = value;
    }
    public void setUpperThreshold(double value) {
        if (value <= lowerThreshold) {
            throw new IllegalArgumentException("upperThreshold musi być > lowerThreshold");
        }
        this.upperThreshold = value;
    }
    public void setLogoPath(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("logoPath nie może być puste");
        }
        this.logoPath = path;
    }

    public void generateReport(String outputPath,
                               LocalDate startDate,
                               LocalDate endDate,
                               List<String> selectedPositions)
            throws Exception {

        List<EmployeeWorkload> workloadData =
                loadWorkloadData(startDate, endDate, selectedPositions);

        if (workloadData == null || workloadData.isEmpty()) {
            throw new NoDataException("Brak danych do wygenerowania raportu.");
        }

        File targetFile = new File(outputPath);
        File parentDir  = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new IllegalStateException("Nie można utworzyć katalogu: " + parentDir);
        }

        /* --- tworzenie PDF ----------------------------------------------------- */
        try (PdfWriter writer = new PdfWriter(outputPath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4)) {

            PdfFont font = PdfFontFactory.createFont(
                    "src/main/java/org/example/pdflib/NotoSans-VariableFont_wdth,wght.ttf", "Cp1250");
            document.setFont(font);

            /* nagłówek z logo i tytułem */
            Table header = new Table(new float[]{1, 3}).setWidth(500);

            File logoFile = new File(logoPath);
            if (!logoFile.exists()) {
                throw new IllegalStateException("Nie znaleziono pliku logo: " + logoPath);
            }
            ImageData logo = ImageDataFactory.create(logoFile.getAbsolutePath());
            header.addCell(new Cell().add(new Image(logo).scaleToFit(80, 80))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER));

            header.addCell(new Cell().add(
                            new Paragraph("Raport obciążenia pracowników")
                                    .setFontSize(20).setBold())
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
            document.add(header);

            document.add(new Paragraph("Data wygenerowania: " + LocalDate.now()).setMarginBottom(10));
            document.add(new Paragraph("Okres raportowania: " + startDate + " – " + endDate));
            document.add(new Paragraph("Wybrane stanowiska: " +
                    String.join(", ", selectedPositions)));
            document.add(new Paragraph("\n"));

            /* tabela główna */
            Table table = new Table(new float[]{3, 2, 2, 2, 2})
                    .setWidth(UnitValue.createPercentValue(100));
            addHeader(table,
                    "ID pracownika",
                    "Stanowisko",
                    "Liczba zadań",
                    "Godziny",
                    "Status");

            int ob = 0, nob = 0, opt = 0;
            for (EmployeeWorkload e : workloadData) {
                String status = getWorkloadStatus(e.totalHours());
                switch (status) {
                    case "Przeciążenie"  -> ob++;
                    case "Niedociążenie" -> nob++;
                    default              -> opt++;
                }
                table.addCell(e.employeeName());
                table.addCell(e.department());
                table.addCell(String.valueOf(e.taskCount()));
                table.addCell(String.valueOf(e.totalHours()));
                table.addCell(status);
            }
            document.add(table).add(new Paragraph("\n"));

            Table summary = new Table(new float[]{3, 1})
                    .setWidth(UnitValue.createPercentValue(100))
                    .setHorizontalAlignment(HorizontalAlignment.RIGHT);
            addHeader(summary, "Podsumowanie", "Liczba");
            summary.addCell("Przeciążeni").addCell(String.valueOf(ob));
            summary.addCell("Niedociążeni").addCell(String.valueOf(nob));
            summary.addCell("Optymalne").addCell(String.valueOf(opt));

            document.add(summary);
        }
    }

    /* ======  METODY POMOCNICZE  =============================================== */

    protected List<EmployeeWorkload>
    loadWorkloadData(LocalDate start, LocalDate end, List<String> positions) {
        return List.of();
    }

    private void addHeader(Table t, String... labels) {
        for (String l : labels) {
            t.addHeaderCell(new Cell().add(new Paragraph(l).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        }
    }

    private String getWorkloadStatus(double hours) {
        if (hours > upperThreshold) return "Przeciążenie";
        if (hours < lowerThreshold) return "Niedociążenie";
        return "Optymalne";
    }

    public record EmployeeWorkload(String employeeName, String department,
                                   int taskCount, double totalHours) {}
}
