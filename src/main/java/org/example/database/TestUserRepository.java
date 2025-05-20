package org.example.database;

import org.example.sys.Address;
import org.example.sys.Employee;
import org.example.wyjatki.SalaryException;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Classname: TestUserRepository
 * Version information: 1.0
 * Date: 2025-04-27
 * Copyright notice: © BŁĘKITNI
 *
 * Klasa testująca działanie UserRepository i AddressRepository.
 */
public class TestUserRepository {

    public static void main(String[] args) {
        UserRepository    userRepo    = new UserRepository();
        AddressRepository addressRepo = new AddressRepository();

        try {
            // === 1. Tworzenie i dodanie adresu ===
            Address adres = new Address();
            adres.setMiejscowosc("Testowo");
            adres.setMiasto("Miastko");
            adres.setKodPocztowy("00-000");
            adres.setNumerDomu("10A");
            adres.setNumerMieszkania("5");
            addressRepo.dodajAdres(adres);
            System.out.println(">>> Dodano adres: " + adres.getMiasto());

            // === 2. Dodanie nowego pracownika ===
            Employee nowy = new Employee(
                    "Michał",
                    "Brzozowski",
                    26,
                    adres,
                    "mbrzo",
                    "tajnehaslo",
                    "Kasjer",
                    new BigDecimal("3200.00")
            );
            userRepo.dodajPracownika(nowy);
            System.out.println(">>> Dodano pracownika: " + nowy.getLogin());

            // === 3. Pobieranie kasjerów ===
            System.out.println("\n>>> Kasjerzy:");
            wypiszPracownikow(userRepo.pobierzKasjerow());

            // === 4. Wyszukiwanie po loginie ===
            Employee byLogin = userRepo.znajdzPoLoginie("mbrzo");
            System.out.println("\n>>> Znaleziony po loginie: " + byLogin.getName() + " " + byLogin.getSurname());

            // === 5. Wyszukiwanie po loginie i haśle ===
            Employee byLoginPass = userRepo.znajdzPoLoginieIHasle("mbrzo", "tajnehaslo");
            System.out.println(">>> Logowanie: " + (byLoginPass != null ? "OK" : "FAIL"));

            // === 6. getCurrentEmployee() ===
            System.out.println(">>> Current logged in: " + userRepo.getCurrentEmployee().getLogin());

            // === 7. Wyszukiwanie po ID ===
            System.out.println("\n>>> Znajdź po ID:");
            wypiszPracownikow(List.of(userRepo.znajdzPoId(nowy.getId())));

            // === 8. Imię / nazwisko / wiek ===
            System.out.println("\n>>> Po fragmencie imienia 'chał':");
            wypiszPracownikow(userRepo.znajdzPoImieniu("chał"));

            System.out.println("\n>>> Po fragmencie nazwiska 'Brzo':");
            wypiszPracownikow(userRepo.znajdzPoNazwisku("Brzo"));

            System.out.println("\n>>> Wiek 20–30:");
            wypiszPracownikow(userRepo.znajdzPoWieku(20, 30));

            // === 9. Adres, e-mail, zarobki, stanowisko ===
            System.out.println("\n>>> Po adresie ID:");
            wypiszPracownikow(userRepo.znajdzPoAdresie(adres.getId()));

            // najpierw ustawiamy e-mail, żeby było co szukać
            nowy.setEmail("mbrzo@example.com");
            userRepo.aktualizujPracownika(nowy);
            System.out.println("\n>>> Po fragmencie e-mail 'mbrzo@':");
            wypiszPracownikow(userRepo.znajdzPoEmailu("mbrzo@"));

            System.out.println("\n>>> Zarobki 3000–3500:");
            wypiszPracownikow(userRepo.znajdzPoZarobkach(3000, 3500));

            System.out.println("\n>>> Stanowisko 'Kasjer':");
            wypiszPracownikow(userRepo.znajdzPoStanowisku("Kasjer"));

            // === 10. SickLeave ===
            nowy.startSickLeave(Date.valueOf(LocalDate.now().minusDays(1)));
            userRepo.aktualizujPracownika(nowy);
            System.out.println("\n>>> Na zwolnieniu lekarskim:");
            wypiszPracownikow(userRepo.pobierzNaSickLeave());
            System.out.println("\n>>> Nie na zwolnieniu:");
            wypiszPracownikow(userRepo.pobierzNieNaSickLeave());

            // === 11. Usuwanie pracownika ===
            userRepo.usunPracownika(nowy);
            System.out.println("\n>>> Usunięto pracownika ID=" + nowy.getId());

            System.out.println("\n>>> Lista po usunięciu:");
            wypiszPracownikow(userRepo.pobierzWszystkichPracownikow());

        } catch (SalaryException se) {
            System.err.println("Błąd walidacji wynagrodzenia: " + se.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userRepo.close();
            addressRepo.close();
        }
    }

    private static void wypiszPracownikow(List<Employee> lista) {
        if (lista.isEmpty()) {
            System.out.println("(Brak wyników)");
            return;
        }
        for (Employee e : lista) {
            System.out.printf(
                    "ID:%-3d Imię:%-10s Nazwisko:%-12s Login:%-8s Zarobki:%8.2f zł%n",
                    e.getId(), e.getName(), e.getSurname(), e.getLogin(), e.getZarobki()
            );
        }
        System.out.println("-----------------------------");
    }
}
