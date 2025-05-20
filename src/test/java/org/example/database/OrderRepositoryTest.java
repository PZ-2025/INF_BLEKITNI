package org.example.database;

import org.example.sys.Employee;
import org.example.sys.Product;
import org.example.sys.Order;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrderRepositoryTest {

    private static OrderRepository   orderRepo;
    private static ProductRepository productRepo;
    private static UserRepository    userRepo;

    private static Product  product;
    private static Employee employee;
    private static Order    order;

    /** data w dwóch postaciach — przydaje się w różnych miejscach */
    private static LocalDate localOrderDate;
    private static Date      utilOrderDate;

    @BeforeAll
    static void setup() {
        orderRepo   = new OrderRepository();
        productRepo = new ProductRepository();
        userRepo    = new UserRepository();

        // 1) produkt testowy
        product = new Product("Testowy produkt", "Testowa kategoria", 5.99);
        productRepo.dodajProdukt(product);

        // 2) dowolny istniejący pracownik
        List<Employee> emps = userRepo.pobierzWszystkichPracownikow();
        assertFalse(emps.isEmpty(), "Brak pracowników w bazie!");
        employee = emps.get(0);

        // 3) data zamówienia
        localOrderDate = LocalDate.of(2025, 5, 12);
        utilOrderDate  = Date.from(localOrderDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Test @org.junit.jupiter.api.Order(1)
    void testAddOrder() {
        order = new Order();
        order.setProdukt(product);
        order.setPracownik(employee);
        order.setIlosc(10);
        order.setCena(new BigDecimal("59.90"));
        order.setData(utilOrderDate);                // <-- java.util.Date

        assertDoesNotThrow(() -> orderRepo.dodajZamowienie(order));
        assertTrue(order.getId() > 0);
    }

    @Test @org.junit.jupiter.api.Order(2)
    void testFindAllAndUpdate() {
        assertTrue(
                orderRepo.pobierzWszystkieZamowienia()
                        .stream().anyMatch(o -> o.getId() == order.getId()));

        order.setIlosc(20);
        order.setCena(new BigDecimal("119.80"));
        assertDoesNotThrow(() -> orderRepo.aktualizujZamowienie(order));

        Order re = orderRepo.znajdzZamowieniePoId(order.getId());
        assertNotNull(re);
        assertEquals(20, re.getIlosc());
        assertEquals(new BigDecimal("119.80"), re.getCena());
    }

    @Test @org.junit.jupiter.api.Order(3)
    void testQueries() {
        // ID produktu
        assertTrue(
                orderRepo.znajdzZamowieniaPoIdProduktu(product.getId())
                        .stream().allMatch(o -> o.getProdukt().getId() == product.getId()));

        // ID pracownika
        assertTrue(
                orderRepo.znajdzZamowieniaPoIdPracownika(employee.getId())
                        .stream().allMatch(o -> o.getPracownik().getId() == employee.getId()));

        // dokładna data
        assertTrue(
                orderRepo.znajdzZamowieniaPoDacie(localOrderDate)
                        .stream().allMatch(o ->
                                o.getData().toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                        .equals(localOrderDate)));

        // zakres dat
        assertTrue(
                orderRepo.znajdzZamowieniaWZakresieDat(
                                localOrderDate.minusDays(1),
                                localOrderDate.plusDays(1))
                        .stream().anyMatch(o -> o.getId() == order.getId()));

        // minimalna ilość
        assertTrue(
                orderRepo.znajdzZamowieniaZMinimalnaIloscia(20)
                        .stream().allMatch(o -> o.getIlosc() >= 20));

        // przedział cenowy
        BigDecimal min = new BigDecimal("100.00");
        BigDecimal max = new BigDecimal("200.00");
        assertTrue(
                orderRepo.znajdzZamowieniaWPrzedzialeCenowym(min, max)
                        .stream().allMatch(o ->
                                o.getCena().compareTo(min) >= 0 &&
                                        o.getCena().compareTo(max) <= 0));
    }

    @Test @org.junit.jupiter.api.Order(4)
    void testDelete() {
        assertDoesNotThrow(() -> orderRepo.usunZamowienie(order.getId()));

        assertNull(orderRepo.znajdzZamowieniePoId(order.getId()));
        assertTrue(orderRepo.pobierzWszystkieZamowienia()
                .stream().noneMatch(o -> o.getId() == order.getId()));
    }

    @AfterAll
    static void closeAll() {
        orderRepo.close();
        productRepo.close();
        userRepo.close();
    }
}
