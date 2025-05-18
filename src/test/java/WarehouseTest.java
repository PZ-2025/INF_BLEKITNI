import org.example.sys.Warehouse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {

    @Test
    void testParameterizedConstructor() {
        Warehouse warehouse = new Warehouse("Laptop", new BigDecimal("1500.00"), 10);

        assertEquals("Laptop", warehouse.getNazwa());
        assertEquals(new BigDecimal("1500.00"), warehouse.getCena());
        assertEquals(10, warehouse.getIloscWmagazynie());
    }

    @Test
    void testSettersAndGetters() {
        Warehouse warehouse = new Warehouse();
        warehouse.setNazwa("Monitor");
        warehouse.setCena(new BigDecimal("799.99"));
        warehouse.setIloscWmagazynie(25);

        assertEquals("Monitor", warehouse.getNazwa());
        assertEquals(new BigDecimal("799.99"), warehouse.getCena());
        assertEquals(25, warehouse.getIloscWmagazynie());
    }

    @Test
    void testToString() {
        Warehouse warehouse = new Warehouse("Biurko", new BigDecimal("300.00"), 5);
        String expected = "Warehouse{id=0, nazwa='Biurko', cena=300.00, iloscWmagazynie=5}";
        assertEquals(expected, warehouse.toString());
    }

    @Test
    void testZeroPriceAndQuantity() {
        Warehouse warehouse = new Warehouse("Długopis", new BigDecimal("0.00"), 0);

        assertEquals(new BigDecimal("0.00"), warehouse.getCena());
        assertEquals(0, warehouse.getIloscWmagazynie());
    }

    @Test
    void testNegativeValuesHandling() {
        Warehouse warehouse = new Warehouse();
        warehouse.setNazwa("Krzesło");

        warehouse.setCena(new BigDecimal("-50.00"));
        warehouse.setIloscWmagazynie(-10);

        assertEquals(new BigDecimal("-50.00"), warehouse.getCena()); // brak walidacji, więc akceptuje
        assertEquals(-10, warehouse.getIloscWmagazynie());
    }
}
