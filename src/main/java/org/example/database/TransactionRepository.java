/*
 * Classname: TransactionRepository
 * Version information: 1.3
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TemporalType;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import org.example.sys.Product;
import org.example.sys.Transaction;
import org.example.sys.TransactionProduct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Repozytorium zarządzające transakcjami w bazie danych.
 * Umożliwia tworzenie, odczyt, aktualizację, usuwanie oraz zaawansowane wyszukiwanie transakcji.
 */
public class TransactionRepository {
    private static final Logger logger = LogManager.getLogger(TransactionRepository.class);
    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący EntityManagerFactory dla persistence unit "myPU".
     */
    public TransactionRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono TransactionRepository, EMF={}", emf);
    }

    /**
     * Dodaje nową transakcję do bazy.
     *
     * @param transaction obiekt Transaction do zapisania
     */
    public void addTransaction(Transaction transaction) {
        logger.debug("addTransaction() – start, transaction={}", transaction);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(transaction);
            tx.commit();
            logger.info("addTransaction() – transaction dodana: {}", transaction);
        } catch (Exception e) {
            logger.error("addTransaction() – błąd podczas dodawania transakcji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addTransaction() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje transakcję o podanym identyfikatorze.
     *
     * @param id identyfikator transakcji
     * @return obiekt Transaction lub null, jeśli nie istnieje
     */
    public Transaction findTransactionById(int id) {
        logger.debug("findTransactionById() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Transaction t = em.find(Transaction.class, id);
            logger.info("findTransactionById() – znaleziono: {}", t);
            return t;
        } catch (Exception e) {
            logger.error("findTransactionById() – błąd podczas pobierania id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findTransactionById() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera wszystkie transakcje z bazy.
     *
     * @return lista obiektów Transaction lub pusta lista w przypadku błędu
     */
    public List<Transaction> getAllTransactions() {
        logger.debug("getAllTransactions() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Transaction> list = em.createQuery("SELECT t FROM Transaction t", Transaction.class)
                    .getResultList();
            logger.info("getAllTransactions() – pobrano {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllTransactions() – błąd podczas pobierania transakcji", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllTransactions() – EntityManager zamknięty");
        }
    }

    /**
     * Usuwa transakcję o podanym identyfikatorze.
     *
     * @param id identyfikator transakcji do usunięcia
     */
    public void removeTransactions(int id) {
        logger.debug("removeTransactions() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction t = em.find(Transaction.class, id);
            if (t != null) {
                em.remove(t);
                logger.info("removeTransactions() – usunięto transakcję: {}", t);
            } else {
                logger.warn("removeTransactions() – brak transakcji o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeTransactions() – błąd podczas usuwania id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeTransactions() – EntityManager zamknięty");
        }
    }

    /**
     * Aktualizuje istniejącą transakcję w bazie.
     *
     * @param transaction obiekt Transaction do zaktualizowania
     */
    public void updateTransaction(Transaction transaction) {
        logger.debug("updateTransaction() – start, transaction={}", transaction);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(transaction);
            tx.commit();
            logger.info("updateTransaction() – transaction zaktualizowana: {}", transaction);
        } catch (Exception e) {
            logger.error("updateTransaction() – błąd podczas aktualizacji transakcji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateTransaction() – EntityManager zamknięty");
        }
    }

    // =========================================================
    // === Dodatkowe metody wyszukiwania po różnych kryteriach ===
    // =========================================================

    /**
     * Zwraca liczbę sztuk danego produktu sprzedanych w określonym dniu.
     *
     * @param product encja Product
     * @param date    dzień sprzedaży (LocalDate, bez czasu)
     * @return liczba sprzedanych sztuk lub 0 w przypadku błędu/braku danych
     */
    public int getSoldQuantityForProductOnDate(Product product, LocalDate date) {
        logger.debug("getSoldQuantityForProductOnDate() – start, product={}, date={}", product, date);
        EntityManager em = emf.createEntityManager();
        try {
            Date sqlDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Long total = em.createQuery(
                            "SELECT COALESCE(SUM(z.quantity),0) FROM Transaction z " +
                                    "WHERE z.product = :product AND z.data = :data", Long.class)
                    .setParameter("product", product)
                    .setParameter("data", sqlDate, TemporalType.DATE)
                    .getSingleResult();
            int result = total.intValue();
            logger.info("getSoldQuantityForProductOnDate() – sprzedano {} sztuk produktu {} w dniu {}",
                    result, product, date);
            return result;
        } catch (Exception e) {
            logger.error("getSoldQuantityForProductOnDate() – błąd liczenia sprzedaży produktu {} w dniu {}",
                    product, date, e);
            return 0;
        } finally {
            em.close();
            logger.debug("getSoldQuantityForProductOnDate() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera wszystkie transakcje wraz z powiązanymi produktami między podanymi datami.
     *
     * @param startDate data początkowa (inclusive)
     * @param endDate   data końcowa (inclusive)
     * @return lista obiektów Transaction lub pusta lista
     */
    public List<Transaction> getTransactionsBetweenDates(Date startDate, Date endDate) {
        logger.debug("getTransactionsBetweenDates() – start, from={}, to={}", startDate, endDate);
        EntityManager em = emf.createEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
            Root<Transaction> root = cq.from(Transaction.class);
            root.fetch("transactionProducts", JoinType.LEFT);
            cq.select(root)
                    .where(cb.between(root.get("data"), startDate, endDate))
                    .orderBy(cb.asc(root.get("data")));
            List<Transaction> result = em.createQuery(cq).getResultList();
            logger.info("getTransactionsBetweenDates() – znaleziono {} transakcji", result.size());
            return result;
        } catch (Exception e) {
            logger.error("getTransactionsBetweenDates() – błąd pobierania transakcji między {} a {}",
                    startDate, endDate, e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getTransactionsBetweenDates() – EntityManager zamknięty");
        }
    }

    /**
     * Dodaje lub aktualizuje relację TransactionProduct dla podanej transakcji i produktu.
     *
     * @param transaction obiekt Transaction
     * @param product     obiekt Product
     * @param quantity    żądana ilość produktu
     */
    public void addProductToTransaction(Transaction transaction, Product product, int quantity) {
        logger.debug("addProductToTransaction() – start, transactionId={}, productId={}, quantity={}",
                transaction.getId(), product.getId(), quantity);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction managedTx = em.find(Transaction.class, transaction.getId());
            Product     managedPr = em.find(Product.class, product.getId());
            if (managedTx != null && managedPr != null) {
                String jpql = "SELECT tp FROM TransactionProduct tp " +
                        "WHERE tp.transaction.id = :txId AND tp.product.id = :prodId";
                List<TransactionProduct> existing = em.createQuery(jpql, TransactionProduct.class)
                        .setParameter("txId", managedTx.getId())
                        .setParameter("prodId", managedPr.getId())
                        .getResultList();
                if (existing.isEmpty()) {
                    TransactionProduct tp = new TransactionProduct();
                    tp.setTransaction(managedTx);
                    tp.setProduct(managedPr);
                    tp.setQuantity(quantity);
                    em.persist(tp);
                    logger.info("addProductToTransaction() – utworzono relację (tx={}, prod={}, qty={})",
                            managedTx.getId(), managedPr.getId(), quantity);
                } else {
                    TransactionProduct tp = existing.get(0);
                    tp.setQuantity(quantity);
                    em.merge(tp);
                    logger.info("addProductToTransaction() – zaktualizowano qty w relacji (tx={}, prod={}, qty={})",
                            managedTx.getId(), managedPr.getId(), quantity);
                }
            } else {
                logger.warn("addProductToTransaction() – nie znaleziono transakcji ({}) lub produktu ({})",
                        transaction.getId(), product.getId());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("addProductToTransaction() – błąd podczas operacji relacji", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addProductToTransaction() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje wszystkie transakcje wykonane przez danego pracownika.
     *
     * @param employeeId identyfikator pracownika
     * @return lista obiektów Transaction lub pusta lista
     */
    public List<Transaction> findByEmployee(int employeeId) {
        logger.debug("findByEmployee() – employeeId={}", employeeId);
        EntityManager em = emf.createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.employee.id = :pid", Transaction.class)
                    .setParameter("pid", employeeId)
                    .getResultList();
            logger.info("findByEmployee() – znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByEmployee() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByEmployee() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje transakcje dokonane dokładnie w danym dniu.
     *
     * @param date date transakcji (bez czasu)
     * @return lista obiektów Transaction lub pusta lista
     */
    public List<Transaction> findByDate(Date date) {
        logger.debug("findByDate() – date={}", date);
        EntityManager em = emf.createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.date = :date", Transaction.class)
                    .setParameter("date", date, TemporalType.DATE)
                    .getResultList();
            logger.info("findByDate() – znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByDate() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByDate() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje transakcje w przedziale dat [fromDate, toDate].
     *
     * @param fromDate data początkowa (inclusive)
     * @param toDate   data końcowa (inclusive)
     * @return lista obiektów Transaction lub pusta lista
     */
    public List<Transaction> findByDateRange(Date fromDate, Date toDate) {
        logger.debug("findByDateRange() – from={}, to={}", fromDate, toDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<Transaction> list = em.createQuery(
                            "SELECT t FROM Transaction t WHERE t.data BETWEEN :from AND :to", Transaction.class)
                    .setParameter("from", fromDate, TemporalType.DATE)
                    .setParameter("to", toDate,   TemporalType.DATE)
                    .getResultList();
            logger.info("findByDateRange() – znaleziono {} transakcji", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByDateRange() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByDateRange() – EntityManager zamknięty");
        }
    }

    /**
     * Zwraca sesję Hibernate uzyskaną z EntityManager.
     *
     * @return obiekt Session lub wyrzuca wyjątek, jeśli konwersja się nie powiedzie
     */
    public Session getSession() {
        logger.debug("getSession() – start");
        EntityManager em = emf.createEntityManager();
        Session session = em.unwrap(Session.class);
        logger.info("getSession() – uzyskano sesję Hibernate: {}", session);
        return session;
    }

    /**
     * Zamyka fabrykę EntityManagerFactory, zwalniając wszystkie zasoby.
     * Po wywołaniu tej metody instancja nie może być używana do dalszych operacji.
     */
    public void close() {
        logger.debug("close() – zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() – EMF zamknięty");
        }
    }

}