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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generator raportów statystyk i wydajności zadań.
 * <p>
 * Klasa umożliwia generowanie raportów PDF przedstawiających kluczowe wskaźniki wydajności (KPI)
 * związane z realizacją zadań w hipermarkecie. Raport zawiera informacje o średnim czasie wykonania zadań,
 * liczbie zadań przypadającej na pracownika oraz wskaźnikach terminowości realizacji.
 * </p>
 * <p>
 * Raport może być filtrowany według zakresu dat, działu/obszaru oraz typu/priorytetu zadania.
 * </p>
 *
 * @author BŁĘKITNI
 * @version 1.0.0
 */
public class StatsRaportGenerator {
    private static final Logger logger = LogManager.getLogger(StatsRaportGenerator.class);

    /** Ścieżka do pliku z logo firmy */
    private String logoPath = "src/main/resources/logo.png";

    /** Lista danych o zadaniach */
    private List<TaskRecord> taskData;

    /**
     * Typ okresu filtrowania
     */
    public enum PeriodType {
        DAILY, WEEKLY, MONTHLY
    }

    /**
     * Priorytet zadania
     */
    public enum Priority {
        HIGH, MEDIUM, LOW
    }

    /**
     * Zwraca ścieżkę do pliku z logo.
     */
    public String getLogoPath() {
        return logoPath;
    }

