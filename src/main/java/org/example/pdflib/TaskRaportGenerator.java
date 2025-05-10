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
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generator raportów realizacji zadań w hipermarkecie.
 * <p>
 * Klasa umożliwia generowanie raportów PDF przedstawiających status realizacji zadań
 * w określonym okresie. Raport zawiera informacje o zadaniach zakończonych, w trakcie
 * realizacji oraz zaległych, z możliwością filtrowania po kategorii i statusie.
 * </p>
 */
public class TaskRaportGenerator {
    private static final Logger logger = LogManager.getLogger(TaskRaportGenerator.class);
    /** Ścieżka do pliku z logo firmy */
    private String logoPath = "src/main/resources/logo.png";
    /** Lista danych o zadaniach */
    private List<TaskRecord> taskData;

    public enum PeriodType {
        LAST_WEEK, LAST_MONTH, LAST_QUARTER
    }

    /**
     * Zwraca ścieżkę do pliku z logo.
     */
    public String getLogoPath() { return logoPath; }

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
     * Generuje raport wykonania zadań.
     */
    public void generateReport(String outputPath,
                               PeriodType periodType,
                               List<String> selectedCategories,
                               List<String> selectedStatuses) throws Exception {
        // Obliczenie dat na podstawie typu okresu
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = calculateStartDate(periodType, endDate);

        // Sprawdzenie danych wejściowych
        if (taskData == null || taskData.isEmpty()) {
            throw new NoDataException("Brak danych do wygenerowania raportu.");
        }

        // Filtrowanie danych
        List<TaskWithStatus> filteredTasks = filterTasks(startDate, endDate, selectedCategories, selectedStatuses);

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
            addReportInfo(document, startDate, endDate, periodType, selectedCategories, selectedStatuses);

            // Tabela z danymi zadań
            Table taskTable = createTaskTable();
            addTaskTableData(taskTable, filteredTasks);
            document.add(taskTable);

            // Podsumowanie
            document.add(new Paragraph("\n"));
            document.add(createSummaryTable(filteredTasks));
        }
    }

    private LocalDate calculateStartDate(PeriodType periodType, LocalDate endDate) {
        return switch (periodType) {
            case LAST_WEEK -> endDate.minusWeeks(1);
            case LAST_MONTH -> endDate.minusMonths(1);
            case LAST_QUARTER -> endDate.minusMonths(3);
            default -> throw new IllegalArgumentException("Nieprawidłowy typ okresu");
        };
    }

    private List<TaskWithStatus> filterTasks(LocalDate startDate, LocalDate endDate,
                                             List<String> selectedCategories, List<String> selectedStatuses) {
        // Filtracja po kategorii
        List<TaskRecord> filteredByCategory = taskData.stream()
                .filter(task -> selectedCategories.isEmpty() || selectedCategories.contains(task.category()))
                .collect(Collectors.toList());

        // Dodatkowa filtracja po dacie
        List<TaskWithStatus> tasksWithStatus = new ArrayList<>();
        for (TaskRecord task : filteredByCategory) {
            LocalDate dateToCheck = task.completionDate() != null ? task.completionDate() : task.dueDate();
            if (dateToCheck != null && !dateToCheck.isBefore(startDate) && !dateToCheck.isAfter(endDate)) {
                tasksWithStatus.add(new TaskWithStatus(task, getTaskStatus(task)));
            }
        }

        // Filtracja po statusie
        if (selectedStatuses != null && !selectedStatuses.isEmpty()) {
            tasksWithStatus = tasksWithStatus.stream()
                    .filter(t -> selectedStatuses.contains(t.status()))
                    .collect(Collectors.toList());
        }

        if (tasksWithStatus.isEmpty()) {
            throw new NoDataException("Brak danych dla wybranych filtrów");
        }

        return tasksWithStatus;
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
                        new Paragraph("Raport wykonania zadań")
                                .setFontSize(20).setBold())
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));

        document.add(header);
    }

    private void addReportInfo(Document document, LocalDate startDate, LocalDate endDate,
                               PeriodType periodType, List<String> selectedCategories,
                               List<String> selectedStatuses) throws Exception {
        document.add(new Paragraph("Data wygenerowania: " + LocalDate.now()).setMarginBottom(10));
        document.add(new Paragraph("Okres raportowania: " + startDate + " – " + endDate));
        document.add(new Paragraph("Typ okresu: " + periodType));
        document.add(new Paragraph("Wybrane kategorie: " +
                (selectedCategories.isEmpty() ? "Wszystkie" : String.join(", ", selectedCategories))));

        if (selectedStatuses != null && !selectedStatuses.isEmpty()) {
            document.add(new Paragraph("Filtry statusów: " + String.join(", ", selectedStatuses)));
        }
        document.add(new Paragraph("\n"));
    }

    private Table createTaskTable() {
        return new Table(new float[]{3, 2, 2, 2, 2})
                .setWidth(UnitValue.createPercentValue(100));
    }

    private void addTaskTableData(Table table, List<TaskWithStatus> tasks) {
        addHeader(table, "Zadanie", "Kategoria", "Termin", "Przypisane do", "Status");

        for (TaskWithStatus task : tasks) {
            table.addCell(task.task().taskName());
            table.addCell(task.task().category());
            table.addCell(task.task().dueDate().toString());
            table.addCell(task.task().assignee());
            table.addCell(task.status());
        }
    }

    private void addHeader(Table t, String... labels) {
        for (String l : labels) {
            t.addHeaderCell(new Cell().add(new Paragraph(l).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        }
    }

    private Table createSummaryTable(List<TaskWithStatus> tasks) {
        Table summary = new Table(new float[]{3, 1})
                .setWidth(UnitValue.createPercentValue(100))
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);

        addHeader(summary, "Podsumowanie", "Liczba");

        long completed = tasks.stream().filter(t -> "Zakończone".equals(t.status())).count();
        long inProgress = tasks.stream().filter(t -> "W trakcie".equals(t.status())).count();
        long overdue = tasks.stream().filter(t -> "Opóźnione".equals(t.status())).count();

        summary.addCell("Zakończone").addCell(String.valueOf(completed));
        summary.addCell("W trakcie").addCell(String.valueOf(inProgress));
        summary.addCell("Opóźnione").addCell(String.valueOf(overdue));

        return summary;
    }

    /**
     * Określa status zadania na podstawie dat.
     */
    private String getTaskStatus(TaskRecord task) {
        if (task.completionDate() != null) {
            return "Zakończone";
        } else if (task.dueDate() != null && task.dueDate().isBefore(LocalDate.now())) {
            return "Opóźnione";
        } else {
            return "W trakcie";
        }
    }

    /**
     * Rekord reprezentujący dane o zadaniu z jego statusem.
     */
    private record TaskWithStatus(TaskRecord task, String status) {}

    /**
     * Rekord reprezentujący dane o zadaniu.
     */
    public record TaskRecord(String taskName, String category, LocalDate dueDate,
                             LocalDate completionDate, String assignee) {}

    /**
     * Wyjątek braku danych.
     */
    public static class NoDataException extends RuntimeException {
        public NoDataException(String message) {
            super(message);
        }
    }
}