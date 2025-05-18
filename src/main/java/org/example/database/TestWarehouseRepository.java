/*
 * Classname: TestWarehouseRepository
 * Version information: 1.0
 * Date: 2025-05-12
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import org.example.sys.Warehouse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Klasa testująca działanie WarehouseRepository.
 */
public class TestWarehouseRepository {

    public static void main(String[] args) {
        WarehouseRepository repo = new WarehouseRepository();

        try {
            // === 1. Dodanie nowego produktu ===
            Warehouse produkt = new Warehouse("Jogurt truskawkowy", new BigDecimal("3.49"), 120);
            repo.dodajProdukt(produkt);
            System.out.println(">>> Dodano produkt!");

            // === 2. Lista wszystkich produktów ===
            System.out.println("\n>>> Lista produktów:");
            wypiszProdukty(repo.pobierzWszystkieProdukty());

            // === 3. Aktualizacja produktu ===
            produkt.setCena(new BigDecimal("3.99"));
            produkt.setIloscWmagazynie(100);
            repo.aktualizujProdukt(produkt);
            System.out.println(">>> Zaktualizowano produkt.");

            // === 4. Pobranie po ID ===
            Warehouse znaleziony = repo.znajdzProduktPoId(produkt.getId());
            System.out.println(">>> Produkt po ID: " + znaleziony);

            // === 5. Usunięcie produktu ===
            repo.usunProdukt(znaleziony.getId());
            System.out.println(">>> Usunięto produkt.");

            // === 6. Lista po usunięciu ===
            System.out.println("\n>>> Lista produktów po usunięciu:");
            wypiszProdukty(repo.pobierzWszystkieProdukty());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            repo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca produkty.
     *
     * @param produkty lista produktów
     */
    private static void wypiszProdukty(List<Warehouse> produkty) {
        if (produkty.isEmpty()) {
            System.out.println("(Brak produktów)");
        } else {
            for (Warehouse p : produkty) {
                System.out.printf("ID: %-3d Nazwa: %-20s Cena: %-6.2f Ilość: %-4d%n",
                        p.getId(),
                        p.getNazwa(),
                        p.getCena(),
                        p.getIloscWmagazynie()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