    /**
     * Ustawia ścieżkę do pliku z logo.
     */
    public void setLogoPath(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Ścieżka do logo nie może być pusta");
        }
        this.logoPath = path;
    }

    /**
     * Ustawia dane o zadaniach.
     */
    public void setTaskData(List<TaskRecord> taskData) {
        this.taskData = taskData;
    }

    /**
     * Generuje raport statystyk i wydajności zadań.
     */
    public void generateReport(String outputPath,
                               LocalDate reportDate,
                               PeriodType periodType,
                               List<String> selectedDepartments,
                               List<String> selectedCategories,
                               List<Priority> selectedPriorities) throws Exception {
        // Obliczenie dat na podstawie typu okresu
        LocalDate endDate = adjustEndDate(reportDate, periodType);
        LocalDate startDate = calculateStartDate(periodType, endDate);

        // Sprawdzenie danych wejściowych
        if (taskData == null || taskData.isEmpty()) {
            throw new NoDataException("Brak danych do wygenerowania raportu.");
        }

        // Filtrowanie danych
        List<TaskRecord> filteredTasks = filterTasks(startDate, endDate, selectedDepartments, selectedCategories, selectedPriorities);

        // Tworzenie katalogu docelowego
        createOutputDirectory(outputPath);

        // Generowanie dokumentu PDF
        try (PdfWriter writer = new PdfWriter(outputPath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4)) {

            // Ustawienie czcionki
            PdfFont font = PdfFontFactory.createFont(
                    "src/main/java/org/example/pdflib/NotoSans-VariableFont_wdth,wght.ttf", "Cp1250");
            document.setFont(font);

            // Nagłówek z logo
            addHeader(document);

            // Informacje o raporcie
            addReportInfo(document, startDate, endDate, periodType, selectedDepartments, selectedCategories, selectedPriorities);

            // Tabela z danymi KPI
            document.add(createKpiSummaryTable(filteredTasks));

            // Tabela z danymi szczegółowymi
            document.add(new Paragraph("\n"));
            document.add(createTaskTable(filteredTasks));
        }
    }

    private LocalDate adjustEndDate(LocalDate date, PeriodType periodType) {
        return switch (periodType) {
            case DAILY -> date;
            case WEEKLY -> date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            case MONTHLY -> date.with(TemporalAdjusters.lastDayOfMonth());
        };
    }

    private LocalDate calculateStartDate(PeriodType periodType, LocalDate endDate) {
        return switch (periodType) {
            case DAILY -> endDate;
            case WEEKLY -> endDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case MONTHLY -> endDate.with(TemporalAdjusters.firstDayOfMonth());
        };
    }

    private List<TaskRecord> filterTasks(LocalDate startDate, LocalDate endDate,
                                         List<String> selectedDepartments, List<String> selectedCategories,
                                         List<Priority> selectedPriorities) {
        // Filtracja po działach
        List<TaskRecord> filteredByDepartment = taskData.stream()
                .filter(task -> selectedDepartments.isEmpty() || selectedDepartments.contains(task.department()))
                .collect(Collectors.toList());

        // Filtracja po kategorii
        List<TaskRecord> filteredByCategory = filteredByDepartment.stream()
                .filter(task -> selectedCategories.isEmpty() || selectedCategories.contains(task.category()))
                .collect(Collectors.toList());

        // Filtracja po priorytecie
        List<TaskRecord> filteredByPriority = filteredByCategory.stream()
                .filter(task -> selectedPriorities.isEmpty() ||
                        (task.priority() != null && selectedPriorities.contains(task.priority())))
                .collect(Collectors.toList());

        // Filtracja po dacie
        List<TaskRecord> filteredByDate = filteredByPriority.stream()
                .filter(task -> {
                    LocalDate dateToCheck = task.completionDate() != null ? task.completionDate() : task.dueDate();
                    return dateToCheck != null && !dateToCheck.isBefore(startDate) && !dateToCheck.isAfter(endDate);
                })
                .collect(Collectors.toList());

        if (filteredByDate.isEmpty()) {
            throw new NoDataException("Brak danych dla wybranych filtrów");
        }

        return filteredByDate;
    }

    private void createOutputDirectory(String outputPath) {
        File targetFile = new File(outputPath);
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new IllegalStateException("Nie można utworzyć katalogu: " + parentDir);
        }
    }

    private void addHeader(Document document) throws Exception {
        Table header = new Table(new float[]{1, 3}).setWidth(500);

        // Logo
        File logoFile = new File(logoPath);
        if (!logoFile.exists()) {
            throw new IllegalStateException("Nie znaleziono pliku logo: " + logoPath);
        }
        ImageData logo = ImageDataFactory.create(logoFile.getAbsolutePath());
        header.addCell(new Cell().add(new Image(logo).scaleToFit(80, 80))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.CENTER));

        // Tytuł
        header.addCell(new Cell().add(
                        new Paragraph("Raport statystyk i wydajności zadań")
                                .setFontSize(20).setBold())
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));

        document.add(header);
    }

    private void addReportInfo(Document document, LocalDate startDate, LocalDate endDate,
                               PeriodType periodType, List<String> selectedDepartments,
                               List<String> selectedCategories, List<Priority> selectedPriorities) throws Exception {
        document.add(new Paragraph("Data wygenerowania: " + LocalDate.now()).setMarginBottom(10));
        document.add(new Paragraph("Okres raportowania: " + startDate + " – " + endDate));
        document.add(new Paragraph("Typ okresu: " + periodType));
        document.add(new Paragraph("Wybrane działy: " +
                (selectedDepartments.isEmpty() ? "Wszystkie" : String.join(", ", selectedDepartments))));
        document.add(new Paragraph("Wybrane kategorie: " +
                (selectedCategories.isEmpty() ? "Wszystkie" : String.join(", ", selectedCategories))));

        if (selectedPriorities != null && !selectedPriorities.isEmpty()) {
            document.add(new Paragraph("Filtry priorytetów: " +
                    selectedPriorities.stream().map(Enum::name).collect(Collectors.joining(", "))));
        }
        document.add(new Paragraph("\n"));
    }

    private Table createKpiSummaryTable(List<TaskRecord> tasks) {
        Table summary = new Table(new float[]{3, 1})
                .setWidth(UnitValue.createPercentValue(100))
                .setHorizontalAlignment(HorizontalAlignment.CENTER);

        addHeader(summary, "Wskaźnik", "Wartość");

        // Obliczanie KPI
        double avgCompletionTime = calculateAverageCompletionTime(tasks);
        int tasksPerEmployee = calculateTasksPerEmployee(tasks);
        double onTimePercentage = calculateOnTimePercentage(tasks);
        double overduePercentage = calculateOverduePercentage(tasks);

        summary.addCell("Średni czas wykonania [dni]").addCell(String.format("%.1f", avgCompletionTime));
        summary.addCell("Zadania na pracownika").addCell(String.valueOf(tasksPerEmployee));
        summary.addCell("Zadania terminowe [%]").addCell(String.format("%.1f", onTimePercentage));
        summary.addCell("Zadania opóźnione [%]").addCell(String.format("%.1f", overduePercentage));

        return summary;
    }

    private double calculateAverageCompletionTime(List<TaskRecord> tasks) {
        return tasks.stream()
                .filter(task -> task.completionDate() != null && task.dueDate() != null)
                .mapToDouble(task -> ChronoUnit.DAYS.between(task.dueDate(), task.completionDate()))
                .average()
                .orElse(0);
    }

    private int calculateTasksPerEmployee(List<TaskRecord> tasks) {
        long uniqueEmployees = tasks.stream()
                .map(TaskRecord::assignee)
                .distinct()
                .count();

        return uniqueEmployees > 0 ? (int) Math.ceil((double) tasks.size() / uniqueEmployees) : 0;
    }

    private double calculateOnTimePercentage(List<TaskRecord> tasks) {
        long total = tasks.size();
        if (total == 0) return 0;

        long onTime = tasks.stream()
                .filter(task -> task.completionDate() != null &&
                        !task.completionDate().isAfter(task.dueDate()))
                .count();

        return (double) onTime / total * 100;
    }

    private double calculateOverduePercentage(List<TaskRecord> tasks) {
        long total = tasks.size();
        if (total == 0) return 0;

        long overdue = tasks.stream()
                .filter(task -> task.completionDate() != null &&
                        task.completionDate().isAfter(task.dueDate()))
                .count();

        return (double) overdue / total * 100;
    }

    private Table createTaskTable(List<TaskRecord> tasks) {
        Table table = new Table(new float[]{3, 2, 2, 2, 2, 2})
                .setWidth(UnitValue.createPercentValue(100));

        addHeader(table, "Zadanie", "Dział", "Kategoria", "Priorytet", "Termin", "Pracownik");

        for (TaskRecord task : tasks) {
            table.addCell(task.taskName());
            table.addCell(task.department());
            table.addCell(task.category());
            table.addCell(task.priority() != null ? task.priority().toString() : "Brak");
            table.addCell(task.dueDate().toString());
            table.addCell(task.assignee());
        }

        return table;
    }

    private void addHeader(Table t, String... labels) {
        for (String l : labels) {
            t.addHeaderCell(new Cell().add(new Paragraph(l).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        }
    }

    /**
     * Rekord reprezentujący dane o zadaniu.
     */
    public record TaskRecord(String taskName, String department, String category,
                             Priority priority, LocalDate dueDate, LocalDate completionDate,
                             String assignee) {}

    /**
     * Wyjątek braku danych.
     */
    public static class NoDataException extends RuntimeException {
        public NoDataException(String message) {
            super(message);
        }
    }
}