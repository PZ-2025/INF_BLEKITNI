package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Transaction;
import pdf.SalesReportGenerator;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionRepository {

    private static final Logger logger = Logger.getLogger(TransactionRepository.class.getName());
    private final EntityManagerFactory emf;

    public TransactionRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("EntityManagerFactory created for TransactionRepository");
    }

    public void dodajTransakcje(Transaction transakcja) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(transakcja);
            tx.commit();
            logger.info("Dodano transakcję o ID: " + transakcja.getId());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Błąd podczas dodawania transakcji: " + e.getMessage(), e);
            if (tx.isActive()) {
                tx.rollback();
                logger.info("Transakcja wycofana");
            }
        } finally {
            em.close();
            logger.fine("EntityManager zamknięty po dodaniu transakcji");
        }
    }

    public List<Transaction> getTransactionsByDate(LocalDate date) {
        EntityManager em = emf.createEntityManager();
        try {
            Date startDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.data >= :startDate AND t.data < :endDate",
                            Transaction.class)
                    .setParameter("startDate", startDate)
                    .setParameter("endDate", endDate)
                    .getResultList();

            logger.info("Pobrano transakcje z dnia " + date + ", liczba: " + list.size());
            return list;
        } finally {
            em.close();
            logger.fine("EntityManager zamknięty po getTransactionsByDate");
        }
    }

    public Transaction znajdzTransakcjePoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            Transaction transakcja = em.find(Transaction.class, id);
            logger.info("Znaleziono transakcję o ID: " + id + " -> " + (transakcja != null));
            return transakcja;
        } finally {
            em.close();
            logger.fine("EntityManager zamknięty po znajdzTransakcjePoId");
        }
    }

    public List<Transaction> pobierzWszystkieTransakcje() {
        EntityManager em = emf.createEntityManager();
        try {
            List<Transaction> list = em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
            logger.info("Pobrano wszystkie transakcje, liczba: " + list.size());
            return list;
        } finally {
            em.close();
            logger.fine("EntityManager zamknięty po pobierzWszystkieTransakcje");
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
                logger.info("Usunięto transakcję o ID: " + id);
            } else {
                logger.warning("Nie znaleziono transakcji do usunięcia o ID: " + id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Błąd podczas usuwania transakcji: " + e.getMessage(), e);
            if (tx.isActive()) {
                tx.rollback();
                logger.info("Transakcja wycofana");
            }
        } finally {
            em.close();
            logger.fine("EntityManager zamknięty po usunięciu transakcji");
        }
    }

    public void aktualizujTransakcje(Transaction transakcja) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(transakcja);
            tx.commit();
            logger.info("Zaktualizowano transakcję o ID: " + transakcja.getId());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Błąd podczas aktualizacji transakcji: " + e.getMessage(), e);
            if (tx.isActive()) {
                tx.rollback();
                logger.info("Transakcja wycofana");
            }
        } finally {
            em.close();
            logger.fine("EntityManager zamknięty po aktualizacji transakcji");
        }
    }

    public List<Transaction> getTransactionsByPeriod(LocalDate date, SalesReportGenerator.PeriodType periodType) {
        EntityManager em = emf.createEntityManager();
        try {
            LocalDate startDate, endDate;

            switch (periodType) {
                case DAILY -> {
                    startDate = date;
                    endDate = date;
                }
                case MONTHLY -> {
                    startDate = date.withDayOfMonth(1);
                    endDate = startDate.plusMonths(1).minusDays(1);
                }
                case YEARLY -> {
                    startDate = date.withDayOfYear(1);
                    endDate = startDate.plusYears(1).minusDays(1);
                }
                default -> throw new IllegalArgumentException("Nieprawidłowy typ okresu: " + periodType);
            }

            Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.data >= :startDate AND t.data < :endDate",
                            Transaction.class)
                    .setParameter("startDate", start)
                    .setParameter("endDate", end)
                    .getResultList();

            logger.info("Pobrano transakcje z okresu " + periodType + " od " + startDate + " do " + endDate + ", liczba: " + list.size());
            return list;
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Błąd w getTransactionsByPeriod: " + e.getMessage(), e);
            throw e;
        } finally {
            em.close();
            logger.fine("EntityManager zamknięty po getTransactionsByPeriod");
        }
    }
}