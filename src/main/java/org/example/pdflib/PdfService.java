package org.example.pdflib;

import pdf.NoDataException;
import pdf.SalesReportGenerator;
import pdf.StatsRaportGenerator;
import pdf.TaskRaportGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PdfService {

    private final String logoPath = getClass().getClassLoader()
            .getResource("logo.png")
            .getPath();

    public void runSalesReport() {
        SalesReportGenerator gen = new SalesReportGenerator();
        gen.setLogoPath(logoPath);

        // Przygotuj dane
        var salesData = List.of(
                new SalesReportGenerator.SalesRecord(1, LocalDateTime.now().minusDays(1),
                        "Mleko", "Spożywcze", 10, 25.0),
                new SalesReportGenerator.SalesRecord(2, LocalDateTime.now().minusDays(2),
                        "Chleb", "Spożywcze", 5, 12.5)
        );

        gen.setSalesData(salesData);

        try {
            gen.generateReport("reports/raport_sprzedazy.pdf",
                    SalesReportGenerator.PeriodType.DAILY,
                    List.of("Spożywcze"));
            System.out.println("Sales report wygenerowany.");
        } catch (NoDataException nde) {
            System.err.println("Brak danych: " + nde.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runStatsReport() {
        StatsRaportGenerator gen = new StatsRaportGenerator();
        gen.setLogoPath(logoPath);

        // Przygotuj dane
        var taskData = List.of(
                new StatsRaportGenerator.TaskRecord(
                        "Inwentaryzacja", "Magazyn",
                        StatsRaportGenerator.Priority.MEDIUM,
                        LocalDate.now().minusDays(1),
                        LocalDate.now(),
                        "Jan Kowalski"
                )
        );

        try {
            gen.generateReport("reports/raport_statystyk.pdf",
                    LocalDate.now(),
                    StatsRaportGenerator.PeriodType.DAILY,
                    List.of("Magazyn"),
                    List.of(StatsRaportGenerator.Priority.MEDIUM)
            );
            System.out.println("Stats report wygenerowany.");
        } catch (NoDataException nde) {
            System.err.println("Brak danych: " + nde.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runTaskReport() {
        TaskRaportGenerator gen = new TaskRaportGenerator();
        gen.setLogoPath(logoPath);

        // Przygotuj dane
        var tasks = List.of(
                new TaskRaportGenerator.TaskRecord(
                        "Sprzątanie", LocalDate.now().plusDays(2),
                        null, "Anna Nowak"
                )
        );

        try {
            gen.generateReport("reports/raport_zadan.pdf",
                    TaskRaportGenerator.PeriodType.LAST_WEEK,
                    List.of("W trakcie")
            );
            System.out.println("Task report wygenerowany.");
        } catch (NoDataException nde) {
            System.err.println("Brak danych: " + nde.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
