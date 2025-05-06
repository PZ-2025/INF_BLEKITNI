package org.example.pdflib;

import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        WorkloadReportGenerator generator = new WorkloadReportGenerator();

        try {
            generator.generateReport(
                    "raport_obciazenia.pdf",
                    LocalDate.of(2025, 4, 1),
                    LocalDate.of(2025, 4, 30),
                    List.of("Sprzedaż", "Magazyn"),
                    List.of("Inwentaryzacja", "Obsługa klienta")
            );
            System.out.println("PDF wygenerowany pomyślnie!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
