import org.example.sys.Employee;
import org.example.sys.Product;
import org.example.sys.Transaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    private Transaction transaction;
    private Employee employee;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setup() {
        transaction = new Transaction();

        employee = new Employee();
        employee.setName("Katarzyna");
        employee.setSurname("Nowak");

        product1 = new Product();
        product1.setName("Mleko");

        product2 = new Product();
        product2.setName("Chleb");
    }

    @Test
    void testSetAndGetPracownik() {
        transaction.setPracownik(employee);
        assertEquals(employee, transaction.getPracownik());
        assertEquals("Katarzyna", transaction.getPracownik().getName());
    }

    @Test
    void testSetAndGetData() {
        Date today = new Date();
        transaction.setData(today);
        assertEquals(today, transaction.getData());
    }

    @Test
    void testDefaultProduktyIsEmpty() {
        assertNotNull(transaction.getProdukty());
        assertTrue(transaction.getProdukty().isEmpty());
    }

    @Test
    void testAddProdukty() {
        Set<Product> produkty = new HashSet<>();
        produkty.add(product1);
        produkty.add(product2);

        transaction.setProdukty(produkty);
        assertEquals(2, transaction.getProdukty().size());
        assertTrue(transaction.getProdukty().contains(product1));
        assertTrue(transaction.getProdukty().contains(product2));
    }

    @Test
    void testOverwriteProdukty() {
        Set<Product> initial = new HashSet<>();
        initial.add(product1);
        transaction.setProdukty(initial);

        Set<Product> updated = new HashSet<>();
        updated.add(product2);
        transaction.setProdukty(updated);

        assertEquals(1, transaction.getProdukty().size());
        assertTrue(transaction.getProdukty().contains(product2));
        assertFalse(transaction.getProdukty().contains(product1));
    }
}
