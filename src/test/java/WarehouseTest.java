import org.example.sys.Product;
import org.example.sys.Warehouse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {

    @Test
    void testParameterizedConstructor() {
        Product product = new Product("Laptop", "Elektronika", 1500.00);
        Warehouse warehouse = new Warehouse(product, 10);

        assertEquals(product, warehouse.getProduct());
        assertEquals("Laptop", warehouse.getProduct().getName());
        assertEquals(1500.00, warehouse.getProduct().getPrice());
        assertEquals(10, warehouse.getQuantity());
    }

    @Test
    void testSettersAndGetters() {
        Product product = new Product("Monitor", "Elektronika", 799.99);
        Warehouse warehouse = new Warehouse();
        warehouse.setProduct(product);
        warehouse.setQuantity(25);

        assertEquals("Monitor", warehouse.getProduct().getName());
        assertEquals(799.99, warehouse.getProduct().getPrice());
        assertEquals(25, warehouse.getQuantity());
    }

    @Test
    void testToString() {
        Product product = new Product("Biurko", "Meble", 300.00);
        Warehouse warehouse = new Warehouse(product, 5);
        String result = warehouse.toString();

        assertTrue(result.contains("Biurko"));
        assertTrue(result.contains("5"));
    }

    @Test
    void testZeroQuantity() {
        Product product = new Product("Długopis", "Biuro", 0.00);
        Warehouse warehouse = new Warehouse(product, 0);

        assertEquals(0.00, warehouse.getProduct().getPrice());
        assertEquals(0, warehouse.getQuantity());
    }

    @Test
    void testNegativeValuesAccepted() {
        Product product = new Product("Krzesło", "Meble", -50.00); // brak walidacji w Product
        Warehouse warehouse = new Warehouse(product, -10);

        assertEquals(-50.00, warehouse.getProduct().getPrice());
        assertEquals(-10, warehouse.getQuantity());
    }
}
