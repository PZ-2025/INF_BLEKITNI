/*
 * Classname: ILacz
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

/**
 * Interfejs definiujący parametry połączenia do bazy oraz
 * metodę do wczytywania i wykonywania skryptu SQL.
 */
public interface ILacz {

    /** Nazwa bazy danych */
    String DB_NAME = "StonkaDB";

    /** Ścieżka do pliku .sql ze skryptem DDL i DML */
    String SQL_FILE = "src/main/resources/Stonka.sql";

    /** URL do serwera MySQL (bez wskazania konkretnej bazy) */
    String MYSQL_SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";

    /** URL do połączenia z docelową bazą danych */
    String MYSQL_DB_URL = "jdbc:mysql://localhost:3306/StonkaDB?useSSL=false&serverTimezone=UTC";

    /** Użytkownik bazy MySQL */
    String MYSQL_USER = "root";

    /** Hasło użytkownika bazy MySQL */
    String MYSQL_PASSWORD = "";
}
