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
import org.example.sys.Product;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generator raportów informacyjnych o stanie magazynu.
 * <p>
 * Klasa umożliwia generowanie raportów PDF przedstawiających stan magazynowy produktów,
 * w tym podsumowania kategorii, analizę zapasów oraz alerty o niskim stanie magazynowym.
 * </p>
 * <p>
 * Raport zawiera szczegółowe dane o produktach, statystyki ogólne oraz opcjonalne filtry
 * umożliwiające analizę konkretnych kategorii produktów.
 * </p>
 *
 * @author BŁĘKITNI
 * @version 1.0.0
 */
public class WarehouseRaport {
    private static final Logger logger = LogManager.getLogger(WarehouseRaport.class);

    /** Ścieżka do pliku z logo firmy */
    private String logoPath = "src/main/resources/logo.png";

    /** Minimalna liczba jednostek dla alertu o niskim stanie magazynowym */
    private int lowStockThreshold = 5;

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
     * Ustawia próg stanu niskiego magazynu.
     */
    public void setLowStockThreshold(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Próg stanu niskiego magazynu nie może być ujemny");
        }
        this.lowStockThreshold = threshold;
    }

    /**
     * Generuje raport magazynowy.
     */
    public void generateReport(String outputPath, List<Product> products, List<String> selectedCategories) throws Exception {
        // Sprawdzenie danych wejściowych
        if (products == null || products.isEmpty()) {
            throw new NoDataException("Brak danych do wygenerowania raportu magazynowego.");
        }

        // Filtrowanie danych
        List<Product> filteredProducts = filterProducts(products, selectedCategories);

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
            addReportInfo(document, selectedCategories);

            // Tabela z danymi produktów
            document.add(createProductTable(filteredProducts));

            // Podsumowanie kategorii
            document.add(new Paragraph("\n"));
            document.add(createCategorySummaryTable(filteredProducts));

            // Alerty o niskim stanie magazynowym
            List<Product> lowStockProducts = getLowStockProducts(filteredProducts);
            if (!lowStockProducts.isEmpty()) {
                document.add(new Paragraph("\n"));
                document.add(createLowStockAlertTable(lowStockProducts));
            }
        }
    }

    private List<Product> filterProducts(List<Product> products, List<String> selectedCategories) {
        if (selectedCategories == null || selectedCategories.isEmpty()) {
            return products;
        }

        return products.stream()
                .filter(product -> selectedCategories.contains(product.getCategory()))
                .collect(Collectors.toList());
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
                        new Paragraph("Raport magazynowy")
                                .setFontSize(20).setBold())
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE));

        document.add(header);
    }

    private void addReportInfo(Document document, List<String> selectedCategories) {
        document.add(new Paragraph("Data wygenerowania: " + LocalDate.now()).setMarginBottom(10));
        document.add(new Paragraph("Wybrane kategorie: " +
                (selectedCategories == null || selectedCategories.isEmpty() ?
                        "Wszystkie" : String.join(", ", selectedCategories))));
        document.add(new Paragraph("Próg niskiego stanu magazynowego: " + lowStockThreshold));
        document.add(new Paragraph("\n"));
    }

    private Table createProductTable(List<Product> products) {
        // Poprawna liczba kolumn (4): Nazwa, Kategoria, Ilość, Cena
        Table table = new Table(new float[]{3, 2, 1, 1})
                .setWidth(UnitValue.createPercentValue(100));

        // Poprawna liczba nagłówków (4)
        addHeader(table, "Nazwa produktu", "Kategoria", "Ilość", "Cena");

        for (Product product : products) {
            table.addCell(product.getName());
            table.addCell(product.getCategory());
            table.addCell(String.valueOf(product.getQuantity()));
            table.addCell(String.format("%.2f zł", product.getPrice()));
        }

        return table;
    }

    private Table createCategorySummaryTable(List<Product> products) {
        Map<String, CategoryStats> categoryStats = calculateCategoryStats(products);

        Table summary = new Table(new float[]{3, 1, 1, 1})
                .setWidth(UnitValue.createPercentValue(100))
                .setHorizontalAlignment(HorizontalAlignment.CENTER);

        addHeader(summary, "Kategoria", "Liczba produktów", "Suma ilości", "Wartość [zł]");

        for (Map.Entry<String, CategoryStats> entry : categoryStats.entrySet()) {
            summary.addCell(entry.getKey());
            summary.addCell(String.valueOf(entry.getValue().productCount()));
            summary.addCell(String.valueOf(entry.getValue().totalQuantity()));
            summary.addCell(String.format("%.2f", entry.getValue().totalValue()));
        }

        return summary;
    }

    private Map<String, CategoryStats> calculateCategoryStats(List<Product> products) {
        Map<String, CategoryStats> categoryStats = new HashMap<>();

        for (Product product : products) {
            String category = product.getCategory();
            double value = product.getPrice() * product.getQuantity();

            categoryStats.merge(category,
                    new CategoryStats(1, product.getQuantity(), value),
                    (oldStats, newStats) -> new CategoryStats(
                            oldStats.productCount() + 1,
                            oldStats.totalQuantity() + newStats.totalQuantity(),
                            oldStats.totalValue() + newStats.totalValue()
                    ));
        }

        return categoryStats;
    }

    private List<Product> getLowStockProducts(List<Product> products) {
        return products.stream()
                .filter(p -> p.getQuantity() <= lowStockThreshold)
                .collect(Collectors.toList());
    }

    private Table createLowStockAlertTable(List<Product> lowStockProducts) {
        // Poprawna liczba kolumn (3): Nazwa, Kategoria, Ilość
        Table alertTable = new Table(new float[]{3, 2, 1})
                .setWidth(UnitValue.createPercentValue(100));

        // Poprawna liczba nagłówków (3)
        addHeader(alertTable, "Nazwa produktu", "Kategoria", "Ilość");
        alertTable.setBackgroundColor(ColorConstants.LIGHT_GRAY);

        for (Product product : lowStockProducts) {
            alertTable.addCell(product.getName());
            alertTable.addCell(product.getCategory());
            alertTable.addCell(String.valueOf(product.getQuantity()));
        }

        return alertTable;
    }

    private void addHeader(Table t, String... labels) {
        for (String l : labels) {
            t.addHeaderCell(new Cell()
                    .add(new Paragraph(l).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }
    }

    /**
     * Rekord przechowujący statystyki dla kategorii produktów.
     */
    private record CategoryStats(int productCount, int totalQuantity, double totalValue) {}

    /**
     * Wyjątek braku danych.
     */
    public static class NoDataException extends RuntimeException {
        public NoDataException(String message) {
            super(message);
        }
    }
}