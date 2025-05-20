package org.example.database;

import org.example.sys.Employee;
import org.example.sys.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Classname: TestTransactionRepository
 * Version information: 1.1
 * Date: 2025-05-12
 * Copyright notice: © BŁĘKITNI
 *
 * Klasa testująca działanie TransactionRepository.
 */
public class TestTransactionRepository {

    public static void main(String[] args) {
        TransactionRepository transactionRepo = new TransactionRepository();
        UserRepository        userRepo        = new UserRepository();

        try {
            // === 0. Przygotowanie dat ===
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date exactDate     = sdf.parse("2025-05-12");
            Date fromDate      = sdf.parse("2025-05-10");
            Date toDate        = sdf.parse("2025-05-15");

            // === 1. Wybierz istniejącego pracownika ===
            Employee employee = userRepo.pobierzWszystkichPracownikow().get(0);

            // === 2. Dodanie transakcji ===
            Transaction tx1 = new Transaction();
            tx1.setData(exactDate);
            tx1.setPracownik(employee);
            transactionRepo.dodajTransakcje(tx1);
            System.out.println(">>> Dodano transakcję ID=" + tx1.getId());

            // jeszcze jedna transakcja dla testów zakresu
            Transaction tx2 = new Transaction();
            tx2.setData(sdf.parse("2025-05-13"));
            tx2.setPracownik(employee);
            transactionRepo.dodajTransakcje(tx2);
            System.out.println(">>> Dodano transakcję ID=" + tx2.getId());

            // === 3. Wyświetlenie wszystkich transakcji ===
            System.out.println("\n>>> Wszystkie transakcje:");
            wypiszTransakcje(transactionRepo.pobierzWszystkieTransakcje());

            // === 4. Odczyt po ID ===
            Transaction loaded = transactionRepo.znajdzTransakcjePoId(tx1.getId());
            System.out.println(">>> Odczyt po ID: " + loaded);

            // === 5. Wyszukiwanie po pracowniku ===
            System.out.println("\n>>> Transakcje pracownika ID=" + employee.getId() + ":");
            wypiszTransakcje(transactionRepo.znajdzPoPracowniku(employee.getId()));

            // === 6. Wyszukiwanie po dokładnej dacie ===
            System.out.println("\n>>> Transakcje z dnia " + sdf.format(exactDate) + ":");
            wypiszTransakcje(transactionRepo.znajdzPoDacie(exactDate));

            // === 7. Wyszukiwanie po zakresie dat ===
            System.out.println("\n>>> Transakcje z przedziału " +
                    sdf.format(fromDate) + " – " + sdf.format(toDate) + ":");
            wypiszTransakcje(transactionRepo.znajdzPoZakresieDat(fromDate, toDate));

            // === 8. Usunięcie jednej z transakcji ===
            transactionRepo.usunTransakcje(tx1.getId());
            System.out.println("\n>>> Usunięto transakcję ID=" + tx1.getId());

            // === 9. Lista po usunięciu ===
            System.out.println("\n>>> Lista po usunięciu:");
            wypiszTransakcje(transactionRepo.pobierzWszystkieTransakcje());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            transactionRepo.close();
            userRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca transakcje.
     */
    private static void wypiszTransakcje(List<Transaction> lista) {
        if (lista.isEmpty()) {
            System.out.println("(Brak transakcji)");
        } else {
            for (Transaction t : lista) {
                System.out.printf(
                        "ID: %-3d | Pracownik: %-20s | Data: %s%n",
                        t.getId(),
                        t.getPracownik().getName() + " " + t.getPracownik().getSurname(),
                        t.getData()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
