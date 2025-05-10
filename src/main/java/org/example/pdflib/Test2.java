package org.example.pdflib;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class Test2 {
    public static void main(String[] args) {
        // Przygotowanie danych
        List<StatsRaportGenerator.TaskRecord> tasks = Arrays.asList(
                new StatsRaportGenerator.TaskRecord(
                        "Sprzątanie", "Logistyka", "Operacyjne",
                        StatsRaportGenerator.Priority.HIGH,
                        LocalDate.now().minusDays(5), LocalDate.now().minusDays(3), "Anna Nowak"),
                new StatsRaportGenerator.TaskRecord(
                        "Audyt", "Administracja", "Kontrola jakości",
                        StatsRaportGenerator.Priority.MEDIUM,
                        LocalDate.now().minusDays(10), LocalDate.now().minusDays(2), "Jan Kowalski")
        );

// Konfiguracja generatora
        StatsRaportGenerator generator = new StatsRaportGenerator();
        generator.setTaskData(tasks);
        generator.setLogoPath("src/main/resources/logo.png");

// Generowanie raportu
        try {
            generator.generateReport(
                    "output/reports/stats_report.pdf",
                    LocalDate.now(), // reportDate
                    StatsRaportGenerator.PeriodType.MONTHLY, // periodType
                    Arrays.asList("Logistyka", "Administracja"), // selectedDepartments
                    Arrays.asList("Operacyjne", "Kontrola jakości"), // selectedCategories
                    Arrays.asList(StatsRaportGenerator.Priority.HIGH, StatsRaportGenerator.Priority.MEDIUM) // selectedPriorities
            );
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas generowania raportu:");
            e.printStackTrace();
        }
    }
}
