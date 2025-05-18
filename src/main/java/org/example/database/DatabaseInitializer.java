/*
 * Classname: DatabaseInitializer
 * Version information: 1.2
 * Date: 2025-05-17
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;

/**
 * Klasa odpowiedzialna za inicjalizację bazy danych.
 * Tworzy bazę danych jeśli nie istnieje i importuje strukturę z pliku SQL.
 */
public class DatabaseInitializer implements ILacz {

    /**
     * Inicjalizuje bazę danych, tworząc ją jeśli nie istnieje i importując strukturę z pliku SQL.
     *
     * @throws SQLException jeśli wystąpi błąd podczas operacji na bazie danych
     * @throws IOException jeśli wystąpi błąd podczas odczytu pliku SQL
     */
    public static void initialize() throws SQLException, IOException {
        Connection serverConn = null;
        Statement serverStmt = null;
        Connection dbConn = null;

        try {
            // Rejestracja sterownika JDBC
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Nie znaleziono sterownika JDBC MySQL: " + e.getMessage(), e);
            }

            // Krok 1: Połączenie do serwera MySQL (bez wskazania konkretnej bazy)
            try {
                serverConn = DriverManager.getConnection(MYSQL_SERVER_URL, MYSQL_USER, MYSQL_PASSWORD);
            } catch (SQLException e) {
                throw new SQLException("Nie można połączyć się z serwerem MySQL: " + e.getMessage(), e);
            }

            // Krok 2: Tworzenie bazy danych jeśli nie istnieje
            try {
                serverStmt = serverConn.createStatement();
                serverStmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            } catch (SQLException e) {
                throw new SQLException("Nie można utworzyć bazy danych " + DB_NAME + ": " + e.getMessage(), e);
            }

            // Krok 3: Połączenie do utworzonej bazy danych
            try {
                dbConn = DriverManager.getConnection(MYSQL_DB_URL, MYSQL_USER, MYSQL_PASSWORD);
            } catch (SQLException e) {
                throw new SQLException("Nie można połączyć się z bazą danych " + DB_NAME + ": " + e.getMessage(), e);
            }

            // Krok 4: Import struktury z pliku SQL
            try {
                executeSqlScript(dbConn, "src/main/resources/Stonka.sql");
            } catch (IOException e) {
                throw new IOException("Nie można odczytać pliku SQL: " + e.getMessage(), e);
            } catch (SQLException e) {
                throw new SQLException("Błąd podczas wykonywania skryptu SQL: " + e.getMessage(), e);
            }

        } finally {
            // Zamknięcie zasobów w odwrotnej kolejności ich tworzenia
            closeQuietly(serverStmt);
            closeQuietly(serverConn);
            closeQuietly(dbConn);
        }
    }

    /**
     * Wykonuje skrypt SQL wczytany z pliku.
     *
     * @param conn     Połączenie z bazą danych
     * @param filePath Ścieżka do pliku SQL
     * @throws SQLException w przypadku błędu wykonania zapytania
     * @throws IOException w przypadku błędu odczytu pliku
     */
    private static void executeSqlScript(Connection conn, String filePath) throws SQLException, IOException {
        String sql = new String(Files.readAllBytes(Paths.get(filePath)));
        String[] statements = sql.split(";");

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            for (String s : statements) {
                String trimmed = s.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        } finally {
            closeQuietly(stmt);
        }
    }

    /**
     * Bezpiecznie zamyka obiekt Statement, ignorując ewentualne wyjątki.
     *
     * @param stmt obiekt Statement do zamknięcia
     */
    private static void closeQuietly(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                // Ignoruj błędy podczas zamykania
                System.err.println("Ostrzeżenie: Nie można zamknąć obiektu Statement: " + e.getMessage());
            }
        }
    }

    /**
     * Bezpiecznie zamyka obiekt Connection, ignorując ewentualne wyjątki.
     *
     * @param conn obiekt Connection do zamknięcia
     */
    private static void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignoruj błędy podczas zamykania
                System.err.println("Ostrzeżenie: Nie można zamknąć połączenia z bazą danych: " + e.getMessage());
            }
        }
    }
}