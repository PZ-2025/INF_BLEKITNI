import org.example.sys.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product createTestProduct() {
        return new Product("Chleb", "Pieczywo", 3.49);
    }

    @Test
    void testParameterizedConstructor() {
        Product product = createTestProduct();

        assertEquals("Chleb", product.getName());
        assertEquals("Pieczywo", product.getCategory());
        assertEquals(BigDecimal.valueOf(3.49), product.getPrice());
    }

    @Test
    void testToString() {
        Product product = createTestProduct();
        String s = product.toString();
        assertTrue(s.contains("Chleb"));
        assertTrue(s.contains("Pieczywo"));
        assertTrue(s.contains("3.49"));
    }

    @Test
    void testSettersAndGetters() {
        Product product = new Product();
        product.setName("Mleko");
        product.setCategory("Nabiał");
        product.setPrice(BigDecimal.valueOf(2.99));

        assertEquals("Mleko", product.getName());
        assertEquals("Nabiał", product.getCategory());
        assertEquals(BigDecimal.valueOf(2.99), product.getPrice());
    }

    @Test
    void testSetPriceNegative() {
        Product product = createTestProduct();
        product.setPrice(BigDecimal.valueOf(-100.0));  // walidacja: nie powinno się ustawić
        assertNotEquals(BigDecimal.valueOf(-100.0), product.getPrice());
    }

    @Test
    void testSetPriceZero() {
        Product product = createTestProduct();
        product.setPrice(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, product.getPrice());
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