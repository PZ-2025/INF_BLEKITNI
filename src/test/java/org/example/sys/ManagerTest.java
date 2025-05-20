package org.example.sys;

import org.example.sys.Employee;
import org.example.sys.Manager;
import org.example.sys.Address;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {

    private Employee employee;
    private Manager manager;

    @BeforeEach
    void setUp() throws Exception {
        Address address = new Address();
        address.setMiasto("Warszawa");
        employee = new Employee("Jan", "Kowalski", 35, "jan@example.com",
                "jkowal", "bezpieczneHaslo", address, "Manager", new BigDecimal("6000"));
        manager = new Manager(employee);
    }

    @Test
    void testConstructorInitialization() {
        assertEquals(employee, manager.getEmployee());
        assertTrue(manager.getManagedEmployees().isEmpty());
    }

    @Test
    void testAddEmployee() throws Exception {
        Employee e = createOtherEmployee();
        manager.addEmployee(e);
        assertEquals(1, manager.getManagedEmployees().size());
        assertTrue(manager.getManagedEmployees().contains(e));
    }

    @Test
    void testAddNullEmployee() {
        manager.addEmployee(null);
        assertTrue(manager.getManagedEmployees().isEmpty());
    }

    @Test
    void testRemoveEmployee() throws Exception {
        Employee e = createOtherEmployee();
        manager.addEmployee(e);
        manager.removeEmployee(e);
        assertFalse(manager.getManagedEmployees().contains(e));
    }

    @Test
    void testRemoveNonExistentEmployee() throws Exception {
        Employee e = createOtherEmployee();
        manager.removeEmployee(e); // nie dodano wcześniej
        assertTrue(manager.getManagedEmployees().isEmpty());
    }

    @Test
    void testUpdateName() {
        manager.updateName("Tomasz");
        assertEquals("Tomasz", manager.getEmployee().getName());
    }

    @Test
    void testUpdateSurname() {
        manager.updateSurname("Nowak");
        assertEquals("Nowak", manager.getEmployee().getSurname());
    }

    @Test
    void testUpdateAge() {
        manager.updateAge(45);
        assertEquals(45, manager.getEmployee().getAge());
    }

    @Test
    void testUpdateAddress() {
        Address newAddress = new Address();
        newAddress.setMiasto("Wrocław");
        manager.updateAddress(newAddress);
        assertEquals("Wrocław", manager.getEmployee().getAdres().getMiasto());
    }

    @Test
    void testUpdatePassword() {
        manager.updatePassword("newStrongPass");
        assertEquals("newStrongPass", manager.getEmployee().getPassword());
    }

    @Test
    void testUpdateDepartment() {
        manager.updateDepartment("Logistyka");
        assertEquals("Logistyka", manager.getEmployee().getStanowisko());
    }

    @Test
    void testUpdateSalary() {
        manager.updateSalary(new BigDecimal("9000.00"));
        assertEquals(new BigDecimal("9000.00"), manager.getEmployee().getZarobki());
    }

    @Test
    void testWygenerujRaportZespolu() {
        manager.wygenerujRaportZespolu(); // tylko sprawdzamy, że się nie wywala, logika w konsoli
    }

    // Pomocnicza metoda
    private Employee createOtherEmployee() throws Exception {
        Address address = new Address();
        address.setMiasto("Gdańsk");
        return new Employee("Anna", "Zielińska", 28, "anna@example.com",
                "aziel", "haslo1234", address, "Specjalista", new BigDecimal("4800"));
    }
}
