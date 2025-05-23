import org.example.sys.Admin;
import org.example.sys.Employee;
import org.example.sys.Address;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    private Admin admin;
    private Employee employee;

    @BeforeEach
    void setup() {
        employee = exampleEmployee();
        admin = new Admin(employee);
    }

    @Test
    void testIsAdmin() {
        assertTrue(admin.isAdmin());
    }

    @Test
    void testGetEmployee() {
        assertEquals(employee, admin.getEmployee());
    }

    @Test
    void testUpdateName() {
        admin.updateName("Ewa");
        assertEquals("Ewa", employee.getName());
    }

    @Test
    void testUpdateSurname() {
        admin.updateSurname("Nowak");
        assertEquals("Nowak", employee.getSurname());
    }

    @Test
    void testUpdateAge() {
        admin.updateAge(45);
        assertEquals(45, employee.getAge());
    }

    @Test
    void testUpdateAddress() {
        Address newAddr = new Address();
        newAddr.setCity("Kraków");

        admin.updateAddress(newAddr);
        assertEquals("Kraków", employee.getAddress().getCity());
    }

    @Test
    void testUpdatePassword() {
        admin.updatePassword("newSecurePass");
        assertEquals("newSecurePass", employee.getPassword());
    }

    @Test
    void testUpdateDepartment() {
        admin.updateDepartment("HR");
        assertEquals("HR", employee.getPosition());
    }

    @Test
    void testUpdateSalary() {
        BigDecimal newSalary = new BigDecimal("8800.50");
        admin.updateSalary(newSalary);
        assertEquals(newSalary, employee.getSalary());
    }

    @Test
    void testAddAndRemoveEmployee() {
        Employee e2 = exampleEmployee2();
        assertTrue(admin.getAllEmployees().isEmpty());

        admin.addEmployee(e2);
        assertEquals(1, admin.getAllEmployees().size());
        assertTrue(admin.getAllEmployees().contains(e2));

        admin.removeEmployee(e2);
        assertTrue(admin.getAllEmployees().isEmpty());
    }

    /**
     * Pomocnicza metoda do tworzenia przykładowego pracownika
     */
    private Employee exampleEmployee() {
        try {
            Address addr = new Address();
            addr.setCity("Poznań");

            return new Employee(
                    "Anna", "Kowalska", 28, "anna@wp.pl",
                    "akowal", "pass12345", addr,
                    "Sprzedawca", new BigDecimal("4200")
            );
        } catch (Exception e) {
            throw new RuntimeException("Błąd przy tworzeniu przykładowego pracownika", e);
        }
    }

    private Employee exampleEmployee2() {
        try {
            Address addr = new Address();
            addr.setCity("Gdańsk");

            return new Employee(
                    "Marek", "Nowicki", 32, "marek@wp.pl",
                    "mnow", "pass67890", addr,
                    "Magazynier", new BigDecimal("3900")
            );
        } catch (Exception e) {
            throw new RuntimeException("Błąd przy tworzeniu przykładowego pracownika 2", e);
        }
    }
}
