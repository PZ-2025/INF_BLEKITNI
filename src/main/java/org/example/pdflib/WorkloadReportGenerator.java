package org.example.pdflib;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class WorkloadReportGenerator {
    private static final Logger logger = LogManager.getLogger(WorkloadReportGenerator.class);
    //TODO: Trzy sposoby filtrowania
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
                               List<String> selectedDepartments) throws NoDataException {
        try {
            logger.info("Rozpoczynanie generowania raportu: {}", outputPath);

            File outputFile = new File(outputPath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (parentDir.mkdirs()) {
                    logger.info("Utworzono katalog: {}", parentDir.getAbsolutePath());
                } else {
                    logger.warn("Nie udało się utworzyć katalogu: {}", parentDir.getAbsolutePath());
                }
            }

            List<EmployeeWorkload> workloadData = loadWorkloadData(startDate, endDate, selectedDepartments);
            if (workloadData == null || workloadData.isEmpty()) {
                logger.warn("Brak danych do wygenerowania raportu.");
                throw new NoDataException("Brak danych do wygenerowania raportu.");
            }

            PdfWriter writer = new PdfWriter(outputPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            String FONT = "src/main/java/org/example/pdflib/NotoSans-VariableFont_wdth,wght.ttf";
            PdfFont font = PdfFontFactory.createFont(FONT, "Cp1250");
            document.setFont(font);

            Paragraph header = new Paragraph("Raport obciążenia pracowników")
                    .setFontSize(20)
                    .setBold();
            document.add(header);

            document.add(new Paragraph("\nData wygenerowania: " + LocalDate.now()));
            document.add(new Paragraph("Okres raportowania: " + startDate + " - " + endDate));
            document.add(new Paragraph("Wybrane działy: " + String.join(", ", selectedDepartments)));

            Table table = new Table(5).setMarginTop(20);
            addHeaderCell(table, "Pracownik");
            addHeaderCell(table, "Dział");
            addHeaderCell(table, "Liczba zadań");
            addHeaderCell(table, "Godziny");
            addHeaderCell(table, "Status obciążenia");

            for (EmployeeWorkload entry : workloadData) {
                table.addCell(entry.getEmployeeName());
                table.addCell(entry.getDepartment());
                table.addCell(String.valueOf(entry.getTaskCount()));
                table.addCell(String.valueOf(entry.getTotalHours()));
                table.addCell(getWorkloadStatus(entry.getTotalHours()));
            }

            document.add(table);
            document.close();
            logger.info("Raport został pomyślnie zapisany do: {}", outputPath);
        } catch (NoDataException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Błąd podczas generowania raportu: {}", e.getMessage(), e);
        }
    }

    private void addHeaderCell(Table table, String text) {
        Cell headerCell = new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY);
        table.addCell(headerCell);
    }

    private String getWorkloadStatus(double hours) {
        if (hours > 160) return "Przeciążenie";
        if (hours < 120) return "Niedociążenie";
        return "Optymalne";
    }

    protected List<EmployeeWorkload> loadWorkloadData(LocalDate start, LocalDate end,
                                                      List<String> departments) {
        logger.debug("Ładowanie przykładowych danych testowych dla raportu...");
        List<EmployeeWorkload> list = new ArrayList<>();
        list.add(new EmployeeWorkload("Jan Kowalski", "Sprzedaż", 15, 175));
        list.add(new EmployeeWorkload("Anna Nowak", "Magazyn", 8, 95));
        return list;
    }

    public static class EmployeeWorkload {
        private final String employeeName;
        private final String department;
        private final int taskCount;
        private final double totalHours;

        public EmployeeWorkload(String employeeName, String department, int taskCount, double totalHours) {
            this.employeeName = employeeName;
            this.department = department;
            this.taskCount = taskCount;
            this.totalHours = totalHours;
        }

        public String getEmployeeName() { return employeeName; }
        public String getDepartment() { return department; }
        public int getTaskCount() { return taskCount; }
        public double getTotalHours() { return totalHours; }
    }
}
