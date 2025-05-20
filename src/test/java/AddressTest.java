import org.example.sys.Address;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    private Address address;

    @BeforeEach
    void setup() {
        address = new Address();
    }

    @Test
    void testSetAndGetMiejscowosc() {
        address.setMiejscowosc("Warszawa");
        assertEquals("Warszawa", address.getMiejscowosc());
    }

    @Test
    void testSetAndGetNumerDomu() {
        address.setNumerDomu("15B");
        assertEquals("15B", address.getNumerDomu());
    }

    @Test
    void testSetAndGetNumerMieszkania() {
        address.setNumerMieszkania("8");
        assertEquals("8", address.getNumerMieszkania());
    }

    @Test
    void testSetAndGetKodPocztowy() {
        address.setKodPocztowy("00-123");
        assertEquals("00-123", address.getKodPocztowy());
    }

    @Test
    void testSetAndGetMiasto() {
        address.setMiasto("Kraków");
        assertEquals("Kraków", address.getMiasto());
    }

    @Test
    void testToStringWithMieszkanie() {
        address.setMiejscowosc("Poznań");
        address.setNumerDomu("10A");
        address.setNumerMieszkania("4");
        address.setKodPocztowy("60-123");
        address.setMiasto("Poznań");

        String expected = "Poznań, ul. 10A/4, 60-123 Poznań";
        assertEquals(expected, address.toString());
    }

    @Test
    void testToStringWithoutMieszkanie() {
        address.setMiejscowosc("Wrocław");
        address.setNumerDomu("7");
        address.setNumerMieszkania(""); // brak mieszkania
        address.setKodPocztowy("50-001");
        address.setMiasto("Wrocław");

        String expected = "Wrocław, ul. 7, 50-001 Wrocław";
        assertEquals(expected, address.toString());
    }
}
