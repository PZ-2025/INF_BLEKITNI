package org.example.database;

import jakarta.persistence.*;
import pdf.WorkloadReportGenerator.EmployeeWorkload;

import java.time.LocalDate;
import java.util.List;

/**
 * Repozytorium do pobierania danych o obciążeniu pracowników.
 */
public class WorkloadRepository implements AutoCloseable {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("myPU");

    /**
     * Zwraca listę EmployeeWorkload dla pracowników, którzy w okresie
     * {@code startDate … endDate} mieli przypisane zadania.
     *
     * Suma godzin jest obliczana w następujący sposób:
     * <pre>
     *   totalHours = Σ  COALESCE(zp.czas_trwania_zmiany , z.czas_trwania_zmiany)
     * </pre>
     * gdzie:
     *  • {@code zp} – tabela łącznikowa Zadania_Pracownicy
     *  • {@code z } – tabela Zadania
     *
     * @param startDate  data początkowa (włącznie)
     * @param endDate    data końcowa    (włącznie)
     */
    public List<EmployeeWorkload> getWorkloadData(LocalDate startDate,
                                                  LocalDate endDate) {

        EntityManager em = emf.createEntityManager();
        try {
            return em.createNativeQuery(
                            // language=SQL
                            """
                            SELECT
                              CONCAT(p.Imie, ' ', p.Nazwisko)                                        AS employeeName,
                              p.Stanowisko                                                           AS department,
                              ROUND(
                                      SUM(
                                          TIME_TO_SEC(
                                              COALESCE(zp.czas_trwania_zmiany , z.czas_trwania_zmiany)
                                          )
                                      ) / 3600
                                  , 2)                                                               AS totalHours
                            FROM   Pracownicy          p
                            JOIN   Zadania_Pracownicy  zp ON zp.Id_pracownika = p.Id
                            JOIN   Zadania             z  ON z.Id            = zp.Id_zadania
                            WHERE  z.Data BETWEEN :start AND :end
                            GROUP  BY p.Id, p.Imie, p.Nazwisko, p.Stanowisko
                            ORDER  BY p.Nazwisko, p.Imie
                            """,
                            "EmployeeWorkloadMapping")
                    .setParameter("start", java.sql.Date.valueOf(startDate))
                    .setParameter("end",   java.sql.Date.valueOf(endDate))
                    .getResultList();

        } finally {
            em.close();
        }
    }

    @Override
    public void close() {
        if (emf.isOpen()) emf.close();
    }
}
