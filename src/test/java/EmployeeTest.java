import org.example.sys.Address;
import org.example.sys.Employee;
import org.example.wyjatki.AgeException;
import org.example.wyjatki.NameException;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeTest {

    @Test
    void testConstructorInitialization() throws NameException, AgeException, PasswordException, SalaryException {
        Address address = new Address();
        address.setCity("Warszawa");

        Employee employee = new Employee(
                "John", "Doe", 30, "john@example.com",
                "jdoe", "pass12345", address, "Manager", new BigDecimal("5000.00")
        );

        assertEquals("John", employee.getName());
        assertEquals("Doe", employee.getSurname());
        assertEquals(30, employee.getAge());
        assertEquals("john@example.com", employee.getEmail());
        assertEquals("jdoe", employee.getLogin());
        assertEquals("pass12345", employee.getPassword());
        assertEquals("Manager", employee.getPosition());
        assertEquals(new BigDecimal("5000.00"), employee.getSalary());
        assertFalse(employee.isOnSickLeave());
        assertNull(employee.getSickLeaveStartDate());
        assertEquals("Warszawa", employee.getAddress().getCity());
    }

    @Test
    void testSettersAndGetters() throws PasswordException, SalaryException, AgeException, NameException {
        Employee employee = new Employee();
        Address address = new Address();
        address.setCity("Lublin");

        employee.setName("Jane");
        employee.setSurname("Smith");
        employee.setAge(25);
        employee.setEmail("jane@example.com");
        employee.setLogin("jsmith");
        employee.setPassword("securepass");
        employee.setPosition("Developer");
        employee.setSalary(new BigDecimal("6000.00"));
        employee.setAddress(address);

        assertEquals("Jane", employee.getName());
        assertEquals("Smith", employee.getSurname());
        assertEquals(25, employee.getAge());
        assertEquals("jane@example.com", employee.getEmail());
        assertEquals("jsmith", employee.getLogin());
        assertEquals("securepass", employee.getPassword());
        assertEquals("Developer", employee.getPosition());
        assertEquals(new BigDecimal("6000.00"), employee.getSalary());
        assertEquals("Lublin", employee.getAddress().getCity());
    }

    @Test
    void testStartSickLeave() throws NameException, AgeException, PasswordException, SalaryException {
        Address address = new Address();
        address.setCity("Kraków");

        Employee employee = new Employee(
                "Anna", "Nowak", 29, "anna@ex.com",
                "anowak", "haslo1234", address, "Sprzedawca", new BigDecimal("4200")
        );

        Date startDate = new Date();
        employee.startSickLeave(startDate);

        assertTrue(employee.isOnSickLeave());
        assertEquals(startDate, employee.getSickLeaveStartDate());
    }

    @Test
    void testDefaultValues() {
        Employee employee = new Employee();
        assertNull(employee.getName());
        assertNull(employee.getSurname());
        assertEquals(0, employee.getAge());
        assertNull(employee.getEmail());
        assertNull(employee.getLogin());
        assertNull(employee.getPassword());
        assertNull(employee.getPosition());
        assertNull(employee.getSalary());
        assertNull(employee.getAddress());
        assertFalse(employee.isOnSickLeave());
        assertNull(employee.getSickLeaveStartDate());
    }

    @Test
    void testEndSickLeave() throws NameException, AgeException, PasswordException, SalaryException {
        Address address = new Address();
        address.setCity("Gdańsk");

        Employee employee = new Employee(
                "Tomasz", "Lis", 45, "tomasz@abc.pl",
                "tlis", "bezpieczneHaslo", address, "Kierownik", new BigDecimal("7500")
        );

        Date startDate = new Date();
        employee.startSickLeave(startDate);
        assertTrue(employee.isOnSickLeave());

        employee.endSickLeave();
        assertFalse(employee.isOnSickLeave());
        assertNull(employee.getSickLeaveStartDate());
    }

    public static class Manager extends Employee {

        private List<Employee> employees = new ArrayList<>();

        public Manager(String name, String surname, int age, Address address, String login, String password,
                       String department, BigDecimal salary) throws PasswordException, SalaryException {
            super(name, surname, age, address, login, password, department, salary);
        }

        public Manager(String name, String surname, int age, Address address, String login, String password,
                       String department, BigDecimal salary,
                       List<Employee> employees) throws PasswordException, SalaryException {
            this(name, surname, age, address,login,  password, department, salary);
            this.employees = employees != null ? employees : new ArrayList<>();
        }

        public Manager() {
            super();
        }

        public void addEmployee(Employee employee) {
            if (employee != null && !employees.contains(employee)) {
                employees.add(employee);
            }
        }

        public void removeEmployee(Employee employee) {
            employees.remove(employee);
        }

        public List<Employee> getEmployees() {
            return employees;
        }

        public void setEmployees(List<Employee> employees) {
            this.employees = employees != null ? employees : new ArrayList<>();
        }

        public void updateName(Employee employee, String newName) {
            employee.setName(newName);
        }

        public void updateSurname(Employee employee, String newSurname) {
            employee.setSurname(newSurname);
        }

        public void updateAge(Employee employee, int newAge) {
            employee.setAge(newAge);
        }

        public void updateAddress(Employee employee,Address newAddress) {
            employee.setAddress(newAddress);
        }

        public void updatePassword(Employee employee, String newPassword)throws PasswordException {
            employee.setPassword(newPassword);
        }

        public void updateDepartment(Employee employee, String newDepartment) {
            employee.setPosition(newDepartment);
        }

        public void updateSalary(Employee employee, BigDecimal newSalary) throws SalaryException {
            employee.setSalary(newSalary);
        }

        public Employee getEmployee(Employee employee) {
            for (Employee e : employees) {
                if (e.equals(employee)) {
                    return e;
                }
            }
            return null;
        }
    }
}
