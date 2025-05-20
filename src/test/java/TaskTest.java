import org.example.sys.Task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private Task task;
    private Date today;
    private LocalTime shiftTime;

    @BeforeEach
    void setup() {
        today = new Date();
        shiftTime = LocalTime.of(8, 30);
        task = new Task();
    }

    @Test
    void testSetAndGetNazwa() {
        task.setNazwa("Załadunek towaru");
        assertEquals("Załadunek towaru", task.getNazwa());
    }

    @Test
    void testSetAndGetData() {
        task.setData(today);
        assertEquals(today, task.getData());
    }

    @Test
    void testSetAndGetStatus() {
        task.setStatus("W toku");
        assertEquals("W toku", task.getStatus());
    }

    @Test
    void testSetAndGetOpis() {
        task.setOpis("Opis szczegółowy zadania");
        assertEquals("Opis szczegółowy zadania", task.getOpis());
    }

    @Test
    void testSetAndGetCzasTrwaniaZmiany() {
        task.setCzasTrwaniaZmiany(shiftTime);
        assertEquals(shiftTime, task.getCzasTrwaniaZmiany());
    }

    @Test
    void testFullConstructorWithoutShiftTime() {
        Task t = new Task("Skanowanie", today, "Zakończone", "Skanowanie etykiet");
        assertEquals("Skanowanie", t.getNazwa());
        assertEquals(today, t.getData());
        assertEquals("Zakończone", t.getStatus());
        assertEquals("Skanowanie etykiet", t.getOpis());
        assertNull(t.getCzasTrwaniaZmiany());
    }

    @Test
    void testFullConstructorWithShiftTime() {
        Task t = new Task("Pakowanie", today, "Nowe", "Pakowanie produktów", shiftTime);
        assertEquals("Pakowanie", t.getNazwa());
        assertEquals(today, t.getData());
        assertEquals("Nowe", t.getStatus());
        assertEquals("Pakowanie produktów", t.getOpis());
        assertEquals(shiftTime, t.getCzasTrwaniaZmiany());
    }

    @Test
    void testToStringWithAllFields() {
        task.setNazwa("Przyjęcie dostawy");
        task.setData(today);
        task.setCzasTrwaniaZmiany(shiftTime);

        String output = task.toString();
        assertTrue(output.contains("Przyjęcie dostawy"));
        assertTrue(output.contains(today.toString()));
        assertTrue(output.contains("08:30"));
    }

    @Test
    void testToStringWithoutDateOrShiftTime() {
        task.setNazwa("Inwentaryzacja");

        String output = task.toString();
        assertTrue(output.contains("Inwentaryzacja"));
        assertTrue(output.contains("brak daty"));
        assertTrue(output.contains("brak"));
    }
}
