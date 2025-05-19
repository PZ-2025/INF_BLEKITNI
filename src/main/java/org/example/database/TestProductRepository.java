/*
 * Classname: TestProductRepository
 * Version information: 1.1
 * Date: 2025-05-12
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import org.example.sys.Product;

import java.util.List;

/**
 * Klasa testująca działanie ProductRepository.
 */
public class TestProductRepository {

    public static void main(String[] args) {
        ProductRepository productRepo = new ProductRepository();

        try {
            // === 1. Dodanie produktu ===
            Product produkt1 = new Product("Masło", "Nabiał", 6.99);
            productRepo.dodajProdukt(produkt1);
            System.out.println(">>> Dodano produkt: Masło");

            Product produkt2 = new Product("Szampon", "Kosmetyki", 12.49);
            productRepo.dodajProdukt(produkt2);
            System.out.println(">>> Dodano produkt: Szampon");

            // === 2. Pobranie wszystkich produktów ===
            System.out.println("\n>>> Wszystkie produkty:");
            wypiszProdukty(productRepo.pobierzWszystkieProdukty());

            // === 3. Pobranie produktu po ID ===
            Product znaleziony = productRepo.znajdzProduktPoId(produkt1.getId());
            System.out.println(">>> Znaleziony po ID: " + znaleziony);

            // === 4. Pobranie po kategorii ===
            System.out.println("\n>>> Produkty w kategorii 'Nabiał':");
            wypiszProdukty(productRepo.pobierzProduktyPoKategorii("Nabiał"));

            // === 5. Aktualizacja obiektu produkt ===
            znaleziony.setCategory("Produkty spożywcze");
            productRepo.aktualizujProdukt(znaleziony);
            System.out.println(">>> Zmieniono kategorię produktu.");

            // === 6. Aktualizacja ceny ===
            productRepo.aktualizujCeneProduktu(produkt2.getId(), 10.99);
            System.out.println(">>> Zmieniono cenę produktu Szampon.");

            // === 7. Produkty w zakresie cenowym ===
            System.out.println("\n>>> Produkty w zakresie cenowym 5.00 - 11.00:");
            wypiszProdukty(productRepo.pobierzProduktyWZakresieCenowym(5.00, 11.00));

            // === 8. Usunięcie produktów po kategorii ===
            int usuniete = productRepo.usunProduktyZKategorii("Produkty spożywcze");
            System.out.println(">>> Usunięto produktów z kategorii 'Produkty spożywcze': " + usuniete);

            // === 9. Usunięcie konkretnego produktu ===
            productRepo.usunProdukt(produkt2.getId());
            System.out.println(">>> Usunięto produkt: Szampon");

            // === 10. Lista końcowa ===
            System.out.println("\n>>> Lista produktów po usunięciach:");
            wypiszProdukty(productRepo.pobierzWszystkieProdukty());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            productRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca produkty.
     *
     * @param produkty lista produktów
     */
    private static void wypiszProdukty(List<Product> produkty) {
        if (produkty.isEmpty()) {
            System.out.println("(Brak produktów)");
        } else {
            for (Product p : produkty) {
                System.out.printf("ID: %-3d | Nazwa: %-20s | Kategoria: %-15s | Cena: %.2f zł%n",
                        p.getId(),
                        p.getName(),
                        p.getCategory(),
                        p.getPrice()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
