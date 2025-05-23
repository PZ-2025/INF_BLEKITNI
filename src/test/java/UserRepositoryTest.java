/*
 * Classname: TestUserRepository
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */

import org.example.database.AddressRepository;
import org.example.database.UserRepository;
import org.example.sys.Address;
import org.example.sys.Employee;
import org.example.wyjatki.SalaryException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Klasa testująca działanie UserRepository i AddressRepository.
 */
public class UserRepositoryTest {

    public static void main(String[] args) {
        UserRepository userRepo = new UserRepository();
        AddressRepository addressRepo = new AddressRepository();

        try {
            // === 1. Tworzenie i dodanie adresu ===
            Address address = new Address();
            address.setTown("Testowo");
            address.setHouseNumber("10A");
            address.setApartmentNumber("5");
            address.setZipCode("00-000");
            address.setCity("Testowo");

            addressRepo.addAddress(address);
            System.out.println(">>> Dodano address: " + address.getCity());

            // === 2. Dodanie nowego pracownika ===
            Employee newCashier = new Employee(
                    "Michał",
                    "Brzozowski",
                    26,
                    address,
                    "mbrzo",
                    "tajnehaslo",
                    "Kasjer",
                    new BigDecimal("3200.00")
            );

            userRepo.addEmployee(newCashier);
            System.out.println(
                    ">>> Dodano pracownika: " + newCashier.getLogin()
            );

            // === 3. Pobieranie kasjerów ===
            System.out.println("\n>>> Lista kasjerów po dodaniu:");
            writeEmployees(userRepo.getCashiers());

            // === 4. Wyszukiwanie po loginie ===
            Employee foundByLogin = userRepo.findByLogin("mbrzo");
            System.out.println(
                    "\n>>> Znaleziony po loginie: " +
                            foundByLogin.getName() + " " +
                            foundByLogin.getSurname()
            );

            // === 5. Wyszukiwanie po loginie i haśle ===
            Employee foundByLoginAndPassword = userRepo
                    .findByLoginAndPassword("mbrzo", "tajnehaslo");
            if (foundByLoginAndPassword != null) {
                System.out.println(
                        ">>> Znaleziony użytkownik (login + hasło): " +
                                foundByLoginAndPassword.getName() + " " +
                                foundByLoginAndPassword.getSurname()
                );
            } else {
                System.out.println(
                        ">>> Nie znaleziono użytkownika przy logowaniu!"
                );
            }

            // === 6. Aktualizacja pracownika ===
            foundByLogin.setSurname("Brzoza");
            foundByLogin.setSalary(new BigDecimal("3400.00"));
            userRepo.updateEmployee(foundByLogin);
            System.out.println(">>> Zaktualizowano nazwisko i pensję.");

            Employee afterUpdate = userRepo.findByLogin("mbrzo");
            System.out.println(
                    ">>> Po aktualizacji: " + afterUpdate.getName() + " " +
                            afterUpdate.getSurname() + ", zarobki: " +
                            afterUpdate.getSalary()
            );

            // === 7. Pobieranie wszystkich pracowników ===
            System.out.println("\n>>> Lista wszystkich pracowników:");
            writeEmployees(userRepo.getAllEmployess());

            // === 8. Usuwanie pracownika ===
            userRepo.removeEmployee(afterUpdate);
            System.out.println(
                    ">>> Usunięto pracownika o id = " +
                            afterUpdate.getId()
            );

            // === 9. Sprawdzenie kasjerów po usunięciu ===
            System.out.println("\n>>> Lista kasjerów po usunięciu:");
            writeEmployees(userRepo.getCashiers());

        } catch (SalaryException e) {
            System.err.println(
                    "Błąd walidacji wynagrodzenia: " + e.getMessage()
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Zamknięcie repozytoriów
            userRepo.close();
            addressRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca listę pracowników.
     *
     * @param employees lista pracowników do wypisania
     */
    private static void writeEmployees(List<Employee> employees) {
        if (employees.isEmpty()) {
            System.out.println("(Brak wyników)");
            return;
        }
        for (Employee e : employees) {
            System.out.printf(
                    "ID: %-3d Imię: %-10s Nazwisko: %-12s Login: %-8s "
                            + "Zarobki: %8.2f zł%n",
                    e.getId(),
                    e.getName(),
                    e.getSurname(),
                    e.getLogin(),
                    e.getSalary()
            );
        }
        System.out.println("-----------------------------");
    }
}
