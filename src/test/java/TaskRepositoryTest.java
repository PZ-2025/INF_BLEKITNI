/*
 * Classname: TestTaskRepository
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */

import org.example.database.TaskRepository;
import org.example.sys.Task;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.List;

/**
 * Klasa testująca działanie TaskRepository.
 */
public class TaskRepositoryTest {

    public static void main(String[] args) throws Exception {
        TaskRepository taskRepo = new TaskRepository();

        try {
            // === 1. Dodawanie nowych zadań ===
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            LocalTime defaultShiftTime = LocalTime.of(8, 0); // np. zmiana zaczyna się o 08:00

            Task task1 = new Task(
                    "Przyjęcie dostawy",
                    sdf.parse("2025-05-01"),
                    "Nowe",
                    "Przyjąć dostawę mleka.",
                    defaultShiftTime
            );

            Task task2 = new Task(
                    "Sprawdzenie stanów",
                    sdf.parse("2025-05-03"),
                    "Nowe",
                    "Sprawdzić ilość jogurtów.",
                    defaultShiftTime
            );

            Task task3 = new Task(
                    "Aktualizacja cen",
                    sdf.parse("2025-05-05"),
                    "W trakcie",
                    "Aktualizacja cen nabiału.",
                    defaultShiftTime
            );

            taskRepo.addTask(task1);
            taskRepo.addTask(task2);
            taskRepo.addTask(task3);

            System.out.println(">>> Dodano zadania!");

            // === 2. Pobieranie wszystkich zadań ===
            System.out.println("\n>>> Lista wszystkich zadań:");
            writeTasks(taskRepo.getAllTasks());

            // === 3. Aktualizacja istniejącego zadania ===
            task1.setStatus("W trakcie");
            task1.setDescription("Dostawa mleka zrealizowana w połowie.");
            taskRepo.updateTask(task1);
            System.out.println("\n>>> Zaktualizowano zadanie 1.");

            // === 4. Pobieranie zadania po ID ===
            Task znalezione = taskRepo.findTaskById(task1.getId());
            System.out.println(">>> Zadanie po ID: " + znalezione);

            // === 5. Usuwanie zadania ===
            taskRepo.removeTask(task2);
            System.out.println("\n>>> Usunięto zadanie 2.");

            // === 6. Lista zadań po usunięciu ===
            System.out.println("\n>>> Lista zadań po usunięciu:");
            writeTasks(taskRepo.getAllTasks());

        } finally {
            taskRepo.close();
        }
    }

    /**
     * Pomocnicza metoda wypisująca tasks.
     *
     * @param tasks lista zadań do wypisania
     */
    private static void writeTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("(Brak zadań)");
        } else {
            for (Task z : tasks) {
                System.out.println(z);
            }
        }
        System.out.println("-----------------------------");
    }
}
