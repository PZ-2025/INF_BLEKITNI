package org.example.pdflib;

import org.example.sys.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WarehouseRaportDemo {
    public static void main(String[] args) {
        try {
            // Dane testowe - produkty wprowadzane ręcznie
            List<Product> allProducts = new ArrayList<>();
            allProducts.add(new Product("Telewizor LED 55\"", "Elektronika", 12, 1999));
            allProducts.add(new Product("Chleb pszenny", "Spożywcze", 4, 3));
            allProducts.add(new Product("Laptop Gamingowy", "Elektronika", 5, 3499));
            allProducts.add(new Product("Mleko UHT 1L", "Spożywcze", 10, 2));
            allProducts.add(new Product("Kuchenka mikrofalowa", "Elektronika", 8, 599));
            allProducts.add(new Product("Ser żółty 200g", "Spożywcze", 3, 7));
            allProducts.add(new Product("Pralka automatyczna", "Elektronika", 7, 1499));
            allProducts.add(new Product("Jogurt owocowy", "Spożywcze", 15, 1));
            allProducts.add(new Product("Klimatyzator", "Elektronika", 4, 249));
            allProducts.add(new Product("Woda mineralna 6x1.5L", "Spożywcze", 20, 14));

            // Konfiguracja generatora
            WarehouseRaport generator = new WarehouseRaport();
            generator.setLowStockThreshold(5);  // Ustawienie progu alertu

            // Generowanie raportu dla wybranych kategorii
            generator.generateReport(
                    "output/reports/warehouse_report.pdf",
                    allProducts,
                    Arrays.asList("Elektronika", "Spożywcze")
            );

            System.out.println("Raport magazynowy został wygenerowany pomyślnie!");
        } catch (Exception e) {
            System.err.println("Wystąpił błąd podczas generowania raportu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}