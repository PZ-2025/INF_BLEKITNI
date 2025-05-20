package org.example.sys;

import org.example.sys.Registration;
import org.example.sys.StatusRegistration;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationTest {

    @Test
    void testFullConstructor() {
        Registration registration = new Registration("Wiadomość", "Jan", "Kowalski", LocalDate.of(2023, 10, 1), "Tytuł", StatusRegistration.ZAAKCEPTOWANY);

        assertEquals("Wiadomość", registration.getMessage());
        assertEquals("Jan", registration.getName());
        assertEquals("Kowalski", registration.getSurname());
        assertEquals("Tytuł", registration.getTitle());
        assertEquals(LocalDate.of(2023, 10, 1), registration.getDate());
        assertEquals(StatusRegistration.ZAAKCEPTOWANY, registration.getStatus());
    }

    @Test
    void testConstructorWithDefaultStatus() {
        Registration registration = new Registration("Wiadomość", "Anna", "Nowak", LocalDate.of(2023, 10, 1), "Tytuł");

        assertEquals("Wiadomość", registration.getMessage());
        assertEquals("Anna", registration.getName());
        assertEquals("Nowak", registration.getSurname());
        assertEquals("Tytuł", registration.getTitle());
        assertEquals(LocalDate.of(2023, 10, 1), registration.getDate());
        assertEquals(StatusRegistration.OCZEKUJACY, registration.getStatus()); // Default status
    }

    @Test
    void testMinimalConstructor() {
        Registration registration = new Registration("Wiadomość", LocalDate.of(2023, 10, 1));

        assertEquals("Wiadomość", registration.getMessage());
        assertNull(registration.getName());
        assertNull(registration.getSurname());
        assertNull(registration.getTitle());
        assertEquals(LocalDate.of(2023, 10, 1), registration.getDate());
        assertEquals(StatusRegistration.OCZEKUJACY, registration.getStatus()); // Default status
    }

    @Test
    void testSettersAndGetters() {
        Registration registration = new Registration("Wiadomość", "Jan", "Kowalski", LocalDate.of(2023, 10, 1), "Tytuł");
        registration.setMessage("Nowa wiadomość");
        registration.setName("Piotr");
        registration.setSurname("Wiśniewski");
        registration.setTitle("Nowy tytuł");
        registration.setDate(LocalDate.of(2024, 1, 1));
        registration.setStatus(StatusRegistration.ZREALIZOWANY);

        assertEquals("Nowa wiadomość", registration.getMessage());
        assertEquals("Piotr", registration.getName());
        assertEquals("Wiśniewski", registration.getSurname());
        assertEquals("Nowy tytuł", registration.getTitle());
        assertEquals(LocalDate.of(2024, 1, 1), registration.getDate());
        assertEquals(StatusRegistration.ZREALIZOWANY, registration.getStatus());
    }

    @Test
    void testAcceptMethod() {
        Registration registration = new Registration("Wiadomość", "Jan", "Kowalski", LocalDate.of(2023, 10, 1), "Tytuł");
        registration.accept();
        assertEquals(StatusRegistration.ZAAKCEPTOWANY, registration.getStatus());
    }

    @Test
    void testRejectMethod() {
        Registration registration = new Registration("Wiadomość", "Jan", "Kowalski", LocalDate.of(2023, 10, 1), "Tytuł");
        registration.reject();
        assertEquals(StatusRegistration.ODRZUCONY, registration.getStatus());
    }

    @Test
    void testRealizeMethod() {
        Registration registration = new Registration("Wiadomość", "Jan", "Kowalski", LocalDate.of(2023, 10, 1), "Tytuł");
        registration.realize();
        assertEquals(StatusRegistration.ZREALIZOWANY, registration.getStatus());
    }

    @Test
    void testToString() {
        Registration registration = new Registration("Wiadomość", "Jan", "Kowalski", LocalDate.of(2023, 10, 1), "Tytuł");
        String result = registration.toString();
        String expected = "Registration{name='Jan Kowalski', title='Tytuł', date=2023-10-01, status=Oczekujący}";
        assertEquals(expected, result);
    }
}