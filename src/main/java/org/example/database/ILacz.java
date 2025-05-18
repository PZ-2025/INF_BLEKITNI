/*
 * Classname: ILacz
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

public interface ILacz {

    String DB_NAME = "StonkaDB";
    String SQL_FILE = "src/main/resources/Stonka.sql";

    // Pierwszy URL – do stworzenia bazy danych
    String MYSQL_SERVER_URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";

    // Drugi URL – do połączenia z bazą danych
    String MYSQL_DB_URL = "jdbc:mysql://localhost:3306/StonkaDB?useSSL=false&serverTimezone=UTC";

    String MYSQL_USER = "root";
    String MYSQL_PASSWORD = "";
}
