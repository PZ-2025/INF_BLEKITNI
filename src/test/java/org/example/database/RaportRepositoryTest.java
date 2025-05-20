package org.example.database;

import org.example.sys.Employee;
import org.example.sys.Raport;
import org.junit.jupiter.api.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RaportRepositoryTest {

    private static RaportRepository raportRepo;
    private static UserRepository    userRepo;
    private static Employee          employee;
    private static Raport            r1, r2;
    private static Date              sqlStart1, sqlEnd1, sqlStart2, sqlEnd2;

    @BeforeAll
    static void setup() {
        raportRepo = new RaportRepository();
        userRepo   = new UserRepository();

        // pick an existing employee
        List<Employee> emps = userRepo.pobierzWszystkichPracownikow();
        assertFalse(emps.isEmpty(), "Must have at least one employee");
        employee = emps.get(0);

        // prepare date ranges
        LocalDate ldStart1 = LocalDate.of(2025, 4, 1);
        LocalDate ldEnd1   = LocalDate.of(2025, 4, 30);
        LocalDate ldStart2 = LocalDate.of(2025, 5, 1);
        LocalDate ldEnd2   = LocalDate.of(2025, 5, 10);

        sqlStart1 = Date.valueOf(ldStart1);
        sqlEnd1   = Date.valueOf(ldEnd1);
        sqlStart2 = Date.valueOf(ldStart2);
        sqlEnd2   = Date.valueOf(ldEnd2);

        // construct two reports
        r1 = new Raport("Raport sprzedaży", ldStart1, ldEnd1, employee, "raporty/sprzedaz_0425.pdf");
        r2 = new Raport("Raport pracowników", ldStart2, ldEnd2, employee, "raporty/pracownicy_0525.pdf");
    }

    @Test
    @Order(1)
    void testAddReports() {
        assertDoesNotThrow(() -> {
            raportRepo.dodajRaport(r1);
            raportRepo.dodajRaport(r2);
        }, "Should add both reports without exception");

        assertTrue(r1.getId() > 0, "r1 should have an ID");
        assertTrue(r2.getId() > 0, "r2 should have an ID");
    }

    @Test
    @Order(2)
    void testFindAllAndUpdate() {
        List<Raport> all = raportRepo.pobierzWszystkieRaporty();
        assertTrue(all.stream().anyMatch(r -> r.getId() == r1.getId()));
        assertTrue(all.stream().anyMatch(r -> r.getId() == r2.getId()));

        // update r1
        r1.setTypRaportu("Raport sprzedaży — zmodyfikowany");
        r1.setSciezkaPliku("raporty/zmieniony_sprzedaz.pdf");
        assertDoesNotThrow(() -> raportRepo.aktualizujRaport(r1));

        // reload
        Raport reloaded = raportRepo.znajdzRaportPoId(r1.getId());
        assertEquals("Raport sprzedaży — zmodyfikowany", reloaded.getTypRaportu());
        assertEquals("raporty/zmieniony_sprzedaz.pdf", reloaded.getSciezkaPliku());
    }

    @Test
    @Order(3)
    void testQueries() {
        // by type fragment
        List<Raport> byType = raportRepo.znajdzPoTypie("sprzedaży");
        assertTrue(byType.stream().allMatch(r -> r.getTypRaportu().toLowerCase().contains("sprzedaży")));

        // by start date range
        List<Raport> byStart = raportRepo.znajdzPoDaciePoczatku(sqlStart1, sqlEnd1);
        assertTrue(byStart.stream().allMatch(r ->
                !r.getDataPoczatku().isBefore(sqlStart1.toLocalDate()) &&
                        !r.getDataPoczatku().isAfter(sqlEnd1.toLocalDate())
        ));

        // by end date range
        List<Raport> byEnd = raportRepo.znajdzPoDacieZakonczenia(sqlStart2, sqlEnd2);
        assertTrue(byEnd.stream().allMatch(r ->
                !r.getDataZakonczenia().isBefore(sqlStart2.toLocalDate()) &&
                        !r.getDataZakonczenia().isAfter(sqlEnd2.toLocalDate())
        ));

        // by employee
        List<Raport> byEmp = raportRepo.znajdzPoPracowniku(employee.getId());
        assertTrue(byEmp.stream().allMatch(r -> r.getPracownik().getId() == employee.getId()));

        // by file path fragment
        List<Raport> byPath = raportRepo.znajdzPoSciezcePliku("pracownicy");
        assertTrue(byPath.stream().allMatch(r ->
                r.getSciezkaPliku().toLowerCase().contains("pracownicy")));
    }

    @Test
    @Order(4)
    void testDelete() {
        // delete r2
        assertDoesNotThrow(() -> raportRepo.usunRaport(r2.getId()));
        assertNull(raportRepo.znajdzRaportPoId(r2.getId()), "r2 should no longer exist");

        // final list should not contain r2
        List<Raport> allAfter = raportRepo.pobierzWszystkieRaporty();
        assertTrue(allAfter.stream().noneMatch(r -> r.getId() == r2.getId()));
    }

    @AfterAll
    static void tearDown() {
        raportRepo.close();
        userRepo.close();
    }
}
