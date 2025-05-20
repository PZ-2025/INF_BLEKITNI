package org.example.database;

import org.example.sys.Order;
import org.example.sys.Employee;
import org.example.sys.Product;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Klasa testująca działanie OrderRepository.
 */
public class TestOrderRepository {

    public static void main(String[] args) {
        OrderRepository orderRepo   = new OrderRepository();
        ProductRepository productRepo = new ProductRepository();
        UserRepository userRepo     = new UserRepository();

        try {
            // === 1. Przygotowanie danych: produkt i pracownik ===
            Product produkt = new Product("Testowy produkt", "Testowa kategoria", 5.99);
            productRepo.dodajProdukt(produkt);

            Employee pracownik = userRepo.pobierzWszystkichPracownikow().get(0);

            // === 2. Dodanie zamówienia ===
            LocalDate localDate = LocalDate.of(2025, 5, 12);
            Date sqlDate        = Date.valueOf(localDate);  // java.sql.Date dla encji

            Order order = new Order();
            order.setProdukt(produkt);
            order.setPracownik(pracownik);
            order.setIlosc(10);
            order.setCena(new BigDecimal("59.90"));
            order.setData(sqlDate);  // setter przyjmuje java.util.Date

            orderRepo.dodajZamowienie(order);
            System.out.println(">>> Dodano zamówienie o ID " + order.getId());

            // === 3. Pobranie wszystkich zamówień ===
            System.out.println("\n>>> Wszystkie zamówienia:");
            wypiszZamowienia(orderRepo.pobierzWszystkieZamowienia());

            // === 4. Aktualizacja ===
            order.setIlosc(20);
            order.setCena(new BigDecimal("119.80"));
            orderRepo.aktualizujZamowienie(order);
            System.out.println("\n>>> Zaktualizowano zamówienie ID " + order.getId());

            // === 5. Pobranie po ID ===
            Order byId = orderRepo.znajdzZamowieniePoId(order.getId());
            System.out.println("\n>>> Zamówienie po ID: " + byId);

            // === 6. Wyszukiwania po różnych kryteriach ===

            // 6a. Po ID produktu
            System.out.println("\n>>> Znajdź po ID produktu:");
            wypiszZamowienia(
                    orderRepo.znajdzZamowieniaPoIdProduktu(produkt.getId())
            );

            // 6b. Po ID pracownika
            System.out.println("\n>>> Znajdź po ID pracownika:");
            wypiszZamowienia(
                    orderRepo.znajdzZamowieniaPoIdPracownika(pracownik.getId())
            );

            // 6c. Po dokładnej dacie
            System.out.println("\n>>> Znajdź po dacie " + localDate + ":");
            wypiszZamowienia(
                    orderRepo.znajdzZamowieniaPoDacie(localDate)
            );

            // 6d. W zakresie dat [localDate − 1, localDate + 1]
            LocalDate odLocal   = localDate.minusDays(1);
            LocalDate doLocal   = localDate.plusDays(1);

            System.out.println("\n>>> Znajdź w zakresie " + odLocal + " – " + doLocal + ":");
            wypiszZamowienia(
                    orderRepo.znajdzZamowieniaWZakresieDat(odLocal, doLocal)
            );

            // 6e. Po minimalnej ilości >= 20
            System.out.println("\n>>> Znajdź z minimalną ilością 20:");
            wypiszZamowienia(
                    orderRepo.znajdzZamowieniaZMinimalnaIloscia(20)
            );

            // 6f. W przedziale cenowym [100.00, 200.00]
            System.out.println("\n>>> Znajdź w przedziale cenowym 100–200:");
            wypiszZamowienia(
                    orderRepo.znajdzZamowieniaWPrzedzialeCenowym(
                            new BigDecimal("100.00"),
                            new BigDecimal("200.00")
                    )
            );

            // === 7. Usunięcie i weryfikacja ===
            orderRepo.usunZamowienie(order.getId());
            System.out.println("\n>>> Usunięto zamówienie ID " + order.getId());

            System.out.println("\n>>> Lista po usunięciu:");
            wypiszZamowienia(orderRepo.pobierzWszystkieZamowienia());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            orderRepo.close();
            productRepo.close();
            userRepo.close();
        }
    }

    private static void wypiszZamowienia(List<Order> zamowienia) {
        if (zamowienia.isEmpty()) {
            System.out.println("(Brak zamówień)");
        } else {
            for (Order z : zamowienia) {
                System.out.printf(
                        "ID: %-3d | Produkt: %-20s | Ilość: %-3d | Cena: %-7s | Data: %s%n",
                        z.getId(),
                        z.getProdukt().getName(),
                        z.getIlosc(),
                        z.getCena().toString(),
                        // z.getData() to java.util.Date lub java.sql.Date, wypisujemy .toString()
                        z.getData().toString()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
