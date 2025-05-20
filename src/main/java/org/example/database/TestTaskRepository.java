/*
 * Classname: TestTaskRepository
 * Version information: 1.0
 * Date: 2025-04-27
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import org.example.sys.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Klasa testująca działanie TaskRepository.
 */
public class TestTaskRepository {

    public static void main(String[] args) {
        TaskRepository taskRepo = new TaskRepository();

        try {
            // === 1. Dodawanie nowych zadań ===
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Task zadanie1 = new Task(
                    "Przyjęcie dostawy",
                    sdf.parse("2025-05-01"),
                    "Nowe",
                    "Przyjąć dostawę mleka.",
                    "Duży"
            );

            Task zadanie2 = new Task(
                    "Sprawdzenie stanów",
                    sdf.parse("2025-05-03"),
                    "Nowe",
                    "Sprawdzić ilość jogurtów.",
                    "Średni"
            );

            Task zadanie3 = new Task(
                    "Aktualizacja cen",
                    sdf.parse("2025-05-05"),
                    "W trakcie",
                    "Aktualizacja cen nabiału.",
                    "Duży"
            );

            taskRepo.dodajZadanie(zadanie1);
            taskRepo.dodajZadanie(zadanie2);
            taskRepo.dodajZadanie(zadanie3);

            System.out.println(">>> Dodano zadania!");

            // === 2. Pobieranie wszystkich zadań ===
            System.out.println("\n>>> Lista wszystkich zadań:");
            wypiszZadania(taskRepo.pobierzWszystkieZadania());

            // === 3. Aktualizacja istniejącego zadania ===
            zadanie1.setStatus("W trakcie");
            zadanie1.setOpis("Dostawa mleka zrealizowana w połowie.");
            taskRepo.aktualizujZadanie(zadanie1);
            System.out.println("\n>>> Zaktualizowano zadanie 1.");

            // === 4. Pobieranie zadania po ID ===
            Task znalezione = taskRepo.znajdzZadaniePoId(zadanie1.getId());
            System.out.println(">>> Zadanie po ID: " + znalezione);

            // === 5. Usuwanie zadania ===
            taskRepo.usunZadanie(zadanie2);
            System.out.println("\n>>> Usunięto zadanie 2.");

            // === 6. Lista zadań po usunięciu ===
            System.out.println("\n>>> Lista zadań po usunięciu:");
            wypiszZadania(taskRepo.pobierzWszystkieZadania());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            taskRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca zadania.
     *
     * @param zadania lista zadań do wypisania
     */
    private static void wypiszZadania(List<Task> zadania) {
        if (zadania.isEmpty()) {
            System.out.println("(Brak zadań)");
        } else {
            for (Task z : zadania) {
                System.out.println(z);
            }
        }
        System.out.println("-----------------------------");
    }
}
