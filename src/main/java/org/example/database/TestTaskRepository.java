package org.example.database;

import org.example.sys.Task;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

/**
 * Klasa testująca działanie TaskRepository.
 */
public class TestTaskRepository {

    public static void main(String[] args) {
        TaskRepository taskRepo = new TaskRepository();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            // === 1. Dodawanie nowych zadań, niektóre z czasem zmiany ===
            Task zad1 = new Task("Przyjęcie dostawy", sdf.parse("2025-05-01"), "Nowe",
                    "Przyjąć dostawę mleka.", LocalTime.of(2, 30));
            Task zad2 = new Task("Sprawdzenie stanów", sdf.parse("2025-05-03"), "Nowe",
                    "Sprawdzić ilość jogurtów.", LocalTime.of(1, 0));
            Task zad3 = new Task("Aktualizacja cen", sdf.parse("2025-05-05"), "W trakcie",
                    "Aktualizacja cen nabiału.", null);

            taskRepo.dodajZadanie(zad1);
            taskRepo.dodajZadanie(zad2);
            taskRepo.dodajZadanie(zad3);
            System.out.println(">>> Dodano zadania!");

            // === 2. Lista wszystkich zadań ===
            System.out.println("\n>>> Wszystkie zadania:");
            wypisz(taskRepo.pobierzWszystkieZadania());

            // === 3. Wyszukiwanie po nazwie ===
            System.out.println("\n>>> Zadania zawierające 'Sprawdzenie':");
            wypisz(taskRepo.znajdzPoNazwie("Sprawdzenie"));

            // === 4. Wyszukiwanie po dacie ===
            Date data = sdf.parse("2025-05-03");
            System.out.println("\n>>> Zadania na dzień 2025-05-03:");
            wypisz(taskRepo.znajdzPoDacie(data));

            // === 5. Wyszukiwanie po statusie ===
            System.out.println("\n>>> Zadania o statusie 'Nowe':");
            wypisz(taskRepo.znajdzPoStatusie("Nowe"));

            // === 6. Wyszukiwanie po opisie ===
            System.out.println("\n>>> Zadania z opisem 'mleka':");
            wypisz(taskRepo.znajdzPoOpisie("mleka"));

            // === 7. Wyszukiwanie po czasie zmiany (>=1h do <=3h) ===
            System.out.println("\n>>> Zadania z czasem zmiany między 01:00 a 03:00:");
            wypisz(taskRepo.znajdzPoCzasieTrwaniaZmiany(LocalTime.of(1,0), LocalTime.of(3,0)));

            // === 8. Aktualizacja zadania ===
            zad1.setStatus("Zakończone");
            taskRepo.aktualizujZadanie(zad1);
            System.out.println("\n>>> Po aktualizacji statusu zadania 1:");
            System.out.println(taskRepo.znajdzZadaniePoId(zad1.getId()));

            // === 9. Usuwanie zadania ===
            taskRepo.usunZadanie(zad2);
            System.out.println("\n>>> Po usunięciu zadania 2, pozostałe:");
            wypisz(taskRepo.pobierzWszystkieZadania());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            taskRepo.close();
        }
    }

    private static void wypisz(List<Task> lista) {
        if (lista.isEmpty()) {
            System.out.println("(Brak zadań)");
        } else {
            for (Task t : lista) {
                System.out.println(t);
            }
        }
        System.out.println("-----------------------------");
    }
}
