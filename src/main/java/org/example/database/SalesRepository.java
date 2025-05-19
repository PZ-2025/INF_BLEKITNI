package org.example.database;

import pdf.SalesReportGenerator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SalesRepository {
    private static final String URL = "jdbc:mysql://localhost:3306/StonkaDB?useSSL=false";
    private static final String USER = "twoj_user";
    private static final String PASSWORD = "twoje_haslo";

    public List<SalesReportGenerator.SalesRecord> getAllSales() {
        List<SalesReportGenerator.SalesRecord> sales = new ArrayList<>();
        String query = "SELECT t.Id AS transaction_id, t.Data AS transaction_date, " +
                "p.Nazwa AS product_name, p.Kategoria AS product_category, " +
                "1 AS quantity, p.Cena AS value " +
                "FROM Transakcje t " +
                "JOIN Transakcje_Produkty tp ON t.Id = tp.Id_transakcji " +
                "JOIN Produkty p ON tp.Id_produktu = p.Id";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int transactionId = rs.getInt("transaction_id");
                LocalDateTime transactionDate = rs.getDate("transaction_date").toLocalDate().atStartOfDay();
                String productName = rs.getString("product_name");
                String productCategory = rs.getString("product_category");
                int quantity = rs.getInt("quantity");
                double value = rs.getDouble("value");
                sales.add(new SalesReportGenerator.SalesRecord(transactionId, transactionDate, productName, productCategory, quantity, value));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }
}