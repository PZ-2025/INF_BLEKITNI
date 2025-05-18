/*
 * Classname: TransactionRepository
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Transaction;
import pdf.SalesReportGenerator;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class TransactionRepository {

    private final EntityManagerFactory emf;

    public TransactionRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    public void dodajTransakcje(Transaction transakcja) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(transakcja);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public List<Transaction> getTransactionsByDate(LocalDate date) {
        EntityManager em = emf.createEntityManager();
        try {
            // Konwersja LocalDate na Date (bez godziny - początek dnia i koniec dnia)
            Date startDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

            return em.createQuery("SELECT t FROM Transaction t WHERE t.data >= :startDate AND t.data < :endDate", Transaction.class)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Transaction znajdzTransakcjePoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Transaction.class, id);
        } finally {
            em.close();
        }
    }

    public List<Transaction> pobierzWszystkieTransakcje() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void usunTransakcje(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction transakcja = em.find(Transaction.class, id);
            if (transakcja != null) {
                em.remove(transakcja);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void aktualizujTransakcje(Transaction transakcja) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(transakcja);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    /**
     * Pobiera transakcje z określonego okresu.
     *
     * @param date data dla której pobieramy transakcje
     * @param periodType typ okresu (dzienny, miesięczny, roczny)
     * @return lista transakcji z danego okresu
     */
    public List<Transaction> getTransactionsByPeriod(LocalDate date, SalesReportGenerator.PeriodType periodType) {
        EntityManager em = emf.createEntityManager();
        try {
            LocalDate startDate, endDate;

            switch (periodType) {
                case DAILY:
                    startDate = date;
                    endDate = date;
                    break;
                case MONTHLY:
                    startDate = date.withDayOfMonth(1);
                    endDate = startDate.plusMonths(1).minusDays(1);
                    break;
                case YEARLY:
                    startDate = date.withDayOfYear(1);
                    endDate = startDate.plusYears(1).minusDays(1);
                    break;
                default:
                    throw new IllegalArgumentException("Nieprawidłowy typ okresu");
            }

            // Konwersja LocalDate na Date
            Date startDateUtil = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDateUtil = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1).toInstant());

            return em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.data >= :startDate AND t.data <= :endDate",
                            Transaction.class)
                    .setParameter("startDate", startDateUtil)
                    .setParameter("endDate", endDateUtil)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
