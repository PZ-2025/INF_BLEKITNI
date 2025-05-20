package org.example.sys;

import org.example.sys.AbsenceRequest;
import org.example.sys.Address;
import org.example.sys.Employee;

import org.example.wyjatki.AgeException;
import org.example.wyjatki.NameException;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class AbsenceRequestTest {

    private AbsenceRequest request;
    private Employee mockEmployee;

    @BeforeEach
    void setup() {
        try {
            Address address = new Address();
            address.setMiasto("Warszawa");

            mockEmployee = new Employee(
                    "Jan", "Kowalski", 30, address,
                    "jankow", "bezpieczneHaslo123",
                    "Kasjer", new BigDecimal("4000")
            );

            request = new AbsenceRequest();
            request.setPracownik(mockEmployee);

        } catch (NameException | AgeException | PasswordException | SalaryException e) {
            fail("Błąd podczas tworzenia mockEmployee: " + e.getMessage());
        }
    }

    @Test
    void testSetAndGetTypWniosku() {
        request.setTypWniosku("Urlop");
        assertEquals("Urlop", request.getTypWniosku());
    }

    @Test
    void testSetAndGetDataRozpoczecia() {
        Date now = new Date();
        request.setDataRozpoczecia(now);
        assertEquals(now, request.getDataRozpoczecia());
    }

    @Test
    void testSetAndGetDataZakonczenia() {
        Date later = new Date(System.currentTimeMillis() + 86400000);
        request.setDataZakonczenia(later);
        assertEquals(later, request.getDataZakonczenia());
    }

    @Test
    void testSetAndGetOpis() {
        request.setOpis("Nieobecność z powodu choroby");
        assertEquals("Nieobecność z powodu choroby", request.getOpis());
    }

    @Test
    void testSetAndGetPracownik() {
        assertEquals("Jan", request.getPracownik().getName());
        assertEquals("Kowalski", request.getPracownik().getSurname());
    }

    @Test
    void testDefaultStatus() {
        assertEquals(AbsenceRequest.StatusWniosku.OCZEKUJE, request.getStatus());
    }

    @Test
    void testSetAndGetStatus() {
        request.setStatus(AbsenceRequest.StatusWniosku.PRZYJETY);
        assertEquals(AbsenceRequest.StatusWniosku.PRZYJETY, request.getStatus());
    }

    @Test
    void testToStringIncludesFields() {
        request.setTypWniosku("Chorobowe");
        request.setOpis("Grypa");
        Date start = new Date();
        Date end = new Date(System.currentTimeMillis() + 2 * 86400000);
        request.setDataRozpoczecia(start);
        request.setDataZakonczenia(end);
        request.setStatus(AbsenceRequest.StatusWniosku.PRZYJETY);
        String result = request.toString();

        assertTrue(result.contains("Chorobowe"));
        assertTrue(result.contains("Grypa"));
        assertTrue(result.contains("Jan"));
        assertTrue(result.contains("Kowalski"));
    }
}
