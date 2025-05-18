import org.example.sys.Address;
import org.example.sys.Employee;
import org.example.sys.Report;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReportTest {

    @Test
    void testConstructorInitialization() {
        Address address = new Address();
        address.setMiasto("Warszawa");

        Employee employee = new Employee();
        employee.setName("Jan");
        employee.setSurname("Kowalski");
        employee.setAge(35);
        employee.setEmail("jan.kowalski@example.com");
        employee.setLogin("jkowal");
        try {
            employee.setPassword("bezpieczneHaslo");
            employee.setZarobki(new BigDecimal("5000"));
        } catch (Exception e) {
            fail("Nieoczekiwany wyjątek: " + e.getMessage());
        }
        employee.setStanowisko("Kierownik");
        employee.setAdres(address);

        LocalDate start = LocalDate.of(2025, 5, 1);
        LocalDate end = LocalDate.of(2025, 5, 31);

        Report report = new Report("Miesięczny", start, end, employee, "plik.pdf");

        assertEquals("Miesięczny", report.getTypRaportu());
        assertEquals(start, report.getDataPoczatku());
        assertEquals(end, report.getDataZakonczenia());
        assertEquals(employee, report.getPracownik());
        assertEquals("plik.pdf", report.getSciezkaPliku());
    }

    @Test
    void testSettersAndGetters() {
        Report report = new Report();

        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 10);

        Employee employee = new Employee();
        employee.setName("Anna");

        report.setTypRaportu("Dzienny");
        report.setDataPoczatku(startDate);
        report.setDataZakonczenia(endDate);
        report.setPracownik(employee);
        report.setSciezkaPliku("raport_dzienny.pdf");

        assertEquals("Dzienny", report.getTypRaportu());
        assertEquals(startDate, report.getDataPoczatku());
        assertEquals(endDate, report.getDataZakonczenia());
        assertEquals(employee, report.getPracownik());
        assertEquals("raport_dzienny.pdf", report.getSciezkaPliku());
    }

    @Test
    void testDefaultConstructorValues() {
        Report report = new Report();

        assertNull(report.getTypRaportu());
        assertNull(report.getDataPoczatku());
        assertNull(report.getDataZakonczenia());
        assertNull(report.getPracownik());
        assertNull(report.getSciezkaPliku());
    }
}
