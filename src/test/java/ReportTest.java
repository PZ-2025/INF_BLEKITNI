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
        address.setCity("Warszawa");

        Employee employee = new Employee();
        employee.setName("Jan");
        employee.setSurname("Kowalski");
        employee.setAge(35);
        employee.setEmail("jan.kowalski@example.com");
        employee.setLogin("jkowal");
        try {
            employee.setPassword("bezpieczneHaslo");
            employee.setSalary(new BigDecimal("5000"));
        } catch (Exception e) {
            fail("Nieoczekiwany wyjątek: " + e.getMessage());
        }
        employee.setPosition("Kierownik");
        employee.setAddress(address);

        LocalDate start = LocalDate.of(2025, 5, 1);
        LocalDate end = LocalDate.of(2025, 5, 31);

        Report report = new Report("Miesięczny", start, end, employee, "plik.pdf");

        assertEquals("Miesięczny", report.getReportType());
        assertEquals(start, report.getStartDate());
        assertEquals(end, report.getEndDate());
        assertEquals(employee, report.getEmployee());
        assertEquals("plik.pdf", report.getFilePath());
    }

    @Test
    void testSettersAndGetters() {
        Report report = new Report();

        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 10);

        Employee employee = new Employee();
        employee.setName("Anna");

        report.setReportType("Dzienny");
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setEmployee(employee);
        report.setFilePath("raport_dzienny.pdf");

        assertEquals("Dzienny", report.getReportType());
        assertEquals(startDate, report.getStartDate());
        assertEquals(endDate, report.getEndDate());
        assertEquals(employee, report.getEmployee());
        assertEquals("raport_dzienny.pdf", report.getFilePath());
    }

    @Test
    void testDefaultConstructorValues() {
        Report report = new Report();

        assertNull(report.getReportType());
        assertNull(report.getStartDate());
        assertNull(report.getEndDate());
        assertNull(report.getEmployee());
        assertNull(report.getFilePath());
    }
}
