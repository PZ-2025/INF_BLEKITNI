package org.example.database;

import org.example.sys.Product;
import org.example.sys.Warehouse;

import java.util.List;

/**
 * Klasa testująca działanie WarehouseRepository.
 */
public class TestWarehouseRepository {

    public static void main(String[] args) {
        WarehouseRepository warehouseRepo = new WarehouseRepository();
        ProductRepository   productRepo   = new ProductRepository();

        try {
            // === 1. Dodanie produktu (potrzebne dla klucza obcego) ===
            Product produkt = new Product("Jogurt truskawkowy", "Nabiał", 3.49);
            productRepo.dodajProdukt(produkt);
            System.out.println(">>> Dodano produkt: " + produkt.getName());

            // === 2. Dodanie stanu magazynowego ===
            Warehouse stan = new Warehouse(produkt, 120);
            warehouseRepo.dodajStanMagazynowy(stan);
            System.out.println(">>> Dodano stan magazynowy!");

            // === 3. Wyświetlenie wszystkich stanów ===
            System.out.println("\n>>> Lista wszystkich stanów:");
            wypiszStany(warehouseRepo.pobierzWszystkieStany());

            // === 4. Aktualizacja stanu (zmieniamy ilość na 100) ===
            stan.setIlosc(100);
            warehouseRepo.aktualizujStan(stan);
            System.out.println(">>> Zaktualizowano stan magazynowy (ilość → 100).");

            // === 5. Odczyt po ID produktu ===
            Warehouse znaleziony = warehouseRepo.znajdzStanPoIdProduktu(produkt.getId());
            System.out.println(">>> Stan po ID produktu: " + znaleziony);

            // === 6. Wyszukiwania po różnych kryteriach ===
            System.out.println("\n>>> Znajdź rekordy z ilością = 100:");
            wypiszStany(warehouseRepo.znajdzPoIlosci(100));

            System.out.println("\n>>> Znajdź rekordy z ilością < 110:");
            wypiszStany(warehouseRepo.znajdzPoIlosciMniejszejNiz(110));

            System.out.println("\n>>> Znajdź rekordy z ilością > 50:");
            wypiszStany(warehouseRepo.znajdzPoIlosciWiekszejNiz(50));

            System.out.println("\n>>> Znajdź rekordy z ilością między 80 a 120:");
            wypiszStany(warehouseRepo.znajdzPoIlosciWMiedzy(80, 120));

            // === 7. Usunięcie stanu ===
            warehouseRepo.usunStan(produkt.getId());
            System.out.println(">>> Usunięto stan magazynowy.");

            // === 8. Lista po usunięciu ===
            System.out.println("\n>>> Lista stanów po usunięciu:");
            wypiszStany(warehouseRepo.pobierzWszystkieStany());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            warehouseRepo.close();
            productRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca listę stanów magazynowych.
     */
    private static void wypiszStany(List<Warehouse> stany) {
        if (stany.isEmpty()) {
            System.out.println("(Brak danych o stanach magazynowych)");
        } else {
            for (Warehouse w : stany) {
                System.out.printf(
                        "ID produktu: %-3d | Nazwa: %-20s | Cena: %6.2f | Ilość: %-4d%n",
                        w.getProdukt().getId(),
                        w.getProdukt().getName(),
                        w.getProdukt().getPrice(),
                        w.getIlosc()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
