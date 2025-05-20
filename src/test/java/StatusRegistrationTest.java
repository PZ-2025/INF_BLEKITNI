import org.example.sys.StatusRegistration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusRegistrationTest {

    @Test
    void testDisplayNames() {
        assertEquals("Oczekujący", StatusRegistration.OCZEKUJACY.getDisplayName());
        assertEquals("Zaakceptowany", StatusRegistration.ZAAKCEPTOWANY.getDisplayName());
        assertEquals("Odrzucony", StatusRegistration.ODRZUCONY.getDisplayName());
        assertEquals("Zrealizowany", StatusRegistration.ZREALIZOWANY.getDisplayName());
    }

    @Test
    void testToStringEqualsDisplayName() {
        for (StatusRegistration status : StatusRegistration.values()) {
            assertEquals(status.getDisplayName(), status.toString());
        }
    }

    @Test
    void testAllEnumValuesPresent() {
        StatusRegistration[] values = StatusRegistration.values();
        assertEquals(4, values.length);
        assertArrayEquals(
                new StatusRegistration[]{
                        StatusRegistration.OCZEKUJACY,
                        StatusRegistration.ZAAKCEPTOWANY,
                        StatusRegistration.ODRZUCONY,
                        StatusRegistration.ZREALIZOWANY
                },
                values
        );
    }
}
