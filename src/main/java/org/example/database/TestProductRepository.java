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
        ProductRepository repo = new ProductRepository();

        try {
            // 1. Dodanie produktów
            Product p1 = new Product("Masło",    "Nabiał",      6.99);
            Product p2 = new Product("Szampon",  "Kosmetyki",  12.49);
            Product p3 = new Product("Mleko",    "Nabiał",      4.50);
            repo.dodajProdukt(p1);
            repo.dodajProdukt(p2);
            repo.dodajProdukt(p3);
            System.out.println(">>> Dodano 3 produkty.");

            // 2. Wyświetlenie wszystkich
            System.out.println("\n>>> Wszystkie produkty:");
            wypiszProdukty(repo.pobierzWszystkieProdukty());

            // 3. Odczyt po ID
            Product found = repo.znajdzProduktPoId(p1.getId());
            System.out.println("\n>>> Znaleziono po ID: " + found);

            // 4. Filtrowanie po kategorii (dokładne)
            System.out.println("\n>>> Kategoria 'Nabiał':");
            wypiszProdukty(repo.pobierzProduktyPoKategorii("Nabiał"));

            // 5. Aktualizacja obiektu
            found.setCategory("Produkty spożywcze");
            repo.aktualizujProdukt(found);
            System.out.println(">>> Zmieniono kategorię Masło na 'Produkty spożywcze'.");

            // 6. Zmiana ceny
            repo.aktualizujCeneProduktu(p2.getId(), 10.99);
            System.out.println(">>> Zmieniono cenę Szampon na 10.99.");

            // 7. Zakres cenowy 5.00–11.00
            System.out.println("\n>>> Zakres cen 5.00–11.00:");
            wypiszProdukty(repo.pobierzProduktyWZakresieCenowym(5.00, 11.00));

            // 8. Usunięcie po kategorii
            int delCount = repo.usunProduktyZKategorii("Produkty spożywcze");
            System.out.println(">>> Usunięto z kategorii 'Produkty spożywcze': " + delCount);

            // 9. Usunięcie pojedyncze
            repo.usunProdukt(p2.getId());
            System.out.println(">>> Usunięto produkt Szampon.");

            // 10. Lista po usunięciach
            System.out.println("\n>>> Po usunięciach:");
            wypiszProdukty(repo.pobierzWszystkieProdukty());

            // === Dodatkowe wyszukiwania ===

            // 11. Po fragmencie nazwy ("M")
            System.out.println("\n>>> Nazwa zawiera 'M':");
            wypiszProdukty(repo.znajdzPoNazwie("M"));

            // 12. Dokładna cena 4.50
            System.out.println("\n>>> Cena dokładnie 4.50:");
            wypiszProdukty(repo.znajdzPoCenieDokladnej(4.50));

            // 13. Cena >= 6.00
            System.out.println("\n>>> Cena >= 6.00:");
            wypiszProdukty(repo.znajdzPoCenieMin(6.00));

            // 14. Cena <= 11.00
            System.out.println("\n>>> Cena <= 11.00:");
            wypiszProdukty(repo.znajdzPoCenieMax(11.00));

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            repo.close();
        }
    }

    /** Pomocnicze wypisywanie. */
    private static void wypiszProdukty(List<Product> list) {
        if (list.isEmpty()) {
            System.out.println("(Brak produktów)");
        } else {
            for (Product p : list) {
                System.out.printf(
                        "ID: %-3d | Nazwa: %-20s | Kategoria: %-15s | Cena: %.2f zł%n",
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
