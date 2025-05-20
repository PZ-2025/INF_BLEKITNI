package org.example.database;

import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Transaction;

import java.util.Date;
import java.util.List;

public class TransactionRepository {
    private static final Logger logger = LogManager.getLogger(TransactionRepository.class);
    private final EntityManagerFactory emf;

    public TransactionRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono TransactionRepository, EMF={}", emf);
    }

    /** Dodaje nową transakcję. */
    public void dodajTransakcje(Transaction transakcja) {
        logger.debug("dodajTransakcje() – start, transakcja={}", transakcja);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(transakcja);
            tx.commit();
            logger.info("dodajTransakcje() – transakcja dodana: {}", transakcja);
        } catch (Exception e) {
            logger.error("dodajTransakcje() – błąd podczas dodawania transakcji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("dodajTransakcje() – EntityManager zamknięty");
        }
    }

    /** Pobiera transakcję po jej ID. */
    public Transaction znajdzTransakcjePoId(int id) {
        logger.debug("znajdzTransakcjePoId() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Transaction t = em.find(Transaction.class, id);
            logger.info("znajdzTransakcjePoId() – znaleziono: {}", t);
            return t;
        } catch (Exception e) {
            logger.error("znajdzTransakcjePoId() – błąd podczas pobierania id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzTransakcjePoId() – EntityManager zamknięty");
        }
    }

    /** Pobiera wszystkie transakcje. */
    public List<Transaction> pobierzWszystkieTransakcje() {
        logger.debug("pobierzWszystkieTransakcje() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Transaction> list = em.createQuery("SELECT t FROM Transaction t", Transaction.class)
                    .getResultList();
            logger.info("pobierzWszystkieTransakcje() – pobrano {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzWszystkieTransakcje() – błąd podczas pobierania transakcji", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzWszystkieTransakcje() – EntityManager zamknięty");
        }
    }

    /** Usuwa transakcję o danym ID. */
    public void usunTransakcje(int id) {
        logger.debug("usunTransakcje() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction t = em.find(Transaction.class, id);
            if (t != null) {
                em.remove(t);
                logger.info("usunTransakcje() – usunięto transakcję: {}", t);
            } else {
                logger.warn("usunTransakcje() – brak transakcji o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("usunTransakcje() – błąd podczas usuwania id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("usunTransakcje() – EntityManager zamknięty");
        }
    }

    /** Aktualizuje istniejącą transakcję. */
    public void aktualizujTransakcje(Transaction transakcja) {
        logger.debug("aktualizujTransakcje() – start, transakcja={}", transakcja);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(transakcja);
            tx.commit();
            logger.info("aktualizujTransakcje() – transakcja zaktualizowana: {}", transakcja);
        } catch (Exception e) {
            logger.error("aktualizujTransakcje() – błąd podczas aktualizacji transakcji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("aktualizujTransakcje() – EntityManager zamknięty");
        }
    }

    // =========================================================
    // === Dodatkowe metody wyszukiwania po różnych kryteriach ===
    // =========================================================

    /**
     * Znajduje wszystkie transakcje wykonane przez danego pracownika.
     */
    public List<Transaction> znajdzPoPracowniku(int pracownikId) {
        logger.debug("znajdzPoPracowniku() – pracownikId={}", pracownikId);
        EntityManager em = emf.createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.pracownik.id = :pid",
                            Transaction.class)
                    .setParameter("pid", pracownikId)
                    .getResultList();
            logger.info("znajdzPoPracowniku() – znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoPracowniku() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoPracowniku() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje transakcje dokonane dokładnie w danym dniu.
     */
    public List<Transaction> znajdzPoDacie(Date data) {
        logger.debug("znajdzPoDacie() – data={}", data);
        EntityManager em = emf.createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.data = :data",
                            Transaction.class)
                    .setParameter("data", data, TemporalType.DATE)
                    .getResultList();
            logger.info("znajdzPoDacie() – znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoDacie() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoDacie() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje transakcje z przedziału dat [fromDate, toDate].
     */
    public List<Transaction> znajdzPoZakresieDat(Date fromDate, Date toDate) {
        logger.debug("znajdzPoZakresieDat() – from={}, to={}", fromDate, toDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.data BETWEEN :fromDate AND :toDate",
                            Transaction.class)
                    .setParameter("fromDate", fromDate, TemporalType.DATE)
                    .setParameter("toDate", toDate,   TemporalType.DATE)
                    .getResultList();
            logger.info("znajdzPoZakresieDat() – znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoZakresieDat() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoZakresieDat() – EntityManager zamknięty");
        }
    }

    /** Zamyka fabrykę EntityManagerFactory. */
    public void close() {
        logger.debug("close() – zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() – EMF zamknięty");
        }
    }
}
