package org.example.pdflib;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesReportGenerator {

    private String logoPath;
    private List<SalesRecord> salesData;

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public void setSalesData(List<SalesRecord> salesData) {
        this.salesData = salesData;
    }

    /**
     * Generuje raport sprzedaży i zwraca plik PDF.
     *
     * @param outputPath ścieżka (z nazwą) do pliku PDF
     * @param period     okres raportu
     * @param categories lista kategorii
     * @return File wygenerowanego PDF-a
     */
    public File generateReport(String outputPath,
                               PeriodType period,
                               List<String> categories) throws Exception {
        if (salesData == null || salesData.isEmpty()) {
            throw new NoDataException("Brak danych sprzedaży do wygenerowania raportu.");
        }

        Map<String, String> filters = new HashMap<>();
        filters.put("Okres", period.name());
        filters.put("Kategorie", String.join(", ", categories));

        // Wywołanie pięcioargumentowe – zwróci File
        return ReportGenerator.generate(
                outputPath,
                "Raport sprzedaży",
                filters,
                logoPath,
                salesData
        );
    }

    public static class SalesRecord {
        private int id;
        private LocalDateTime date;
        private String product;
        private String category;
        private int quantity;
        private double price;

        public SalesRecord(int id,
                           LocalDateTime date,
                           String product,
                           String category,
                           int quantity,
                           double price) {
            this.id = id;
            this.date = date;
            this.product = product;
            this.category = category;
            this.quantity = quantity;
            this.price = price;
        }
        // gettery i settery…
    }

    public enum PeriodType {
        DAILY, WEEKLY, MONTHLY
    }
}
