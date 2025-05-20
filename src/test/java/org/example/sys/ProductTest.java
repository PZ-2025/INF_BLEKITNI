package org.example.sys;

import org.example.sys.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

    private Product createTestProduct() {
        return new Product("Chleb", "Pieczywo", 3.49);
    }

    @Test
    void testParameterizedConstructor() {
        Product product = createTestProduct();

        assertEquals("Chleb", product.getName());
        assertEquals("Pieczywo", product.getCategory());
        assertEquals(3.49, product.getPrice());
    }

    @Test
    void testToString() {
        Product product = createTestProduct();
        String expected = String.format("Product{id=%d, name='Chleb', category='Pieczywo', price=3.49}", product.getId());
        assertTrue(product.toString().contains("Chleb"));
        assertTrue(product.toString().contains("Pieczywo"));
        assertTrue(product.toString().contains("3.49"));
    }

    @Test
    void testSettersAndGetters() {
        Product product = new Product();
        product.setId(10);
        product.setName("Mleko");
        product.setCategory("Nabiał");
        product.setPrice(2.99);

        assertEquals(10, product.getId());
        assertEquals("Mleko", product.getName());
        assertEquals("Nabiał", product.getCategory());
        assertEquals(2.99, product.getPrice());
    }

    @Test
    void testSetPriceNegative() {
        Product product = createTestProduct();
        product.setPrice(-100.0);  // walidacja: nie powinno ustawić
        assertNotEquals(-100.0, product.getPrice());
    }

    @Test
    void testSetPriceZero() {
        Product product = createTestProduct();
        product.setPrice(0.0);
        assertEquals(0.0, product.getPrice());
    }

    @Test
    void testChangeNameAndCategory() {
        Product product = createTestProduct();
        product.setName("Jogurt");
        product.setCategory("Nabiał");

        assertEquals("Jogurt", product.getName());
        assertEquals("Nabiał", product.getCategory());
    }
}
