package org.example.database;

import jakarta.persistence.*;
import pdf.WorkloadReportGenerator.EmployeeWorkload;

import java.time.LocalDate;
import java.util.List;

/**
 * Repozytorium do pobierania danych o obciążeniu pracowników.
 */
public class WorkloadRepository implements AutoCloseable {

    private final EntityManagerFactory emf;

    public WorkloadRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    /**
     * Pobiera dane do raportu obciążenia dla zadanego zakresu dat.
     *
     * @param startDate data początkowa
     * @param endDate   data końcowa
     * @return lista obiektów EmployeeWorkload
     */
    public List<EmployeeWorkload> getWorkloadData(LocalDate startDate, LocalDate endDate) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createNativeQuery(
                            // korzystamy z tekstowego bloku JDK 15+
                            """
                            SELECT
                              CONCAT(p.Imie, ' ', p.Nazwisko) AS employeeName,
                              p.Stanowisko                    AS department,
                              ROUND(SUM(TIME_TO_SEC(zp.czas_trwania_zmiany)) / 3600, 2) AS totalHours
                            FROM Pracownicy p
                            JOIN Zadania_Pracownicy zp ON zp.Id_pracownika = p.Id
                            JOIN Zadania z ON z.Id = zp.Id_zadania
                            WHERE z.Data BETWEEN :startDate AND :endDate
                            GROUP BY p.Id, p.Imie, p.Nazwisko, p.Stanowisko
                            """,
                            "EmployeeWorkloadMapping"
                    )
                    .setParameter("startDate", java.sql.Date.valueOf(startDate))
                    .setParameter("endDate",   java.sql.Date.valueOf(endDate))
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
