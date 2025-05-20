/*
 * Classname: TestAbsenceRequestRepository
 * Version information: 1.1
 * Date: 2025-05-18
 * Copyright notice: © BŁĘKITNI
 */
package org.example.database;

import org.example.sys.AbsenceRequest;
import org.example.sys.Employee;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Klasa testująca działanie AbsenceRequestRepository.
 */
public class TestAbsenceRequestRepository {
    public static void main(String[] args) {
        AbsenceRequestRepository absenceRepo = new AbsenceRequestRepository();
        UserRepository userRepo = new UserRepository();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start      = sdf.parse("2025-07-01");
            Date end        = sdf.parse("2025-07-10");
            Date laterStart = sdf.parse("2025-07-15");
            Date laterEnd   = sdf.parse("2025-07-25");

            // === 1. Pobranie dwóch istniejących pracowników ===
            List<Employee> allEmps = userRepo.pobierzWszystkichPracownikow();
            if (allEmps.size() < 2) {
                System.err.println("Potrzebujesz co najmniej dwóch pracowników w bazie do tego testu.");
                return;
            }
            Employee emp1 = allEmps.get(0);
            Employee emp2 = allEmps.get(1);

            // === 2. Dodanie dwóch wniosków ===
            AbsenceRequest r1 = new AbsenceRequest();
            r1.setTypWniosku("Urlop wypoczynkowy");
            r1.setDataRozpoczecia(start);
            r1.setDataZakonczenia(end);
            r1.setOpis("Testowy urlop");
            r1.setPracownik(emp1);
            r1.setStatus(AbsenceRequest.StatusWniosku.OCZEKUJE);
            absenceRepo.dodajWniosek(r1);

            AbsenceRequest r2 = new AbsenceRequest();
            r2.setTypWniosku("Urlop okolicznościowy");
            r2.setDataRozpoczecia(laterStart);
            r2.setDataZakonczenia(laterEnd);
            r2.setOpis("Wesele kolegi");
            r2.setPracownik(emp2);
            r2.setStatus(AbsenceRequest.StatusWniosku.PRZYJETY);
            absenceRepo.dodajWniosek(r2);

            System.out.println(">>> Dodano dwa wnioski.");

            // === 3. Wyświetlenie wszystkich wniosków ===
            System.out.println("\n>>> Wszystkie wnioski:");
            wypiszWnioski(absenceRepo.pobierzWszystkieWnioski());

            // === 4. Aktualizacja pierwszego wniosku ===
            r1.setOpis("Zaktualizowany opis");
            r1.setStatus(AbsenceRequest.StatusWniosku.PRZYJETY);
            absenceRepo.aktualizujWniosek(r1);
            System.out.println(">>> Zaktualizowano pierwszy wniosek.");

            // === 5. Pobranie po ID ===
            AbsenceRequest loaded = absenceRepo.znajdzWniosekPoId(r1.getId());
            System.out.printf(">>> Wczytano wniosek ID=%d, status=%s, opis=%s%n",
                    loaded.getId(), loaded.getStatus(), loaded.getOpis());

            // === 6. Test metod wyszukiwania ===
            System.out.println("\n>>> Wnioski pracownika " + emp1.getName() + ":");
            wypiszWnioski(absenceRepo.znajdzWnioskiPracownika(emp1));

            System.out.println("\n>>> Wnioski pracownika o ID " + emp2.getId() + ":");
            wypiszWnioski(absenceRepo.znajdzWnioskiPracownikaPoId(emp2.getId()));

            System.out.println("\n>>> Wnioski typu 'Urlop wypoczynkowy':");
            wypiszWnioski(absenceRepo.znajdzWnioskiPoTypie("Urlop wypoczynkowy"));

            System.out.println("\n>>> Wnioski o statusie PRZYJETY:");
            wypiszWnioski(absenceRepo.znajdzWnioskiPoStatusie(AbsenceRequest.StatusWniosku.PRZYJETY));

            System.out.println("\n>>> Wnioski od daty " + start + ":");
            wypiszWnioski(absenceRepo.znajdzWnioskiOdDaty(start));

            System.out.println("\n>>> Wnioski do daty " + laterEnd + ":");
            wypiszWnioski(absenceRepo.znajdzWnioskiDoDaty(laterEnd));

            System.out.println("\n>>> Wnioski w zakresie dat:");
            wypiszWnioski(absenceRepo.znajdzWnioskiWZakresieDat(start, laterEnd));

            System.out.println("\n>>> Wnioski nachodzące na zakres dat:");
            wypiszWnioski(absenceRepo.znajdzWnioskiNachodzaceNaZakresDat(start, laterEnd));

            // === 7. Usunięcie obu wniosków ===
            absenceRepo.usunWniosek(r1.getId());
            absenceRepo.usunWniosek(r2.getId());
            System.out.println(">>> Usunięto oba wnioski.");

            // === 8. Lista po usunięciu ===
            System.out.println("\n>>> Wszystkie wnioski po usunięciu:");
            wypiszWnioski(absenceRepo.pobierzWszystkieWnioski());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            absenceRepo.close();
            userRepo.close();
        }
    }

    private static void wypiszWnioski(List<AbsenceRequest> lista) {
        if (lista.isEmpty()) {
            System.out.println("(Brak wniosków)");
        } else {
            for (AbsenceRequest w : lista) {
                System.out.printf(
                        "ID: %-3d | Typ: %-25s | Pracownik: %-20s | Od: %s | Do: %s | Status: %s | Opis: %s%n",
                        w.getId(),
                        w.getTypWniosku(),
                        w.getPracownik().getName() + " " + w.getPracownik().getSurname(),
                        w.getDataRozpoczecia(),
                        w.getDataZakonczenia(),
                        w.getStatus(),
                        w.getOpis()
                );
            }
        }
        System.out.println("-----------------------------");
    }
}
