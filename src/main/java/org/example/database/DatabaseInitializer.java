/*
 * Classname: DatabaseInitializer
 * Version information: 1.1
 * Date: 2025-04-11
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer implements ILacz {
    private static final Logger logger = LogManager.getLogger(DatabaseInitializer.class);

    public static void initialize() {
        // Połączenie do serwera MySQL (bez wskazania konkretnej bazy)
        logger.info("Rozpoczynam inicjalizację bazy danych");
        try (Connection conn = DriverManager.getConnection(MYSQL_SERVER_URL, MYSQL_USER, MYSQL_PASSWORD)) {
            logger.debug("Połączenie do serwera MySQL nawiązane: {}", MYSQL_SERVER_URL);
            logger.info("Tworzenie bazy danych: {}", DB_NAME);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
                logger.info("Baza danych '{}' utworzona lub już istnieje", DB_NAME);
            }

        } catch (Exception e) {
            logger.error("Błąd podczas tworzenia bazy danych '{}'", DB_NAME, e);
            return;
        }

        // Połączenie do właściwej bazy danych + import danych
        try (Connection conn = DriverManager.getConnection(MYSQL_DB_URL, MYSQL_USER, MYSQL_PASSWORD)) {
            logger.debug("Połączenie do bazy '{}' nawiązane: {}", DB_NAME, MYSQL_DB_URL);
            logger.info("Importowanie pliku SQL: src/main/resources/Stonka.sql");
            executeSqlScript(conn, "src/main/resources/Stonka.sql");
            logger.info("Import danych zakończony pomyślnie. Baza '{}' gotowa.", DB_NAME);
        } catch (Exception e) {
            logger.error("Błąd podczas importowania danych do bazy '{}'", DB_NAME, e);
        }
    }

    /**
     * Wykonuje skrypt SQL wczytany z pliku.
     *
     * @param conn     Połączenie z bazą danych
     * @param filePath Ścieżka do pliku SQL
     * @throws Exception w przypadku błędu odczytu lub wykonania zapytania
     */
    private static void executeSqlScript(Connection conn, String filePath) throws Exception {
        logger.debug("Wczytywanie skryptu SQL z pliku: {}", filePath);
        String sql = new String(Files.readAllBytes(Paths.get(filePath)));
        String[] statements = sql.split(";");

        try (Statement stmt = conn.createStatement()) {
            for (String s : statements) {
                String trimmed = s.trim();
                if (!trimmed.isEmpty()) {
                    logger.trace("Wykonuję zapytanie SQL: {}", trimmed.replaceAll("\\s+", " "));
                    stmt.execute(trimmed);
                }
            }
        }
        logger.debug("Wszystkie polecenia SQL z pliku {} zostały wykonane", filePath);
    }
}
