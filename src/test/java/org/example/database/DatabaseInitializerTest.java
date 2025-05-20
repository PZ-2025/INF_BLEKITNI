package org.example.database;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseInitializerTest {

    private static final String DB_NAME = "StonkaDB";
    private static final String TEST_URL = "jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String ROOT_URL = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    @Test
    @Order(1)
    void testInitialize_createsDatabaseAndTables() throws Exception {
        //uruchomienie inicjalizacji
        DatabaseInitializer.initialize();

        //połączenie z utworzoną bazą
        try (Connection conn = DriverManager.getConnection(TEST_URL, USER, PASS)) {
            Statement stmt = conn.createStatement();

            // Sprawdzenie czy jakaś przykładowa tabela istnieje
            ResultSet rs = stmt.executeQuery("SELECT * FROM Produkty");
            assertTrue(rs.next(), "Tabela 'produkty' powinna istnieć po inicjalizacji.");
        }
    }

    @Test
    @Order(2)
    void testInitialize_isIdempotent() {
        assertDoesNotThrow(DatabaseInitializer::initialize,
                "Ponowne uruchomienie initialize() nie powinno rzucać wyjątków.");
    }
}