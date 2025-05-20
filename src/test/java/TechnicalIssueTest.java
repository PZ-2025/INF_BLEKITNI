import org.example.sys.Employee;
import org.example.sys.TechnicalIssue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TechnicalIssueTest {

    private TechnicalIssue issue;
    private Employee employee;

    @BeforeEach
    void setUp() {
        issue = new TechnicalIssue();
        employee = new Employee();
        employee.setName("Jan");
        employee.setSurname("Kowalski");
    }

    @Test
    void testSetAndGetType() {
        issue.setType("Awaria sprzętu");
        assertEquals("Awaria sprzętu", issue.getType());
    }

    @Test
    void testSetAndGetDescription() {
        issue.setDescription("Problem z drukarką");
        assertEquals("Problem z drukarką", issue.getDescription());
    }

    @Test
    void testSetAndGetDateSubmitted() {
        LocalDate date = LocalDate.of(2025, 5, 20);
        issue.setDateSubmitted(date);
        assertEquals(date, issue.getDateSubmitted());
    }

    @Test
    void testSetAndGetEmployee() {
        issue.setEmployee(employee);
        assertEquals(employee, issue.getEmployee());
        assertEquals("Jan", issue.getEmployee().getName());
    }

    @Test
    void testSetAndGetStatus() {
        issue.setStatus("W trakcie");
        assertEquals("W trakcie", issue.getStatus());
    }

    @Test
    void testDefaultConstructor() {
        TechnicalIssue newIssue = new TechnicalIssue();
        assertNotNull(newIssue);
        assertEquals("Nowe", newIssue.getStatus()); // wartość domyślna
    }

    @Test
    void testFullConstructor() {
        LocalDate date = LocalDate.of(2025, 5, 20);
        TechnicalIssue full = new TechnicalIssue(
                "Błąd oprogramowania",
                "System nie odpowiada",
                date,
                employee,
                "Nowe"
        );

        assertEquals("Błąd oprogramowania", full.getType());
        assertEquals("System nie odpowiada", full.getDescription());
        assertEquals(date, full.getDateSubmitted());
        assertEquals(employee, full.getEmployee());
        assertEquals("Nowe", full.getStatus());
    }

    @Test
    void testConstructorWithId() {
        LocalDate date = LocalDate.of(2025, 5, 20);
        TechnicalIssue withId = new TechnicalIssue(
                10,
                "Inne",
                "Test opisu",
                date,
                employee,
                "Zamknięte"
        );

        assertEquals(10, withId.getId());
        assertEquals("Inne", withId.getType());
        assertEquals("Test opisu", withId.getDescription());
        assertEquals(date, withId.getDateSubmitted());
        assertEquals("Zamknięte", withId.getStatus());
    }
}
