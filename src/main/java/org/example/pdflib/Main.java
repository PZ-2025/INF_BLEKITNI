package org.example.pdflib;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String destPath = "C:/Raporty/test_report.pdf";

        // Przygotowanie listy pracowników do testu
        List<WorkloadReportGenerator.EmployeeWorkload> testData = new ArrayList<>();
        testData.add(new WorkloadReportGenerator.EmployeeWorkload("1", "Kierownik", 60));
        testData.add(new WorkloadReportGenerator.EmployeeWorkload("2", "Kasjer", 96));
        testData.add(new WorkloadReportGenerator.EmployeeWorkload("3", "Kasjer", 120));
        testData.add(new WorkloadReportGenerator.EmployeeWorkload("4", "Sprzedawca", 150));
        testData.add(new WorkloadReportGenerator.EmployeeWorkload("5", "Kierownik", 80));
        testData.add(new WorkloadReportGenerator.EmployeeWorkload("6", "Magazynier", 140));
        testData.add(new WorkloadReportGenerator.EmployeeWorkload("7", "Sprzedawca", 40));
        testData.add(new WorkloadReportGenerator.EmployeeWorkload("8", "Magazynier", 165));

        // Utworzenie i konfiguracja generatora raportów
        WorkloadReportGenerator generator = new WorkloadReportGenerator();
        generator.setWorkloadData(testData);

        try {
            // Ustawienie niestandardowych norm (opcjonalnie)
            generator.setMinWeeklyNorm(35);
            generator.setMaxWeeklyNorm(45);
            
            generator.generateReport(
                    destPath,
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 1, 31),
                    List.of("Kierownik", "Kasjer", "Sprzedawca", "Magazynier"),
                    List.of("Niedociążenie")
            );

            System.out.println("Raport PDF został pomyślnie wygenerowany: " + destPath);
            System.out.println("Liczba pracowników w raporcie: " + testData.size());
            System.out.println("Normy tygodniowe: " + generator.getMinWeeklyNorm() + " - " +
                    generator.getMaxWeeklyNorm() + " godzin");

        } catch (NoDataException nde) {
            System.err.println("Błąd: Brak danych do wygenerowania raportu - " + nde.getMessage());
        } catch (Exception ex) {
            System.err.println("Wystąpił błąd podczas generowania raportu:");
            ex.printStackTrace();
        }
    }
}