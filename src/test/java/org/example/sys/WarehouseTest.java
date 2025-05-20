package org.example.sys;

import org.example.sys.Product;
import org.example.sys.Warehouse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {

    @Test
    void testParameterizedConstructor() {
        Product produkt = new Product("Laptop", "Elektronika", 1500.00);
        Warehouse warehouse = new Warehouse(produkt, 10);

        assertEquals(produkt, warehouse.getProdukt());
        assertEquals("Laptop", warehouse.getProdukt().getName());
        assertEquals(1500.00, warehouse.getProdukt().getPrice());
        assertEquals(10, warehouse.getIlosc());
    }

    @Test
    void testSettersAndGetters() {
        Product produkt = new Product("Monitor", "Elektronika", 799.99);
        Warehouse warehouse = new Warehouse();
        warehouse.setProdukt(produkt);
        warehouse.setIlosc(25);

        assertEquals("Monitor", warehouse.getProdukt().getName());
        assertEquals(799.99, warehouse.getProdukt().getPrice());
        assertEquals(25, warehouse.getIlosc());
    }

    @Test
    void testToString() {
        Product produkt = new Product("Biurko", "Meble", 300.00);
        Warehouse warehouse = new Warehouse(produkt, 5);
        String result = warehouse.toString();

        assertTrue(result.contains("Biurko"));
        assertTrue(result.contains("5"));
    }

    @Test
    void testZeroQuantity() {
        Product produkt = new Product("Długopis", "Biuro", 0.00);
        Warehouse warehouse = new Warehouse(produkt, 0);

        assertEquals(0.00, warehouse.getProdukt().getPrice());
        assertEquals(0, warehouse.getIlosc());
    }

    @Test
    void testNegativeValuesAccepted() {
        Product produkt = new Product("Krzesło", "Meble", -50.00); // brak walidacji w Product
        Warehouse warehouse = new Warehouse(produkt, -10);

        assertEquals(-50.00, warehouse.getProdukt().getPrice());
        assertEquals(-10, warehouse.getIlosc());
    }
}
