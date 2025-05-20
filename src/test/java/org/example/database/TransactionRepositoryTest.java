package org.example.database;

import org.example.sys.Employee;
import org.example.sys.Transaction;
import org.junit.jupiter.api.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionRepositoryTest {

    private static TransactionRepository transactionRepo;
    private static UserRepository        userRepo;

    private static Employee    employee;
    private static Transaction tx1, tx2;
    private static Date        exactDate, fromDate, toDate;

    @BeforeAll
    static void setup() throws Exception {
        transactionRepo = new TransactionRepository();
        userRepo        = new UserRepository();

        // prepare dates
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        exactDate = sdf.parse("2025-05-12");
        fromDate  = sdf.parse("2025-05-10");
        toDate    = sdf.parse("2025-05-15");

        // pick an existing employee
        List<Employee> emps = userRepo.pobierzWszystkichPracownikow();
        assertFalse(emps.isEmpty(), "At least one employee must exist");
        employee = emps.get(0);
    }

    @Test
    @Order(1)
    void testAddTransactions() {
        tx1 = new Transaction();
        tx1.setPracownik(employee);
        tx1.setData(exactDate);

        tx2 = new Transaction();
        tx2.setPracownik(employee);
        tx2.setData(toDate);

        assertDoesNotThrow(() -> transactionRepo.dodajTransakcje(tx1), "Should add tx1 without exception");
        assertTrue(tx1.getId() > 0, "tx1 should receive an ID");

        assertDoesNotThrow(() -> transactionRepo.dodajTransakcje(tx2), "Should add tx2 without exception");
        assertTrue(tx2.getId() > 0, "tx2 should receive an ID");
    }

    @Test
    @Order(2)
    void testFindAllAndFindById() {
        List<Transaction> all = transactionRepo.pobierzWszystkieTransakcje();
        assertTrue(all.stream().anyMatch(t -> t.getId() == tx1.getId()), "tx1 must be in all transactions");
        assertTrue(all.stream().anyMatch(t -> t.getId() == tx2.getId()), "tx2 must be in all transactions");

        Transaction loaded = transactionRepo.znajdzTransakcjePoId(tx1.getId());
        assertNotNull(loaded, "Should find tx1 by ID");
        assertEquals(exactDate, loaded.getData(), "Loaded tx1 should have correct date");
    }

    @Test
    @Order(3)
    void testQueries() {
        // by employee
        List<Transaction> byEmp = transactionRepo.znajdzPoPracowniku(employee.getId());
        assertTrue(byEmp.stream().allMatch(t -> t.getPracownik().getId() == employee.getId()),
                "All results must belong to the same employee");

        // by exact date
        List<Transaction> byExact = transactionRepo.znajdzPoDacie(exactDate);
        assertTrue(byExact.stream().allMatch(t -> t.getData().equals(exactDate)),
                "All results must match the exact date");

        // by date range
        List<Transaction> inRange = transactionRepo.znajdzPoZakresieDat(fromDate, toDate);
        assertTrue(inRange.stream().anyMatch(t -> t.getId() == tx1.getId()),
                "tx1 should appear in the date range");
        assertTrue(inRange.stream().anyMatch(t -> t.getId() == tx2.getId()),
                "tx2 should appear in the date range");
    }

    @Test
    @Order(4)
    void testDelete() {
        assertDoesNotThrow(() -> transactionRepo.usunTransakcje(tx1.getId()),
                "Should delete tx1 without exception");

        assertNull(transactionRepo.znajdzTransakcjePoId(tx1.getId()),
                "Deleted transaction should no longer be found");

        List<Transaction> allAfter = transactionRepo.pobierzWszystkieTransakcje();
        assertTrue(allAfter.stream().noneMatch(t -> t.getId() == tx1.getId()),
                "Deleted transaction must not appear in list");
    }

    @AfterAll
    static void tearDown() {
        transactionRepo.close();
        userRepo.close();
    }
}
