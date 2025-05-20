package org.example.database;

import org.example.sys.Product;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductRepositoryTest {

    private static ProductRepository repo;
    private static Product p1, p2, p3;

    @BeforeAll
    static void setup() {
        repo = new ProductRepository();
        // create three products
        p1 = new Product("Masło",   "Nabiał",      6.99);
        p2 = new Product("Szampon", "Kosmetyki",  12.49);
        p3 = new Product("Mleko",   "Nabiał",      4.50);

        assertDoesNotThrow(() -> {
            repo.dodajProdukt(p1);
            repo.dodajProdukt(p2);
            repo.dodajProdukt(p3);
        }, "Should add three products without exception");

        assertTrue(p1.getId() > 0, "p1 should have an ID");
        assertTrue(p2.getId() > 0, "p2 should have an ID");
        assertTrue(p3.getId() > 0, "p3 should have an ID");
    }

    @Test
    @Order(1)
    void testFindAll() {
        List<Product> all = repo.pobierzWszystkieProdukty();
        // we should have at least our three
        assertTrue(all.size() >= 3, "Should find at least 3 products");
        assertTrue(all.stream().anyMatch(p -> p.getId() == p1.getId()));
        assertTrue(all.stream().anyMatch(p -> p.getId() == p2.getId()));
        assertTrue(all.stream().anyMatch(p -> p.getId() == p3.getId()));
    }

    @Test
    @Order(2)
    void testFindByIdAndUpdate() {
        Product found = repo.znajdzProduktPoId(p1.getId());
        assertNotNull(found, "Should find p1 by its ID");
        assertEquals("Masło", found.getName());

        // update its category
        found.setCategory("Produkty spożywcze");
        assertDoesNotThrow(() -> repo.aktualizujProdukt(found));
        Product reloaded = repo.znajdzProduktPoId(p1.getId());
        assertEquals("Produkty spożywcze", reloaded.getCategory());
    }

    @Test
    @Order(3)
    void testUpdatePrice() {
        // update price of p2
        assertDoesNotThrow(() ->
                repo.aktualizujCeneProduktu(p2.getId(), BigDecimal.valueOf(10.99))
        );
        Product reloaded = repo.znajdzProduktPoId(p2.getId());
        assertEquals(BigDecimal.valueOf(10.99), reloaded.getPrice());
    }


    @Test
    @Order(4)
    void testQueries() {
        // exact category match "Nabiał"
        List<Product> dairy = repo.pobierzProduktyPoKategorii("Nabiał");
        assertTrue(dairy.stream().anyMatch(p -> p.getId() == p3.getId()));
        assertFalse(dairy.stream().anyMatch(p -> p.getId() == p1.getId()));

        // price range [5.00, 11.00]
        List<Product> range = repo.pobierzProduktyWZakresieCenowym(
                BigDecimal.valueOf(5.00),
                BigDecimal.valueOf(11.00)
        );
        assertTrue(range.stream().anyMatch(p -> p.getId() == p1.getId()));
        assertTrue(range.stream().anyMatch(p -> p.getId() == p2.getId()));
        assertFalse(range.stream().anyMatch(p -> p.getId() == p3.getId()));

        // delete by category
        int removed = repo.usunProduktyZKategorii("Produkty spożywcze");
        assertTrue(removed >= 1, "Should remove at least the p1 retagged earlier");

        // delete single p2
        assertDoesNotThrow(() -> repo.usunProdukt(p2.getId()));
        assertNull(repo.znajdzProduktPoId(p2.getId()), "p2 should be deleted");
    }


    @Test
    @Order(5)
    void testAdditionalSearches() {
        // name contains "M"
        List<Product> nameM = repo.znajdzPoNazwie("m");
        assertTrue(nameM.stream().anyMatch(p -> p.getId() == p3.getId()));

        // exact price 4.50
        List<Product> priceExact = repo.znajdzPoCenieDokladnej(BigDecimal.valueOf(4.50));
        assertTrue(priceExact.stream().anyMatch(p -> p.getId() == p3.getId()));

        // price >= 6.00
        List<Product> priceMin = repo.znajdzPoCenieMin(BigDecimal.valueOf(6.00));
        assertTrue(
                priceMin.stream()
                        .anyMatch(p -> p.getPrice().compareTo(BigDecimal.valueOf(6.00)) >= 0)
        );

        // price <= 11.00
        List<Product> priceMax = repo.znajdzPoCenieMax(BigDecimal.valueOf(11.00));
        assertTrue(
                priceMax.stream()
                        .allMatch(p -> p.getPrice().compareTo(BigDecimal.valueOf(11.00)) <= 0)
        );
    }


    @AfterAll
    static void tearDown() {
        repo.close();
    }
}
