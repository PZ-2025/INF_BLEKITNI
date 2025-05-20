package org.example.database;

import org.example.sys.Task;
import org.junit.jupiter.api.*;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskRepositoryTest {

    private static TaskRepository taskRepo;
    private static SimpleDateFormat sdf;

    private static Task zad1;
    private static Task zad2;
    private static Task zad3;

    @BeforeAll
    static void setup() throws Exception {
        taskRepo = new TaskRepository();
        sdf      = new SimpleDateFormat("yyyy-MM-dd");

        // prepare three tasks
        Date d1 = sdf.parse("2025-05-01");
        Date d2 = sdf.parse("2025-05-03");
        Date d3 = sdf.parse("2025-05-05");

        zad1 = new Task("Przyjęcie dostawy", d1, "Nowe", "Przyjąć dostawę mleka.", LocalTime.of(2, 30));
        zad2 = new Task("Sprawdzenie stanów", d2, "Nowe", "Sprawdzić ilość jogurtów.",    LocalTime.of(1, 0));
        zad3 = new Task("Aktualizacja cen",  d3, "W trakcie", "Aktualizacja cen nabiału.",  null);

        // persist them
        assertDoesNotThrow(() -> taskRepo.dodajZadanie(zad1), "Should add zad1");
        assertDoesNotThrow(() -> taskRepo.dodajZadanie(zad2), "Should add zad2");
        assertDoesNotThrow(() -> taskRepo.dodajZadanie(zad3), "Should add zad3");

        // make sure they got IDs
        assertTrue(zad1.getId() > 0);
        assertTrue(zad2.getId() > 0);
        assertTrue(zad3.getId() > 0);
    }

    @Test
    @Order(1)
    void testFindAllAndSearch() {
        List<Task> all = taskRepo.pobierzWszystkieZadania();
        assertTrue(all.size() >= 3, "Should have at least 3 tasks");

        // by name fragment
        List<Task> byName = taskRepo.znajdzPoNazwie("Sprawdzenie");
        assertTrue(byName.stream().allMatch(t -> t.getNazwa().contains("Sprawdzenie")));

        // by exact date
        List<Task> byDate = taskRepo.znajdzPoDacie(assertDoesNotThrow(() -> sdf.parse("2025-05-03")));
        assertTrue(byDate.stream().allMatch(t -> sdf.format(t.getData()).equals("2025-05-03")));

        // by status
        List<Task> byStatus = taskRepo.znajdzPoStatusie("Nowe");
        assertTrue(byStatus.stream().allMatch(t -> t.getStatus().equals("Nowe")));

        // by description fragment
        List<Task> byDesc = taskRepo.znajdzPoOpisie("mleka");
        assertTrue(byDesc.stream().allMatch(t -> t.getOpis().toLowerCase().contains("mleka")));

        // by shift duration between 01:00 and 03:00
        List<Task> byTime = taskRepo.znajdzPoCzasieTrwaniaZmiany(
                LocalTime.of(1, 0), LocalTime.of(3, 0));
        assertTrue(
                byTime.stream().allMatch(t -> {
                    LocalTime ct = t.getCzasTrwaniaZmiany();
                    return ct != null
                            && !ct.isBefore(LocalTime.of(1, 0))
                            && !ct.isAfter (LocalTime.of(3, 0));
                })
        );
    }

    @Test
    @Order(2)
    void testUpdate() {
        // change status of zad1
        zad1.setStatus("Zakończone");
        assertDoesNotThrow(() -> taskRepo.aktualizujZadanie(zad1));

        Task reloaded = taskRepo.znajdzZadaniePoId(zad1.getId());
        assertNotNull(reloaded);
        assertEquals("Zakończone", reloaded.getStatus());
    }

    @Test
    @Order(3)
    void testDelete() {
        // delete zad2
        assertDoesNotThrow(() -> taskRepo.usunZadanie(zad2));

        // confirm it's gone
        Task shouldBeNull = taskRepo.znajdzZadaniePoId(zad2.getId());
        assertNull(shouldBeNull);

        // remaining list
        List<Task> remaining = taskRepo.pobierzWszystkieZadania();
        assertTrue(remaining.stream().noneMatch(t -> t.getId() == zad2.getId()));
    }

    @AfterAll
    static void tearDown() {
        taskRepo.close();
    }
}
