package org.example.database;

import org.example.sys.Product;
import org.example.sys.Warehouse;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WarehouseRepositoryTest {

    private static ProductRepository   productRepo;
    private static WarehouseRepository warehouseRepo;

    private static Product   testProduct;
    private static Warehouse testWarehouse;

    @BeforeAll
    static void setup() {
        productRepo   = new ProductRepository();
        warehouseRepo = new WarehouseRepository();
    }

    @Test
    @Order(1)
    void testAddProductAndWarehouse() {
        // 1. add a product for FK
        testProduct = new Product("Jogurt truskawkowy", "Nabiał", 3.49);
        assertDoesNotThrow(() -> productRepo.dodajProdukt(testProduct),
                "Should add product without exception");
        assertTrue(testProduct.getId() > 0, "Product should have an ID");

        // 2. add stock record
        testWarehouse = new Warehouse(testProduct, 120);
        assertDoesNotThrow(() -> warehouseRepo.dodajStanMagazynowy(testWarehouse),
                "Should add warehouse record without exception");
        // no generated ID here (uses product PK), so ensure getProdukt().getId() matches
        assertEquals(testProduct.getId(), testWarehouse.getProdukt().getId());
    }

    @Test
    @Order(2)
    void testFindAllAndUpdate() {
        // 3. fetch all stock records
        List<Warehouse> all = warehouseRepo.pobierzWszystkieStany();
        assertTrue(all.stream().anyMatch(w -> w.getProdukt().getId() == testProduct.getId()),
                "New warehouse record should appear in list");

        // 4. update quantity
        testWarehouse.setIlosc(100);
        assertDoesNotThrow(() -> warehouseRepo.aktualizujStan(testWarehouse),
                "Should update warehouse record without exception");

        Warehouse reloaded = warehouseRepo.znajdzStanPoIdProduktu(testProduct.getId());
        assertNotNull(reloaded, "Reloaded record must not be null");
        assertEquals(100, reloaded.getIlosc(), "Quantity should be updated to 100");
    }

    @Test
    @Order(3)
    void testQueries() {
        // quantity == 100
        List<Warehouse> eq100 = warehouseRepo.znajdzPoIlosci(100);
        assertTrue(eq100.stream()
                .allMatch(w -> w.getIlosc() == 100), "All must have quantity 100");

        // quantity < 110
        List<Warehouse> lt110 = warehouseRepo.znajdzPoIlosciMniejszejNiz(110);
        assertTrue(lt110.stream()
                .allMatch(w -> w.getIlosc() < 110), "All must have quantity < 110");

        // quantity > 50
        List<Warehouse> gt50 = warehouseRepo.znajdzPoIlosciWiekszejNiz(50);
        assertTrue(gt50.stream()
                .allMatch(w -> w.getIlosc() > 50), "All must have quantity > 50");

        // between 80 and 120
        List<Warehouse> between = warehouseRepo.znajdzPoIlosciWMiedzy(80, 120);
        assertTrue(between.stream()
                        .anyMatch(w -> w.getProdukt().getId() == testProduct.getId()),
                "Record should appear in [80,120] range");
    }

    @Test
    @Order(4)
    void testDelete() {
        assertDoesNotThrow(() -> warehouseRepo.usunStan(testProduct.getId()),
                "Should delete warehouse record without exception");

        Warehouse gone = warehouseRepo.znajdzStanPoIdProduktu(testProduct.getId());
        assertNull(gone, "Deleted warehouse record should no longer be found");

        List<Warehouse> allAfter = warehouseRepo.pobierzWszystkieStany();
        assertTrue(allAfter.stream()
                        .noneMatch(w -> w.getProdukt().getId() == testProduct.getId()),
                "Deleted record must not appear in list");
    }

    @AfterAll
    static void tearDown() {
        productRepo.close();
        warehouseRepo.close();
    }
}
