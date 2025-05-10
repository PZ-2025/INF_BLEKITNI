package org.example.pdflib;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

// Przykład testowego użycia
public class Test {
    public static void main(String[] args) {
        try {
            TaskRaportGenerator generator = new TaskRaportGenerator();

            // Przygotowanie danych testowych
            List<TaskRaportGenerator.TaskRecord> tasks = Arrays.asList(
                    new TaskRaportGenerator.TaskRecord(
                            "Sprzątanie", "Operacyjne",
                            LocalDate.now().minusDays(5), LocalDate.now().minusDays(3), "Anna Nowak"),
                    new TaskRaportGenerator.TaskRecord(
                            "Audyt", "Administracyjne",
                            LocalDate.now().minusDays(10), null, "Jan Kowalski")
            );

            generator.setTaskData(tasks);

            // Generowanie raportu
            generator.generateReport(
                    "raport.pdf",
                    TaskRaportGenerator.PeriodType.LAST_MONTH,
                    Arrays.asList("Operacyjne", "Administracyjne"),
                    Arrays.asList("Zakończone", "W trakcie")
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
