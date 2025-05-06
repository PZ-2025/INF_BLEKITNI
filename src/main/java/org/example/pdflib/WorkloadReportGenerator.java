package org.example.pdflib;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class WorkloadReportGenerator {

    public void generateReport(String outputPath,
                               LocalDate startDate,
                               LocalDate endDate,
                               List<String> selectedDepartments,
                               List<String> selectedTaskTypes) throws Exception {

        PdfWriter writer = new PdfWriter(outputPath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Nagłówek raportu
        Paragraph header = new Paragraph("Raport obciążenia pracowników")
                .setFontSize(20)
                .setBold();
        document.add(header);

        // Informacje o parametrach
        document.add(new Paragraph("\nData wygenerowania: " + LocalDate.now()));
        document.add(new Paragraph("Okres raportowania: " + startDate + " - " + endDate));
        document.add(new Paragraph("Wybrane dzialy: " + String.join(", ", selectedDepartments)));
        document.add(new Paragraph("Rodzaje zadań: " + String.join(", ", selectedTaskTypes)));

        // Tabela z danymi
        Table table = new Table(5).setMarginTop(20);

        // Nagłówki
        addHeaderCell(table, "Pracownik");
        addHeaderCell(table, "Dział");
        addHeaderCell(table, "Liczba zadań");
        addHeaderCell(table, "Godziny");
        addHeaderCell(table, "Status obciążenia");

        // Dane
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

    // Dane testowe
    private List<EmployeeWorkload> loadWorkloadData(LocalDate start, LocalDate end,
                                                    List<String> departments,
                                                    List<String> taskTypes) {
        List<EmployeeWorkload> list = new ArrayList<>();
        list.add(new EmployeeWorkload("Jan Kowalski", "Sprzedaż", 15, 175));
        list.add(new EmployeeWorkload("Anna Nowak", "Magazyn", 8, 95));
        return list;
    }

    // Klasa pomocnicza
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
