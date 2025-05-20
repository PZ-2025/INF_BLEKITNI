package org.example.database;

import org.example.sys.AbsenceRequest;
import org.example.sys.Employee;
import org.junit.jupiter.api.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AbsenceRequestRepositoryTest {

    private static AbsenceRequestRepository absenceRepo;
    private static UserRepository userRepo;
    private static Employee emp1, emp2;
    private static AbsenceRequest r1, r2;
    private static Date start, end, laterStart, laterEnd;

    @BeforeAll
    static void setup() throws Exception {
        absenceRepo = new AbsenceRequestRepository();
        userRepo    = new UserRepository();

        // prepare two distinct employees
        List<Employee> all = userRepo.pobierzWszystkichPracownikow();
        assertTrue(all.size() >= 2, "Musisz mieć przynajmniej 2 pracowników w bazie");
        emp1 = all.get(0);
        emp2 = all.get(1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        start      = sdf.parse("2025-07-01");
        end        = sdf.parse("2025-07-10");
        laterStart = sdf.parse("2025-07-15");
        laterEnd   = sdf.parse("2025-07-25");
    }

    @Test
    @Order(1)
    void testAddTwoRequests() {
        r1 = new AbsenceRequest();
        r1.setTypWniosku("Urlop wypoczynkowy");
        r1.setDataRozpoczecia(start);
        r1.setDataZakonczenia(end);
        r1.setOpis("Testowy urlop");
        r1.setPracownik(emp1);
        r1.setStatus(AbsenceRequest.StatusWniosku.OCZEKUJE);
        assertDoesNotThrow(() -> absenceRepo.dodajWniosek(r1));

        r2 = new AbsenceRequest();
        r2.setTypWniosku("Urlop okolicznościowy");
        r2.setDataRozpoczecia(laterStart);
        r2.setDataZakonczenia(laterEnd);
        r2.setOpis("Wesele kolegi");
        r2.setPracownik(emp2);
        r2.setStatus(AbsenceRequest.StatusWniosku.PRZYJETY);
        assertDoesNotThrow(() -> absenceRepo.dodajWniosek(r2));

        // after adding, IDs should be assigned
        assertTrue(r1.getId() > 0, "r1 powinno mieć nadane ID");
        assertTrue(r2.getId() > 0, "r2 powinno mieć nadane ID");
    }

    @Test
    @Order(2)
    void testFindAllAndUpdate() {
        // pobierz wszystkie i upewnij się, że co najmniej 2 są
        List<AbsenceRequest> all = absenceRepo.pobierzWszystkieWnioski();
        assertTrue(all.size() >= 2, "Powinno być co najmniej 2 wnioski");

        // zaktualizuj pierwszy
        r1.setOpis("Zaktualizowany opis");
        r1.setStatus(AbsenceRequest.StatusWniosku.PRZYJETY);
        assertDoesNotThrow(() -> absenceRepo.aktualizujWniosek(r1));

        // przeładuj i sprawdź zmiany
        AbsenceRequest loaded = absenceRepo.znajdzWniosekPoId(r1.getId());
        assertNotNull(loaded, "Załadowany wniosek nie może być null");
        assertEquals("Zaktualizowany opis", loaded.getOpis());
        assertEquals(AbsenceRequest.StatusWniosku.PRZYJETY, loaded.getStatus());
    }

    @Test
    @Order(3)
    void testCustomQueries() {
        // wg pracownika
        List<AbsenceRequest> byEmp1 = absenceRepo.znajdzWnioskiPracownika(emp1);
        assertTrue(byEmp1.stream().anyMatch(r -> r.getId() == r1.getId()));

        // wg ID pracownika
        List<AbsenceRequest> byEmp2Id = absenceRepo.znajdzWnioskiPracownikaPoId(emp2.getId());
        assertTrue(byEmp2Id.stream().anyMatch(r -> r.getId() == r2.getId()));

        // wg typu
        List<AbsenceRequest> byType = absenceRepo.znajdzWnioskiPoTypie("Urlop wypoczynkowy");
        assertTrue(byType.stream().allMatch(r -> r.getTypWniosku().equals("Urlop wypoczynkowy")));

        // wg statusu
        List<AbsenceRequest> byStatus = absenceRepo.znajdzWnioskiPoStatusie(AbsenceRequest.StatusWniosku.PRZYJETY);
        assertTrue(byStatus.stream().allMatch(r -> r.getStatus() == AbsenceRequest.StatusWniosku.PRZYJETY));

        // od daty
        List<AbsenceRequest> fromDate = absenceRepo.znajdzWnioskiOdDaty(start);
        assertTrue(fromDate.stream().anyMatch(r -> !r.getDataRozpoczecia().before(start)));

        // do daty
        List<AbsenceRequest> toDate = absenceRepo.znajdzWnioskiDoDaty(laterEnd);
        assertTrue(toDate.stream().anyMatch(r -> !r.getDataZakonczenia().after(laterEnd)));

        // w zakresie
        List<AbsenceRequest> inRange = absenceRepo.znajdzWnioskiWZakresieDat(start, laterEnd);
        assertTrue(inRange.size() >= 2);

        // nachodzące na zakres
        List<AbsenceRequest> overlapping = absenceRepo.znajdzWnioskiNachodzaceNaZakresDat(start, laterEnd);
        assertTrue(overlapping.size() >= 2);
    }

    @Test
    @Order(4)
    void testDeleteBoth() {
        // usuń oba
        assertDoesNotThrow(() -> absenceRepo.usunWniosek(r1.getId()));
        assertDoesNotThrow(() -> absenceRepo.usunWniosek(r2.getId()));

        // powinny już nie istnieć
        assertNull(absenceRepo.znajdzWniosekPoId(r1.getId()));
        assertNull(absenceRepo.znajdzWniosekPoId(r2.getId()));

        // lista powinna być pusta lub pomniejszona
        List<AbsenceRequest> allAfter = absenceRepo.pobierzWszystkieWnioski();
        assertTrue(allAfter.stream().noneMatch(r -> r.getId() == r1.getId()
                || r.getId() == r2.getId()));
    }

    @AfterAll
    static void tearDown() {
        absenceRepo.close();
        userRepo.close();
    }
}
