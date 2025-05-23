/*
 * Classname: ReportRepository
 * Version information: 1.5
 * Date: 2025-05-23
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Report;

import java.io.File;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Repozytorium zarządzające encjami Report w bazie danych.
 * Umożliwia tworzenie, odczyt, aktualizację oraz usuwanie raportów.
 */
public class ReportRepository {
    private static final Logger logger = LogManager.getLogger(ReportRepository.class);
    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący EntityManagerFactory dla persistence unit "myPU".
     */
    public ReportRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono RaportRepository, EMF={}", emf);
    }

    /**
     * Dodaje nowy raport do bazy.
     *
     * @param report obiekt Report do zapisania
     */
    public void addReport(Report report) {
        logger.debug("addReport() – start, report={}", report);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(report);
            tx.commit();
            logger.info("addReport() – raport dodany: {}", report);
        } catch (Exception e) {
            logger.error("addReport() – błąd podczas dodawania raportu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addReport() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje raport o podanym identyfikatorze.
     *
     * @param id identyfikator raportu
     * @return znaleziony obiekt Report lub null, jeśli nie istnieje
     */
    public Report findReportById(int id) {
        logger.debug("findReportById() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Report r = em.find(Report.class, id);
            logger.info("findReportById() – znaleziono: {}", r);
            return r;
        } catch (Exception e) {
            logger.error("findReportById() – błąd podczas wyszukiwania raportu o id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findReportById() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera wszystkie raporty z bazy.
     *
     * @return lista wszystkich obiektów Report, lub pusta lista w przypadku błędu
     */
    public List<Report> getAllReports() {
        logger.debug("getAllReports() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Report> list = em.createQuery("SELECT r FROM Report r", Report.class)
                    .getResultList();
            logger.info("getAllReports() – pobrano {} raportów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllReports() – błąd podczas pobierania raportów", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllReports() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera raporty danego pracownika przypisane na konkretny dzień.
     *
     * @param employeeId identyfikator pracownika
     * @param day        dzień do sprawdzenia (data bez czasu)
     * @return lista raportów z danego dnia lub pusta lista
     */
    public List<Report> getEmployeeDayReport(int employeeId, LocalDate day) {
        logger.debug("getEmployeeDayReport() – start, employeeId={}, day={}", employeeId, day);
        EntityManager em = emf.createEntityManager();
        try {
            List<Report> list = em.createQuery(
                            "SELECT r FROM Report r WHERE r.employee.id = :pid AND r.startDate = :data",
                            Report.class)
                    .setParameter("pid", employeeId)
                    .setParameter("data", java.sql.Date.valueOf(day), TemporalType.DATE)
                    .getResultList();
            logger.info("getEmployeeDayReport() – znaleziono {} raportów", list.size());
            return list;
        } catch (Exception ex) {
            logger.error("getEmployeeDayReport() – błąd dla employeeId={}, day={}", employeeId, day, ex);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("getEmployeeDayReport() – EntityManager zamknięty");
        }
    }

    /**
     * Aktualizuje istniejący raport w bazie.
     *
     * @param report obiekt Report do zaktualizowania
     */
    public void updateReport(Report report) {
        logger.debug("updateReport() – start, report={}", report);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(report);
            tx.commit();
            logger.info("updateReport() – raport zaktualizowany: {}", report);
        } catch (Exception e) {
            logger.error("updateReport() – błąd podczas aktualizacji raportu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateReport() – EntityManager zamknięty");
        }
    }

    /**
     * Usuwa raport z bazy oraz powiązany plik z dysku (jeśli istnieje).
     *
     * @param id identyfikator raportu do usunięcia
     */
    public void removeReport(int id) {
        logger.debug("removeReport() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Report r = em.find(Report.class, id);
            if (r != null) {
                File file = new File(r.getFilePath());
                if (file.exists() && file.isFile()) {
                    if (file.delete()) {
                        logger.info("removeReport() – plik skojarzony usunięty: {}", r.getFilePath());
                    } else {
                        logger.warn("removeReport() – nie udało się usunąć pliku: {}", r.getFilePath());
                    }
                }
                em.remove(r);
                logger.info("removeReport() – raport usunięty z bazy: {}", r);
            } else {
                logger.warn("removeReport() – brak raportu o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeReport() – błąd podczas usuwania raportu o id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeReport() – EntityManager zamknięty");
        }
    }

    // =========================================================
    // === Metody wyszukiwania raportów po różnych kryteriach ===
    // =========================================================

    /**
     * Wyszukuje raporty zawierające fragment typu raportu.
     *
     * @param typeFragment fragment ciągu typu raportu (np. "finansowy")
     * @return lista pasujących raportów lub pusta lista
     */
    public List<Report> findByType(String typeFragment) {
        logger.debug("findByType() – typeFragment={}", typeFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Report> list = em.createQuery(
                            "SELECT r FROM Report r WHERE LOWER(r.reportType) LIKE LOWER(CONCAT('%', :frag, '%'))",
                            Report.class)
                    .setParameter("frag", typeFragment)
                    .getResultList();
            logger.info("findByType() – znaleziono {} raportów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByType() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByType() – EntityManager zamknięty");
        }
    }

    /**
     * Wyszukuje raporty o dacie rozpoczęcia w podanym przedziale.
     *
     * @param startDate data początkowa (włącznie)
     * @param endDate   data końcowa (włącznie)
     * @return lista raportów lub pusta lista
     */
    public List<Report> findByStartDate(Date startDate, Date endDate) {
        logger.debug("findByStartDate() – startDate={}, endDate={}", startDate, endDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<Report> list = em.createQuery(
                            "SELECT r FROM Report r WHERE r.startDate BETWEEN :startDate AND :endDate",
                            Report.class)
                    .setParameter("startDate", startDate, TemporalType.DATE)
                    .setParameter("endDate", endDate, TemporalType.DATE)
                    .getResultList();
            logger.info("findByStartDate() – znaleziono {} raportów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByStartDate() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByStartDate() – EntityManager zamknięty");
        }
    }

    /**
     * Wyszukuje raporty o dacie zakończenia w podanym przedziale.
     *
     * @param startDate data początku przedziału (włącznie)
     * @param endDate   data końca przedziału (włącznie)
     * @return lista raportów lub pusta lista
     */
    public List<Report> findByEndDate(Date startDate, Date endDate) {
        logger.debug("findByEndDate() – startDate={}, endDate={}", startDate, endDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<Report> list = em.createQuery(
                            "SELECT r FROM Report r WHERE r.endDate BETWEEN :startDate AND :endDate",
                            Report.class)
                    .setParameter("startDate", startDate, TemporalType.DATE)
                    .setParameter("endDate", endDate, TemporalType.DATE)
                    .getResultList();
            logger.info("findByEndDate() – znaleziono {} raportów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByEndDate() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByEndDate() – EntityManager zamknięty");
        }
    }

    /**
     * Wyszukuje raporty przypisane do konkretnego pracownika.
     *
     * @param employeeId identyfikator pracownika
     * @return lista raportów pracownika lub pusta lista
     */
    public List<Report> findByEmployee(int employeeId) {
        logger.debug("findByEmployee() – employeeId={}", employeeId);
        EntityManager em = emf.createEntityManager();
        try {
            List<Report> list = em.createQuery(
                            "SELECT r FROM Report r WHERE r.employee.id = :pid",
                            Report.class)
                    .setParameter("pid", employeeId)
                    .getResultList();
            logger.info("findByEmployee() – znaleziono {} raportów", list.size());
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
     * Wyszukuje raporty, których ścieżka pliku zawiera podany fragment (bez uwzględniania wielkości liter).
     *
     * @param fileFragment fragment tekstu do wyszukania w ścieżce pliku
     * @return lista obiektów Report, których ścieżka pliku zawiera podany fragment;
     *         zwraca pustą listę w przypadku błędu lub braku wyników
     */
    public List<Report> findByFilePath(String fileFragment) {
        logger.debug("findByFilePath() – fileFragment={}", fileFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Report> list = em.createQuery(
                            "SELECT r FROM Report r WHERE LOWER(r.filePath) LIKE LOWER(CONCAT('%', :frag, '%'))",
                            Report.class)
                    .setParameter("frag", fileFragment)
                    .getResultList();
            logger.info("findByFilePath() – znaleziono {} raportów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByFilePath() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByFilePath() – EntityManager zamknięty");
        }
    }

    /**
     * Zamyka fabrykę EntityManagerFactory, zwalniając wszystkie zasoby związane z persistence unit.
     * Po wywołaniu tej metody instancja klasy nie może być dalej używana do operacji na bazie.
     */
    public void close() {
        logger.debug("close() – zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() – EMF zamknięty");
        }
    }
}
