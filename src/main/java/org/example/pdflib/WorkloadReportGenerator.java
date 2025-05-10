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
import java.util.ArrayList;
import java.util.List;

/**
 * Generator raportów obciążenia pracowników.
 * <p>
 * Klasa umożliwia generowanie raportów PDF przedstawiających obciążenie pracowników
 * w określonym czasie. Raport zawiera informacje o średnim tygodniowym
 * obciążeniu pracowników i klasyfikuje ich według norm tygodniowych.
 * </p>
 * <p>
 * Normy tygodniowe oraz ścieżkę do logo można konfigurować za pomocą odpowiednich setterów.
 * </p>
 *
 * @author BŁĘKITNI
 * @version 1.2.0
 */
public class WorkloadReportGenerator {
    private static final Logger logger = LogManager.getLogger(WorkloadReportGenerator.class);

    /** Minimalna norma tygodniowa w godzinach (domyślnie 40h) */
    private double minWeeklyNorm = 40;

    /** Maksymalna norma tygodniowa w godzinach (domyślnie 48h) */
    private double maxWeeklyNorm = 48;

    /** Ścieżka do pliku z logo firmy */
    private String logoPath = "src/main/resources/logo.png";

    /** Lista danych o obciążeniu pracowników */
    private List<EmployeeWorkload> workloadData;

    /**
     * Zwraca minimalną normę tygodniową.
     *
     * @return Minimalna liczba godzin w tygodniu
     */
    public double getMinWeeklyNorm() { return minWeeklyNorm; }

    /**
     * Zwraca maksymalną normę tygodniową.
     *
     * @return Maksymalna liczba godzin w tygodniu
     */
    public double getMaxWeeklyNorm() { return maxWeeklyNorm; }

    /**
     * Zwraca ścieżkę do pliku z logo.
     *
     * @return Ścieżka do pliku z logo
     */
    public String getLogoPath() { return logoPath; }

    /**
     * Ustawia minimalną normę tygodniową.
     *
     * @param value Nowa wartość minimalnej normy tygodniowej
     * @throws IllegalArgumentException Gdy wartość jest ujemna lub większa/równa maksymalnej normie
     */
    public void setMinWeeklyNorm(double value) {
        if (value < 0 || value >= maxWeeklyNorm) {
            throw new IllegalArgumentException("minWeeklyNorm musi być ≥0 i < maxWeeklyNorm");
        }
        this.minWeeklyNorm = value;
    }

    /**
     * Ustawia maksymalną normę tygodniową.
     *
     * @param value Nowa wartość maksymalnej normy tygodniowej
     * @throws IllegalArgumentException Gdy wartość jest mniejsza lub równa minimalnej normie
     */
    public void setMaxWeeklyNorm(double value) {
        if (value <= minWeeklyNorm) {
            throw new IllegalArgumentException("maxWeeklyNorm musi być > minWeeklyNorm");
        }
        this.maxWeeklyNorm = value;
    }

