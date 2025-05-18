/*
 * Classname: TestTransactionRepository
 * Version information: 1.0
 * Date: 2025-05-12
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import org.example.sys.Employee;
import org.example.sys.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Klasa testująca działanie TransactionRepository.
 */
public class TestTransactionRepository {

    public static void main(String[] args) {
        TransactionRepository transactionRepo = new TransactionRepository();
        UserRepository userRepo = new UserRepository();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date data = sdf.parse("2025-05-12");

            // === 1. Wybierz istniejącego pracownika ===
            Employee employee = userRepo.pobierzWszystkichPracownikow().get(0); // zakładamy że istnieje

            // === 2. Dodanie transakcji ===
            Transaction transakcja = new Transaction();
            transakcja.setData(data);
            transakcja.setPracownik(employee);

            transactionRepo.dodajTransakcje(transakcja);
            System.out.println(">>> Dodano transakcję!");

            // === 3. Wyświetlenie wszystkich transakcji ===
            System.out.println("\n>>> Lista wszystkich transakcji:");
            wypiszTransakcje(transactionRepo.pobierzWszystkieTransakcje());

            // === 4. Odczyt po ID ===
            Transaction loaded = transactionRepo.znajdzTransakcjePoId(transakcja.getId());
            System.out.println(">>> Transakcja po ID: " + loaded);

            // === 5. Usunięcie ===
            transactionRepo.usunTransakcje(loaded.getId());
            System.out.println(">>> Usunięto transakcję.");

            // === 6. Lista po usunięciu ===
            System.out.println("\n>>> Lista transakcji po usunięciu:");
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
     *
     * @param lista lista transakcji
     */
    private static void wypiszTransakcje(List<Transaction> lista) {
        if (lista.isEmpty()) {
            System.out.println("(Brak transakcji)");
        } else {
            for (Transaction t : lista) {
                System.out.printf("ID: %-3d | Pracownik: %-20s | Data: %s%n",
                        t.getId(),
                        t.getPracownik().getName() + " " + t.getPracownik().getSurname(),
                        t.getData()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
