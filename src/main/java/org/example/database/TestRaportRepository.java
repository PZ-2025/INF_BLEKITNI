/*
 * Classname: TestRaportRepository
 * Version information: 1.0
 * Date: 2025-05-15
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import org.example.sys.Employee;
import org.example.sys.Report;

import java.time.LocalDate;
import java.util.List;

/**
 * Klasa testująca działanie RaportRepository.
 */
public class TestRaportRepository {

    public static void main(String[] args) {
        ReportRepository raportRepo = new ReportRepository();
        UserRepository userRepo = new UserRepository(); // aby pobrać pracownika

        try {
            // === 1. Przykładowy pracownik do raportu ===
            List<Employee> pracownicy = userRepo.pobierzWszystkichPracownikow();
            if (pracownicy.isEmpty()) {
                System.out.println("Brak pracowników w bazie. Dodaj przynajmniej jednego przed testami.");
                return;
            }
            Employee pracownik = pracownicy.get(0);

            // === 2. Dodawanie raportów ===
            Report r1 = new Report(
                    "Raport sprzedaży",
                    LocalDate.of(2025, 4, 1),
                    LocalDate.of(2025, 4, 30),
                    pracownik,
                    "raporty/sprzedaz_0425.pdf"
            );

            Report r2 = new Report(
                    "Raport pracowników",
                    LocalDate.of(2025, 5, 1),
                    LocalDate.of(2025, 5, 10),
                    pracownik,
                    "raporty/pracownicy_0525.pdf"
            );

            raportRepo.dodajRaport(r1);
            raportRepo.dodajRaport(r2);
            System.out.println(">>> Dodano raporty.");

            // === 3. Pobieranie wszystkich raportów ===
            System.out.println("\n>>> Lista wszystkich raportów:");
            wypiszRaporty(raportRepo.pobierzWszystkieRaporty());

            // === 4. Aktualizacja ===
            r1.setTypRaportu("Raport sprzedaży — zmodyfikowany");
            r1.setSciezkaPliku("raporty/zmieniony_sprzedaz.pdf");
            raportRepo.aktualizujRaport(r1);
            System.out.println(">>> Zaktualizowano raport r1.");

            // === 5. Pobranie po ID ===
            Report znaleziony = raportRepo.znajdzRaportPoId(r1.getId());
            System.out.println(">>> Raport po ID: " + znaleziony.getTypRaportu() + " | " + znaleziony.getSciezkaPliku());

            // === 6. Usunięcie ===
            raportRepo.usunRaport(r2.getId());
            System.out.println(">>> Usunięto raport r2.");

            // === 7. Lista po usunięciu ===
            System.out.println("\n>>> Raporty po usunięciu:");
            wypiszRaporty(raportRepo.pobierzWszystkieRaporty());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            raportRepo.close();
            userRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca raporty.
     *
     * @param raporty lista raportów
     */
    private static void wypiszRaporty(List<Report> raporty) {
        if (raporty.isEmpty()) {
            System.out.println("(Brak raportów)");
        } else {
            for (Report r : raporty) {
                System.out.println("[" + r.getId() + "] "
                        + r.getTypRaportu()
                        + " | " + r.getDataPoczatku()
                        + " → " + r.getDataZakonczenia()
                        + " | plik: " + r.getSciezkaPliku());
            }
        }
        System.out.println("-----------------------------");
    }
}
