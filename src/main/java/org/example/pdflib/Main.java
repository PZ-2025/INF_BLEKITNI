package org.example.pdflib;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String destPath = "C:/Raporty/test_report.pdf";

        WorkloadReportGenerator generator = new WorkloadReportGenerator() {
            @Override
            protected List<EmployeeWorkload> loadWorkloadData(LocalDate s, LocalDate e,
                                                              List<String> positions) {
                return List.of(
                        new EmployeeWorkload("1", "Kierownik", 18, 172),
                        new EmployeeWorkload("2",  "Kasjer",     10, 96)
                );
            }
        };

        try {
            generator.generateReport(
                    destPath,
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 3, 31),
                    List.of("Kierownik", "Kasjer")       // lista stanowisk
            );
            System.out.println("PDF zapisany: " + destPath);
        } catch (NoDataException nde) {
            System.err.println("Nie wygenerowano raportu – brak danych!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
