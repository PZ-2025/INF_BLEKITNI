package org.example.database;

import pdf.WorkloadReportGenerator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkloadRepository {
    private static final String URL = "jdbc:mysql://localhost:3306/StonkaDB?useSSL=false";
    private static final String USER = "twoj_user"; // Zastąp swoim użytkownikiem
    private static final String PASSWORD = "twoje_haslo"; // Zastąp swoim hasłem

    public List<WorkloadReportGenerator.EmployeeWorkload> getAllWorkloadRecords() {
        List<WorkloadReportGenerator.EmployeeWorkload> workloads = new ArrayList<>();
        String query = "SELECT p.Id, p.Imie, p.Nazwisko, p.Stanowisko, COUNT(zp.Id_zadania) * 8 AS total_hours " +
                "FROM Pracownicy p " +
                "LEFT JOIN Zadania_Pracownicy zp ON p.Id = zp.Id_pracownika " +
                "GROUP BY p.Id";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String employeeName = rs.getString("Imie") + " " + rs.getString("Nazwisko");
                String department = rs.getString("Stanowisko");
                double totalHours = rs.getDouble("total_hours");
                workloads.add(new WorkloadReportGenerator.EmployeeWorkload(employeeName, department, totalHours));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workloads;
    }
}