/*
 * Classname: DatabaseInitializer
 * Version information: 1.2
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer implements ILacz {
    private static final Logger logger = LogManager.getLogger(DatabaseInitializer.class);

    /**
     * Tworzy bazę danych (jeśli nie istnieje) i importuje do niej struktury oraz dane
     * na podstawie pliku SQL.
     * <p>
     * Najpierw łączy się z serwerem MySQL bez wskazania konkretnej bazy,
     * tworzy bazę (DB_NAME), a następnie ponownie łączy się już z nią i
     * importuje zawartość pliku Stonka.sql.
     * </p>
     */
    public static void initialize() {
        logger.info("Rozpoczynam inicjalizację bazy danych");
        // Połączenie do serwera MySQL (bez wskazania konkretnej bazy)
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
            executeSqlScript(conn, "Stonka.sql");
            logger.info("Import danych zakończony pomyślnie. Baza '{}' gotowa.", DB_NAME);
        } catch (Exception e) {
            logger.error("Błąd podczas importowania danych do bazy '{}'", DB_NAME, e);
        }
    }

    /**
     * Wczytuje i wykonuje kolejne polecenia SQL zawarte w podanym pliku.
     *
     * @param conn     aktywne połączenie do bazy, w której mają zostać wykonane zapytania
     * @param filePath ścieżka do pliku SQL zawierającego skrypt DDL i DML
     * @throws Exception gdy odczyt pliku lub wykonanie zapytań zakończy się niepowodzeniem
     */
    private static void executeSqlScript(Connection conn, String filePath) throws Exception {
        logger.debug("Wczytywanie skryptu SQL z zasobów: {}", filePath);
        try (InputStream is = DatabaseInitializer.class.getResourceAsStream("/" + filePath)) {
            if (is == null) {
                throw new FileNotFoundException("Plik " + filePath + " nie został znaleziony w zasobach!");
            }
            String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
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
        }
        logger.debug("Wykonano wszystkie polecenia SQL z pliku {}", filePath);
    }
}
