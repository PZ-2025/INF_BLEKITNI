/*
 * Classname: WorkloadRepository
 * Version information: 1.0
 * Date: 2025-05-23
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import pdf.WorkloadReportGenerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repozytorium odpowiedzialne za pobieranie danych o obciążeniu pracowników.
 * Umożliwia pobranie listy obciążeń (sumy godzin pracy) wszystkich pracowników.
 * Korzysta z JDBC do komunikacji z bazą danych MySQL.
 */
public class WorkloadRepository {

    private static final Logger logger = Logger.getLogger(WorkloadRepository.class.getName());

    private static final String URL = "jdbc:mysql://localhost:3306/StonkaDB?useSSL=false";
    private static final String USER = "twoj_user";    // TODO: podmień na swojego użytkownika
    private static final String PASSWORD = "twoje_haslo";  // TODO: podmień na swoje hasło

    /**
     * Pobiera listę obciążeń pracowników z bazy danych.
     * Obciążenie jest obliczane jako liczba zadań pomnożona przez 8 godzin.
     *
     * @return lista obiektów EmployeeWorkload zawierających imię i nazwisko pracownika,
     *         dział oraz łączną liczbę godzin pracy; zwraca pustą listę w przypadku błędu.
     */
    public List<WorkloadReportGenerator.EmployeeWorkload> getAllWorkloadRecords() {
        logger.info("getAllWorkloadRecords() – rozpoczęcie pobierania rekordów obciążenia pracowników");

        List<WorkloadReportGenerator.EmployeeWorkload> workloads = new ArrayList<>();

        String query = """
            SELECT 
                p.Id,
                p.Imie,
                p.Nazwisko,
                p.Stanowisko,
                COUNT(zp.Id_zadania) * 8 AS total_hours
            FROM Pracownicy p
            LEFT JOIN Zadania_Pracownicy zp ON p.Id = zp.Id_pracownika
            GROUP BY p.Id, p.Imie, p.Nazwisko, p.Stanowisko
            ORDER BY p.Nazwisko, p.Imie
            """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            logger.fine("Połączenie z bazą danych nawiązane, wykonanie zapytania");

            while (rs.next()) {
                String employeeName = rs.getString("Imie") + " " + rs.getString("Nazwisko");
                String department = rs.getString("Stanowisko");
                double totalHours = rs.getDouble("total_hours");

                workloads.add(new WorkloadReportGenerator.EmployeeWorkload(employeeName, department, totalHours));
            }

            logger.info("getAllWorkloadRecords() – pobrano " + workloads.size() + " rekordów");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "getAllWorkloadRecords() – błąd podczas pobierania danych obciążenia", e);
        }

        return workloads;
    }
}
