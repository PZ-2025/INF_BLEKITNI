package org.example.pdflib;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import org.example.sys.Employee;

import java.time.LocalDate;
import java.util.List;

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
        document.add(new Paragraph("Wybrane działy: " + String.join(", ", selectedDepartments)));
        document.add(new Paragraph("Rodzaje zadań: " + String.join(", ", selectedTaskTypes)));

        // Tabela z danymi
        Table table = new Table(5) // 5 kolumn
                .setMarginTop(20);

        // Nagłówki tabeli
        addHeaderCell(table, "Pracownik");
        addHeaderCell(table, "Dział");
        addHeaderCell(table, "Liczba zadań");
        addHeaderCell(table, "Godziny");
        addHeaderCell(table, "Status obciążenia");

        // Przykładowe dane
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
        table.addCell(new Paragraph(text)
                .setBold()
                .setBackgroundColor(0.9f, 0.9f, 0.9f));
    }

    private String getWorkloadStatus(double hours) {
        if (hours > 160) return "Przeciążenie";
        if (hours < 120) return "Niedociążenie";
        return "Optymalne";
    }

    // Przykładowa implementacja klasy pomocniczej
    private static class EmployeeWorkload {
        private String employeeName;
        private String department;
        private int taskCount;
        private double totalHours;

        // Gettery i settery
        public String getEmployeeName() { return employeeName; }
        public String getDepartment() { return department; }
        public int getTaskCount() { return taskCount; }
        public double getTotalHours() { return totalHours; }
    }

    private List<EmployeeWorkload> loadWorkloadData(LocalDate start, LocalDate end,
                                                    List<String> departments,
                                                    List<String> taskTypes) {
        // Przykładowe dane testowe
        return List.of(
                new Employee() {{
                    employeeName = "Jan Kowalski";
                    department = "Sprzedaż";
                    taskCount = 15;
                    totalHours = 175;
                }},
                new Employee() {{
                    employeeName = "Anna Nowak";
                    department = "Magazyn";
                    taskCount = 8;
                    totalHours = 95;
                }}
        );
    }
}