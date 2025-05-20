package org.example.sys;

import org.example.sys.Address;
import org.example.sys.Employee;
import org.example.wyjatki.AgeException;
import org.example.wyjatki.NameException;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    private Employee createTestEmployee() throws NameException, AgeException, PasswordException, SalaryException {
        Address address = new Address();
        address.setMiasto("TestCity");

        return new Employee(
                "Jan", "Kowalski", 30, "jan@example.com",
                "jankowal", "securePass123", address,
                "Kierownik", new BigDecimal("5000")
        );
    }

    @Test
    void testParameterizedConstructor() {
        try {
            Employee person = createTestEmployee();

            assertEquals("Jan", person.getName());
            assertEquals("Kowalski", person.getSurname());
            assertEquals(30, person.getAge());
            assertEquals("jan@example.com", person.getEmail());
        } catch (Exception e) {
            fail("Wyjątek w konstruktorze: " + e.getMessage());
        }
    }

    @Test
    void testToString() throws Exception {
        Employee person = createTestEmployee();
        String expected = "Jan Kowalski (30), jan@example.com";
        assertEquals(expected, person.toString());
    }

    @Test
    void testSettersAndGetters() throws Exception {
        Employee person = createTestEmployee();

        person.setName("Piotr");
        person.setSurname("Wiśniewski");
        person.setAge(40);
        person.setEmail("piotr@example.com");

        assertEquals("Piotr", person.getName());
        assertEquals("Wiśniewski", person.getSurname());
        assertEquals(40, person.getAge());
        assertEquals("piotr@example.com", person.getEmail());
    }

    @Test
    void testSetNameInvalid() throws Exception {
        Employee person = createTestEmployee();
        assertThrows(NameException.class, () -> person.setName(""));
        assertThrows(NameException.class, () -> person.setName("A"));
        assertThrows(NameException.class, () -> person.setName(null));
    }

    @Test
    void testSetSurnameInvalid() throws Exception {
        Employee person = createTestEmployee();
        assertThrows(NameException.class, () -> person.setSurname(""));
        assertThrows(NameException.class, () -> person.setSurname("B"));
        assertThrows(NameException.class, () -> person.setSurname(null));
    }

    @Test
    void testSetAgeInvalid() throws Exception {
        Employee person = createTestEmployee();
        assertThrows(AgeException.class, () -> person.setAge(-1));
        assertThrows(AgeException.class, () -> person.setAge(130));
        assertThrows(AgeException.class, () -> person.setAge(17));
    }
}
