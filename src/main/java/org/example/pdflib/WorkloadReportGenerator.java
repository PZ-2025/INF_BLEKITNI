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

    public void generateReport(String outputPath,
                               LocalDate startDate,
                               LocalDate endDate,
                               List<String> selectedDepartments,
                               List<String> selectedTaskTypes) {
        try {
            logger.info("Rozpoczynanie generowania raportu: {}", outputPath);

            // Upewniamy się, że katalog istnieje
            File outputFile = new File(outputPath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (parentDir.mkdirs()) {
                    logger.info("Utworzono katalog: {}", parentDir.getAbsolutePath());
                } else {
                    logger.warn("Nie udało się utworzyć katalogu: {}", parentDir.getAbsolutePath());
                }
            }

            PdfWriter writer = new PdfWriter(outputPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Font do obsługi polskich znaków
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
            document.add(new Paragraph("Rodzaje zadań: " + String.join(", ", selectedTaskTypes)));

            Table table = new Table(5).setMarginTop(20);
            addHeaderCell(table, "Pracownik");
            addHeaderCell(table, "Dział");
            addHeaderCell(table, "Liczba zadań");
            addHeaderCell(table, "Godziny");
            addHeaderCell(table, "Status obciążenia");

            List<EmployeeWorkload> workloadData = loadWorkloadData(startDate, endDate, selectedDepartments, selectedTaskTypes);
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

    private List<EmployeeWorkload> loadWorkloadData(LocalDate start, LocalDate end,
                                                    List<String> departments,
                                                    List<String> taskTypes) {
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
