package org.example.database;

import org.example.sys.Employee;
import org.example.sys.Raport;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Klasa testująca działanie RaportRepository.
 */
public class TestRaportRepository {

    public static void main(String[] args) {
        RaportRepository raportRepo = new RaportRepository();
        UserRepository   userRepo   = new UserRepository();   // potrzebny pracownik

        try {
            // === 1. Pracownik testowy ===
            List<Employee> pracownicy = userRepo.pobierzWszystkichPracownikow();
            if (pracownicy.isEmpty()) {
                System.out.println("Brak pracowników w bazie – dodaj jednego przed uruchomieniem testu.");
                return;
            }
            Employee pracownik = pracownicy.get(0);

            // === 2. Daty w dwóch wersjach ===
            LocalDate ldStart1 = LocalDate.of(2025, 4, 1);
            LocalDate ldEnd1   = LocalDate.of(2025, 4, 30);
            LocalDate ldStart2 = LocalDate.of(2025, 5, 1);
            LocalDate ldEnd2   = LocalDate.of(2025, 5, 10);

            // – sql* tylko dla metod repozytorium, które przyjmują java.util.Date
            Date sqlStart1 = Date.valueOf(ldStart1);
            Date sqlEnd1   = Date.valueOf(ldEnd1);
            Date sqlStart2 = Date.valueOf(ldStart2);
            Date sqlEnd2   = Date.valueOf(ldEnd2);

            // === 3. Dodawanie raportów (używamy LocalDate) ===
            Raport r1 = new Raport(
                    "Raport sprzedaży", ldStart1, ldEnd1,
                    pracownik, "raporty/sprzedaz_0425.pdf");

            Raport r2 = new Raport(
                    "Raport pracowników", ldStart2, ldEnd2,
                    pracownik, "raporty/pracownicy_0525.pdf");

            raportRepo.dodajRaport(r1);
            raportRepo.dodajRaport(r2);
            System.out.println(">>> Dodano raporty r1 oraz r2.");

            // === 4. Lista wszystkich raportów ===
            System.out.println("\n>>> Wszystkie raporty:");
            wypiszRaporty(raportRepo.pobierzWszystkieRaporty());

            // === 5. Aktualizacja r1 ===
            r1.setTypRaportu("Raport sprzedaży — zmodyfikowany");
            r1.setSciezkaPliku("raporty/zmieniony_sprzedaz.pdf");
            raportRepo.aktualizujRaport(r1);

            // === 6. Pobranie po ID ===
            Raport r1PoId = raportRepo.znajdzRaportPoId(r1.getId());
            System.out.println("\n>>> r1 po ID: " + r1PoId.getTypRaportu()
                    + " | " + r1PoId.getSciezkaPliku());

            // === 7. Wyszukiwania ===
            System.out.println("\n>>> Typ zawiera 'sprzedaży':");
            wypiszRaporty(raportRepo.znajdzPoTypie("sprzedaży"));

            System.out.println("\n>>> Data początku 01-04-2025 … 30-04-2025:");
            wypiszRaporty(raportRepo.znajdzPoDaciePoczatku(sqlStart1, sqlEnd1));

            System.out.println("\n>>> Data końca 01-05-2025 … 10-05-2025:");
            wypiszRaporty(raportRepo.znajdzPoDacieZakonczenia(sqlStart2, sqlEnd2));

            System.out.println("\n>>> Raporty pracownika ID " + pracownik.getId() + ":");
            wypiszRaporty(raportRepo.znajdzPoPracowniku(pracownik.getId()));

            System.out.println("\n>>> Ścieżka pliku zawiera 'pracownicy':");
            wypiszRaporty(raportRepo.znajdzPoSciezcePliku("pracownicy"));

            // === 8. Usunięcie r2 ===
            raportRepo.usunRaport(r2.getId());
            System.out.println("\n>>> Usunięto r2.");

            // === 9. Lista końcowa ===
            System.out.println("\n>>> Raporty po usunięciu r2:");
            wypiszRaporty(raportRepo.pobierzWszystkieRaporty());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            raportRepo.close();
            userRepo.close();
        }
    }

    // ---------------------------------------------------------

    private static void wypiszRaporty(List<Raport> raporty) {
        if (raporty.isEmpty()) {
            System.out.println("(Brak raportów)");
        } else {
            for (Raport r : raporty) {
                System.out.printf("[%d] %s | %s → %s | plik: %s%n",
                        r.getId(),
                        r.getTypRaportu(),
                        r.getDataPoczatku(),
                        r.getDataZakonczenia(),
                        r.getSciezkaPliku());
            }
        }
        System.out.println("-----------------------------");
    }
}
