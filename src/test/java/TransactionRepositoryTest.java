/*
 * Classname: TestTransactionRepository
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */

import org.example.database.TransactionRepository;
import org.example.database.UserRepository;
import org.example.sys.Employee;
import org.example.sys.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Klasa testująca działanie TransactionRepository.
 */
public class TransactionRepositoryTest {

    public static void main(String[] args) {
        TransactionRepository transactionRepo = new TransactionRepository();
        UserRepository userRepo = new UserRepository();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date data = sdf.parse("2025-05-12");

            // === 1. Wybierz istniejącego pracownika ===
            Employee employee = userRepo.getAllEmployess().get(0); // zakładamy że istnieje

            // === 2. Dodanie transakcji ===
            Transaction transaction = new Transaction();
            transaction.setDate(data);
            transaction.setEmployee(employee);

            transactionRepo.addTransaction(transaction);
            System.out.println(">>> Dodano transakcję!");

            // === 3. Wyświetlenie wszystkich transakcji ===
            System.out.println("\n>>> Lista wszystkich transakcji:");
            writeTransactions(transactionRepo.getAllTransactions());

            // === 4. Odczyt po ID ===
            Transaction loaded = transactionRepo.findTransactionById(transaction.getId());
            System.out.println(">>> Transakcja po ID: " + loaded);

            // === 5. Usunięcie ===
            transactionRepo.removeTransactions(loaded.getId());
            System.out.println(">>> Usunięto transakcję.");

            // === 6. Lista po usunięciu ===
            System.out.println("\n>>> Lista transakcji po usunięciu:");
            writeTransactions(transactionRepo.getAllTransactions());

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
     * @param list list transakcji
     */
    private static void writeTransactions(List<Transaction> list) {
        if (list.isEmpty()) {
            System.out.println("(Brak transakcji)");
        } else {
            for (Transaction t : list) {
                System.out.printf("ID: %-3d | Pracownik: %-20s | Data: %s%n",
                        t.getId(),
                        t.getEmployee().getName() + " " + t.getEmployee().getSurname(),
                        t.getDate()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
