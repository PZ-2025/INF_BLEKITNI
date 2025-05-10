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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**
 * Generator raportu obciążenia pracowników – progi godzin
 * oraz ścieżkę do logo można konfigurować setterami.
 */
public class WorkloadReportGenerator {
    private static final Logger logger = LogManager.getLogger(WorkloadReportGenerator.class);

    /* ---------- konfiguracja progów ---------- */
    private double lowerThreshold = 120;
    private double upperThreshold = 160;

    /* ----------  ścieżka do pliku z logo  ---------- */
    /** Domyślna lokalizacja logo (można ją zmienić setterem) */
    private String logoPath = "src/main/resources/logo.png";

    /* ======  GETTERY / SETTERY  ================================================= */

    public double getLowerThreshold() { return lowerThreshold; }
    public double getUpperThreshold() { return upperThreshold; }
    public String getLogoPath() { return logoPath; }

    public void setLowerThreshold(double value) {
        logger.debug("Setting lower threshold to: {}", value);
        if (value < 0 || value >= upperThreshold) {
            logger.error("Invalid lower threshold value: {}", value);
            throw new IllegalArgumentException("lowerThreshold musi być ≥0 i < upperThreshold");
        }
        this.lowerThreshold = value;
    }

    public void setUpperThreshold(double value) {
        logger.debug("Setting upper threshold to: {}", value);
        if (value <= lowerThreshold) {
            logger.error("Invalid upper threshold value: {}", value);
            throw new IllegalArgumentException("upperThreshold musi być > lowerThreshold");
        }
        this.upperThreshold = value;
    }

    public void setLogoPath(String path) {
        logger.debug("Setting logo path to: {}", path);
        if (path == null || path.isBlank()) {
            logger.error("Invalid logo path: {}", path);
            throw new IllegalArgumentException("logoPath nie może być puste");
        }
        this.logoPath = path;
    }

    /**
     * Generates a workload report as a PDF file.
     *
     * @param outputPath Path where the PDF file will be saved
     * @param startDate Start date of the reporting period
     * @param endDate End date of the reporting period
     * @param selectedPositions List of positions to include in the report
     * @throws Exception If there's an error during PDF generation
     */
    public void generateReport(String outputPath,
                               LocalDate startDate,
                               LocalDate endDate,
                               List<String> selectedPositions)
            throws Exception {

        logger.info("Starting report generation: outputPath={}, period={} to {}, positions={}",
                outputPath, startDate, endDate, selectedPositions);

        List<EmployeeWorkload> workloadData =
                loadWorkloadData(startDate, endDate, selectedPositions);

        if (workloadData == null || workloadData.isEmpty()) {
            logger.warn("No data available for report generation");
            throw new NoDataException("Brak danych do wygenerowania raportu.");
        }

        File targetFile = new File(outputPath);
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            logger.debug("Creating parent directory: {}", parentDir.getAbsolutePath());
            if (!parentDir.mkdirs()) {
                logger.error("Failed to create directory: {}", parentDir.getAbsolutePath());
                throw new IllegalStateException("Nie można utworzyć katalogu: " + parentDir);
            }
        }

        /* --- tworzenie PDF ----------------------------------------------------- */
        logger.debug("Creating PDF document");
        try (PdfWriter writer = new PdfWriter(outputPath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4)) {

            logger.debug("Setting up document font and styling");
            PdfFont font = PdfFontFactory.createFont(
                    "src/main/java/org/example/pdflib/NotoSans-VariableFont_wdth,wght.ttf", "Cp1250");
            document.setFont(font);

            /* nagłówek z logo i tytułem */
            logger.debug("Adding header with logo and title");
            Table header = new Table(new float[]{1, 3}).setWidth(500);

            File logoFile = new File(logoPath);
            if (!logoFile.exists()) {
                logger.error("Logo file not found: {}", logoPath);
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
            logger.debug("Creating main data table");
            Table table = new Table(new float[]{3, 2, 2, 2, 2})
                    .setWidth(UnitValue.createPercentValue(100));
            addHeader(table,
                    "ID pracownika",
                    "Stanowisko",
                    "Liczba zadań",
                    "Godziny",
                    "Status");

            int overloaded = 0, underloaded = 0, optimal = 0;
            for (EmployeeWorkload e : workloadData) {
                String status = getWorkloadStatus(e.totalHours());
                switch (status) {
                    case "Przeciążenie"  -> overloaded++;
                    case "Niedociążenie" -> underloaded++;
                    default              -> optimal++;
                }
                table.addCell(e.employeeName());
                table.addCell(e.department());
                table.addCell(String.valueOf(e.taskCount()));
                table.addCell(String.valueOf(e.totalHours()));
                table.addCell(status);
            }
            document.add(table).add(new Paragraph("\n"));

            logger.debug("Adding summary table");
            Table summary = new Table(new float[]{3, 1})
                    .setWidth(UnitValue.createPercentValue(100))
                    .setHorizontalAlignment(HorizontalAlignment.RIGHT);
            addHeader(summary, "Podsumowanie", "Liczba");
            summary.addCell("Przeciążeni").addCell(String.valueOf(overloaded));
            summary.addCell("Niedociążeni").addCell(String.valueOf(underloaded));
            summary.addCell("Optymalne").addCell(String.valueOf(optimal));

            document.add(summary);

            logger.info("Report successfully generated: {}", outputPath);
        } catch (Exception e) {
            logger.error("Error generating report: {}", e.getMessage(), e);
            throw e;
        }
    }

    /* ======  METODY POMOCNICZE  =============================================== */

    /**
     * Loads workload data for the specified period and positions.
     * This method should be overridden in production to fetch actual data.
     *
     * @param start Start date
     * @param end End date
     * @param positions List of positions to include
     * @return List of employee workload records
     */
    protected List<EmployeeWorkload> loadWorkloadData(LocalDate start, LocalDate end, List<String> positions) {
        logger.debug("Loading workload data for period {} to {}, positions: {}", start, end, positions);
        // This is a placeholder - in a real implementation, this would fetch data from a database
        return List.of(
                new EmployeeWorkload("Jan Kowalski", "Sprzedaż", 15, 175),
                new EmployeeWorkload("Anna Nowak", "Magazyn", 8, 95),
                new EmployeeWorkload("Piotr Wiśniewski", "IT", 12, 140)
        );
    }

    /**
     * Adds a header row to a table.
     *
     * @param t The table to add the header to
     * @param labels The header cell labels
     */
    private void addHeader(Table t, String... labels) {
        for (String l : labels) {
            t.addHeaderCell(new Cell().add(new Paragraph(l).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        }
    }

    /**
     * Determines the workload status based on hours worked.
     *
     * @param hours Number of hours worked
     * @return Status description (Przeciążenie, Niedociążenie, or Optymalne)
     */
    private String getWorkloadStatus(double hours) {
        if (hours > upperThreshold) return "Przeciążenie";
        if (hours < lowerThreshold) return "Niedociążenie";
        return "Optymalne";
    }

    /**
     * Record representing employee workload data.
     */
    public record EmployeeWorkload(String employeeName, String department,
                                   int taskCount, double totalHours) {}
}