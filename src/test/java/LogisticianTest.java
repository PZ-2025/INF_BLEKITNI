import org.example.sys.Employee;
import org.example.sys.Logistician;
import org.example.sys.Address;

import org.example.wyjatki.AgeException;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LogisticianTest {

    private Employee employee;
    private Logistician logistician;

    @BeforeEach
    void setUp() throws AgeException, PasswordException, SalaryException {
        Address address = new Address();
        address.setCity("Warszawa");

        employee = new Employee(
                "Anna", "Kowalska", 30, "anna@example.com",
                "akowalska", "securePass123", address,
                "Logistyk", new BigDecimal("5000.00")
        );

        logistician = new Logistician(employee);
    }

    @Test
    void testUpdateName() {
        logistician.updateName("Joanna");
        assertEquals("Joanna", employee.getName());
    }

    @Test
    void testUpdateSurname() {
        logistician.updateSurname("Nowak");
        assertEquals("Nowak", employee.getSurname());
    }

    @Test
    void testUpdateAge() {
        logistician.updateAge(40);
        assertEquals(40, employee.getAge());
    }

    @Test
    void testUpdateAddress() {
        Address newAddress = new Address();
        newAddress.setCity("Kraków");
        logistician.updateAddress(newAddress);
        assertEquals("Kraków", employee.getAddress().getCity());
    }

    @Test
    void testUpdatePassword() {
        logistician.updatePassword("newSecurePass");
        assertEquals("newSecurePass", employee.getPassword());
    }

    @Test
    void testUpdateDepartment() {
        logistician.updateDepartment("Koordynator");
        assertEquals("Koordynator", employee.getPosition());
    }

    @Test
    void testUpdateSalary() {
        logistician.updateSalary(new BigDecimal("7500.00"));
        assertEquals(new BigDecimal("7500.00"), employee.getSalary());
    }

    @Test
    void testAssignOrder() {
        assertDoesNotThrow(() -> logistician.assignOrder(123));
    }
}