    /**
     * Ustawia ścieżkę do pliku z logo.
     *
     * @param path Nowa ścieżka do pliku z logo
     * @throws IllegalArgumentException Gdy ścieżka jest pusta lub null
     */
    public void setLogoPath(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("logoPath nie może być puste");
        }
        this.logoPath = path;
    }

    /**
     * Ustawia dane o obciążeniu pracowników.
     *
     * @param workloadData Lista danych o obciążeniu pracowników
     */
    public void setWorkloadData(List<EmployeeWorkload> workloadData) {
        this.workloadData = workloadData;
    }

    /**
     * Generuje raport obciążenia pracowników bez filtrowania według statusu.
     *
     * @param outputPath Ścieżka do pliku wyjściowego PDF
     * @param startDate Data początkowa okresu raportowania (zostanie dostosowana do najbliższego poniedziałku)
     * @param endDate Data końcowa okresu raportowania (zostanie dostosowana do najbliższej niedzieli)
     * @param selectedPositions Lista wybranych stanowisk do uwzględnienia w raporcie
     * @throws Exception Gdy wystąpi błąd podczas generowania raportu
     */
    public void generateReport(String outputPath,
                               LocalDate startDate,
                               LocalDate endDate,
                               List<String> selectedPositions)
            throws Exception {
        generateReport(outputPath, startDate, endDate, selectedPositions, null);
    }

    /**
     * Generuje raport obciążenia pracowników z możliwością filtrowania według statusu.
     *
     * @param outputPath Ścieżka do pliku wyjściowego PDF
     * @param startDate Data początkowa okresu raportowania (zostanie dostosowana do najbliższego poniedziałku)
     * @param endDate Data końcowa okresu raportowania (zostanie dostosowana do najbliższej niedzieli)
     * @param selectedPositions Lista wybranych stanowisk do uwzględnienia w raporcie
     * @param selectedStatuses Lista wybranych statusów do uwzględnienia w raporcie (Przeciążenie, Niedociążenie, Optymalne)
     * @throws Exception Gdy wystąpi błąd podczas generowania raportu
     */
    public void generateReport(String outputPath,
                               LocalDate startDate,
                               LocalDate endDate,
                               List<String> selectedPositions,
                               List<String> selectedStatuses)
            throws Exception {

        // Dostosowanie dat do pełnych tygodni (poniedziałek-niedziela)
        LocalDate adjustedStartDate = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate adjustedEndDate = endDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // Obliczenie liczby tygodni w okresie
        long numberOfWeeks = ChronoUnit.WEEKS.between(adjustedStartDate, adjustedEndDate.plusDays(1));
        if (numberOfWeeks < 1) numberOfWeeks = 1;

        // Sprawdzenie czy mamy dane wejściowe
        if (workloadData == null || workloadData.isEmpty()) {
            throw new NoDataException("Brak danych do wygenerowania raportu.");
        }

        // Filtrowanie danych według stanowisk
        List<EmployeeWorkload> filteredByPosition = loadWorkloadData(adjustedStartDate, adjustedEndDate, selectedPositions, workloadData);

        // Przygotowanie danych z obliczonym statusem dla każdego pracownika
        List<EmployeeWithStatus> employeesWithStatus = new ArrayList<>();
        for (EmployeeWorkload employee : filteredByPosition) {
            double weeklyAverage = employee.totalHours() / numberOfWeeks;
            String status = getWorkloadStatus(weeklyAverage);
            employeesWithStatus.add(new EmployeeWithStatus(employee, weeklyAverage, status));
        }

        // Filtrowanie według statusu, jeśli podano
        List<EmployeeWithStatus> filteredEmployees;
        if (selectedStatuses != null && !selectedStatuses.isEmpty()) {
            filteredEmployees = employeesWithStatus.stream()
                    .filter(e -> selectedStatuses.contains(e.status))
                    .toList();

            // Sprawdzenie czy po filtrowaniu mamy jakieś dane
            if (filteredEmployees.isEmpty()) {
                throw new NoDataException("Brak danych dla wybranych statusów: " + String.join(", ", selectedStatuses));
            }
        } else {
            filteredEmployees = employeesWithStatus;
        }

        // Tworzenie katalogu docelowego, jeśli nie istnieje
        File targetFile = new File(outputPath);
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists() && !parentDir.mkdirs()) {
            throw new IllegalStateException("Nie można utworzyć katalogu: " + parentDir);
        }

        // Generowanie dokumentu PDF
        try (PdfWriter writer = new PdfWriter(outputPath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4)) {

            // Ustawienie czcionki dokumentu
            PdfFont font = PdfFontFactory.createFont(
                    "src/main/java/org/example/pdflib/NotoSans-VariableFont_wdth,wght.ttf", "Cp1250");
            document.setFont(font);

            // Tworzenie nagłówka z logo i tytułem
            Table header = new Table(new float[]{1, 3}).setWidth(500);

            // Sprawdzenie czy plik logo istnieje
            File logoFile = new File(logoPath);
            if (!logoFile.exists()) {
                throw new IllegalStateException("Nie znaleziono pliku logo: " + logoPath);
            }

            // Dodanie logo do nagłówka
            ImageData logo = ImageDataFactory.create(logoFile.getAbsolutePath());
            header.addCell(new Cell().add(new Image(logo).scaleToFit(80, 80))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER));

            // Dodanie tytułu do nagłówka
            header.addCell(new Cell().add(
                            new Paragraph("Raport obciążenia pracowników")
                                    .setFontSize(20).setBold())
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));
            document.add(header);

            // Dodanie informacji o raporcie
            document.add(new Paragraph("Data wygenerowania: " + LocalDate.now()).setMarginBottom(10));
            document.add(new Paragraph("Okres raportowania: " + adjustedStartDate + " – " + adjustedEndDate));
            document.add(new Paragraph("Liczba tygodni: " + numberOfWeeks));
            document.add(new Paragraph("Norma tygodniowa: " + minWeeklyNorm + " – " + maxWeeklyNorm + " godzin"));
            document.add(new Paragraph("Wybrane stanowiska: " +
                    String.join(", ", selectedPositions)));

            // Dodanie informacji o wybranych statusach, jeśli zdefiniowane
            if (selectedStatuses != null && !selectedStatuses.isEmpty()) {
                document.add(new Paragraph("Filtry statusów: " +
                        String.join(", ", selectedStatuses)));
            }

            document.add(new Paragraph("\n"));

            // Tworzenie tabeli z danymi pracowników
            Table table = new Table(new float[]{3, 2, 2, 2, 2})
                    .setWidth(UnitValue.createPercentValue(100));
            addHeader(table,
                    "ID pracownika",
                    "Stanowisko",
                    "Godziny",
                    "Średnio/tydzień",
                    "Status");

            // Zliczanie do podsumowania
            int overloaded = 0, underloaded = 0, optimal = 0;

            // Dodawanie przefiltrowanych pracowników do tabeli
            for (EmployeeWithStatus e : filteredEmployees) {
                // Zliczanie do podsumowania
                switch (e.status) {
                    case "Przeciążenie"  -> overloaded++;
                    case "Niedociążenie" -> underloaded++;
                    default              -> optimal++;
                }

                table.addCell(e.employee.employeeName());
                table.addCell(e.employee.department());
                table.addCell(String.valueOf(e.employee.totalHours()));
                table.addCell(String.format("%.1f", e.weeklyAverage));
                table.addCell(e.status);
            }
            document.add(table).add(new Paragraph("\n"));

            // Tworzenie tabeli podsumowującej
            Table summary = new Table(new float[]{3, 1})
                    .setWidth(UnitValue.createPercentValue(100))
                    .setHorizontalAlignment(HorizontalAlignment.RIGHT);
            addHeader(summary, "Podsumowanie", "Liczba");
            summary.addCell("Przeciążeni").addCell(String.valueOf(overloaded));
            summary.addCell("Niedociążeni").addCell(String.valueOf(underloaded));
            summary.addCell("Optymalne").addCell(String.valueOf(optimal));

            document.add(summary);
        }
    }

    /**
     * Klasa pomocnicza do przechowywania pracownika wraz z jego statusem i średnim obciążeniem tygodniowym.
     */
    private record EmployeeWithStatus(EmployeeWorkload employee, double weeklyAverage, String status) {}

    /**
     * Filtruje dane o obciążeniu pracowników według wybranych stanowisk i okresu.
     *
     * @param start Data początkowa okresu
     * @param end Data końcowa okresu
     * @param positions Lista stanowisk do uwzględnienia
     * @param allWorkloadData Wszystkie dostępne dane o obciążeniu pracowników
     * @return Przefiltrowana lista danych o obciążeniu pracowników
     * @throws NoDataException Gdy brak danych po filtrowaniu
     */
    public List<EmployeeWorkload> loadWorkloadData(
            LocalDate start,
            LocalDate end,
            List<String> positions,
            List<EmployeeWorkload> allWorkloadData) throws NoDataException {

        if (allWorkloadData == null || allWorkloadData.isEmpty()) {
            throw new NoDataException("Brak danych do wygenerowania raportu.");
        }

        List<EmployeeWorkload> filteredData;
        if (positions != null && !positions.isEmpty()) {
            filteredData = allWorkloadData.stream()
                    .filter(employee -> positions.contains(employee.department()))
                    .toList();
        } else {
            filteredData = allWorkloadData;
        }

        if (filteredData.isEmpty()) {
            throw new NoDataException("Brak danych dla wybranych stanowisk.");
        }

        return filteredData;
    }

    /**
     * Dodaje wiersz nagłówkowy do tabeli.
     *
     * @param t Tabela, do której dodawany jest nagłówek
     * @param labels Etykiety komórek nagłówka
     */
    private void addHeader(Table t, String... labels) {
        for (String l : labels) {
            t.addHeaderCell(new Cell().add(new Paragraph(l).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
        }
    }

    /**
     * Określa status obciążenia pracownika na podstawie średniej tygodniowej liczby godzin.
     *
     * @param weeklyHours Średnia tygodniowa liczba godzin
     * @return Status obciążenia (Przeciążenie, Niedociążenie lub Optymalne)
     */
    private String getWorkloadStatus(double weeklyHours) {
        if (weeklyHours > maxWeeklyNorm) return "Przeciążenie";
        if (weeklyHours < minWeeklyNorm) return "Niedociążenie";
        return "Optymalne";
    }

    /**
     * Rekord reprezentujący dane o obciążeniu pracownika.
     *
     * @param employeeName Identyfikator/nazwa pracownika
     * @param department Stanowisko/dział pracownika
     * @param totalHours Całkowita liczba przepracowanych godzin
     */
    public record EmployeeWorkload(String employeeName, String department, double totalHours) {}
}