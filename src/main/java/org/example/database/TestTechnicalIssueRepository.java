package org.example.database;

import org.example.sys.Employee;
import org.example.sys.TechnicalIssue;

import java.time.LocalDate;
import java.util.List;

/**
 * Klasa testująca działanie TechnicalIssueRepository.
 */
public class TestTechnicalIssueRepository {

    public static void main(String[] args) {
        TechnicalIssueRepository issueRepo = new TechnicalIssueRepository();
        UserRepository           userRepo  = new UserRepository();

        try {
            // --- 0. Pobranie pracownika do przypisania zgłoszenia ---
            List<Employee> pracownicy = userRepo.pobierzWszystkichPracownikow();
            if (pracownicy.isEmpty()) {
                System.out.println("Brak pracowników w bazie. Dodaj pracownika przed testem.");
                return;
            }
            Employee employee = pracownicy.get(0);

            // === 1. Dodanie nowego zgłoszenia ===
            LocalDate       ld    = LocalDate.now();
            TechnicalIssue  issue = new TechnicalIssue();
            issue.setType("Awaria terminala");
            issue.setDescription("Terminal płatniczy nie działa.");
            issue.setDateSubmitted(ld);      // LocalDate
            issue.setStatus("Nowe");
            issue.setEmployee(employee);

            issueRepo.dodajZgloszenie(issue);
            System.out.println(">>> Dodano zgłoszenie o ID " + issue.getId());

            // === 2. Wyświetlenie wszystkich zgłoszeń ===
            System.out.println("\n>>> Wszystkie zgłoszenia:");
            wypiszZgloszenia(issueRepo.pobierzWszystkieZgloszenia());

            // === 3. Odczyt zgłoszenia po ID ===
            TechnicalIssue znalezione = issueRepo.znajdzZgloszeniePoId(issue.getId());
            System.out.println(">>> Znalezione po ID: " + znalezione);

            // === 4. Aktualizacja zgłoszenia ===
            issue.setStatus("W trakcie");
            issue.setDescription("Zgłoszenie przekazane do serwisu.");
            issueRepo.aktualizujZgloszenie(issue);
            System.out.println("\n>>> Zaktualizowano zgłoszenie.");

            // === 5. Wyświetlenie po aktualizacji ===
            System.out.println("\n>>> Po aktualizacji:");
            wypiszZgloszenia(issueRepo.pobierzWszystkieZgloszenia());

            // === 6. Testy metod wyszukiwania po kryteriach ===

            // 6a) po fragmencie typu
            System.out.println("\n>>> Znajdź po typie zawierającym 'Awaria':");
            wypiszZgloszenia(issueRepo.znajdzPoTypie("Awaria"));

            // 6b) po przedziale dat [ld−1, ld+1]
            LocalDate start = ld.minusDays(1);
            LocalDate end   = ld.plusDays(1);
            System.out.println("\n>>> Znajdź po dacie zgłoszenia [" + start + " – " + end + "]:");
            wypiszZgloszenia(issueRepo.znajdzPoDacie(start, end));

            // 6c) po exact statusie
            System.out.println("\n>>> Znajdź po statusie 'W trakcie':");
            wypiszZgloszenia(issueRepo.znajdzPoStatusie("W trakcie"));

            // 6d) po pracowniku
            System.out.println("\n>>> Znajdź zgłoszenia pracownika o ID " + employee.getId() + ":");
            wypiszZgloszenia(issueRepo.znajdzPoPracowniku(employee.getId()));

            // === 7. Usunięcie zgłoszenia ===
            issueRepo.usunZgloszenie(issue);
            System.out.println("\n>>> Usunięto zgłoszenie.");

            // === 8. Lista po usunięciu ===
            System.out.println("\n>>> Po usunięciu (powinno być pusto):");
            wypiszZgloszenia(issueRepo.pobierzWszystkieZgloszenia());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            issueRepo.close();
            userRepo.close();
        }
    }

    private static void wypiszZgloszenia(List<TechnicalIssue> zgloszenia) {
        if (zgloszenia.isEmpty()) {
            System.out.println("(Brak zgłoszeń)");
        } else {
            for (TechnicalIssue z : zgloszenia) {
                System.out.printf(
                        "ID: %-3d | Typ: %-20s | Status: %-12s | Data: %s%n",
                        z.getId(),
                        z.getType(),
                        z.getStatus(),
                        z.getDateSubmitted()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
