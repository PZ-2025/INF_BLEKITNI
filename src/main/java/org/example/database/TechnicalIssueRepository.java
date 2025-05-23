package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.TechnicalIssue;

import java.time.LocalDate;
import java.util.List;

/**
 * Repozytorium do obsługi zgłoszeń technicznych.
 * Umożliwia tworzenie, odczyt, aktualizację, usuwanie oraz wyszukiwanie zgłoszeń.
 */
public class TechnicalIssueRepository {

    private static final Logger logger = LogManager.getLogger(TechnicalIssueRepository.class);
    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący EntityManagerFactory dla persistence unit "myPU".
     */
    public TechnicalIssueRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono TechnicalIssueRepository, EMF={}", emf);
    }

    /**
     * Dodaje nowe zgłoszenie techniczne do bazy.
     *
     * @param issue obiekt TechnicalIssue do zapisania
     */
    public void addIssue(TechnicalIssue issue) {
        logger.debug("addIssue() – start, issue={}", issue);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(issue);
            tx.commit();
            logger.info("addIssue() – zgłoszenie dodane: {}", issue);
        } catch (Exception e) {
            logger.error("addIssue() – błąd podczas dodawania zgłoszenia", e);
            if (tx.isActive()) {
                tx.rollback();
                logger.info("addIssue() – transakcja wycofana");
            }
        } finally {
            em.close();
            logger.debug("addIssue() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera zgłoszenie techniczne o podanym identyfikatorze.
     *
     * @param id identyfikator zgłoszenia
     * @return obiekt TechnicalIssue lub null, jeśli nie znaleziono
     */
    public TechnicalIssue findIssueById(int id) {
        logger.debug("findIssueById() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            TechnicalIssue issue = em.find(TechnicalIssue.class, id);
            logger.info("findIssueById() – znaleziono: {}", issue);
            return issue;
        } catch (Exception e) {
            logger.error("findIssueById() – błąd podczas pobierania zgłoszenia id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findIssueById() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera wszystkie zgłoszenia techniczne z bazy.
     *
     * @return lista obiektów TechnicalIssue lub pusta lista w przypadku błędu
     */
    public List<TechnicalIssue> getAllIssues() {
        logger.debug("getAllIssues() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> list = em.createQuery("SELECT t FROM TechnicalIssue t", TechnicalIssue.class)
                    .getResultList();
            logger.info("getAllIssues() – pobrano {} zgłoszeń", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllIssues() – błąd podczas pobierania zgłoszeń", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllIssues() – EntityManager zamknięty");
        }
    }

    /**
     * Aktualizuje istniejące zgłoszenie techniczne.
     *
     * @param issue obiekt TechnicalIssue do zaktualizowania
     */
    public void updateIssue(TechnicalIssue issue) {
        logger.debug("updateIssue() – start, issue={}", issue);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(issue);
            tx.commit();
            logger.info("updateIssue() – zgłoszenie zaktualizowane: {}", issue);
        } catch (Exception e) {
            logger.error("updateIssue() – błąd podczas aktualizacji zgłoszenia", e);
            if (tx.isActive()) {
                tx.rollback();
                logger.info("updateIssue() – transakcja wycofana");
            }
        } finally {
            em.close();
            logger.debug("updateIssue() – EntityManager zamknięty");
        }
    }

    /**
     * Usuwa zgłoszenie techniczne z bazy.
     *
     * @param issue obiekt TechnicalIssue do usunięcia
     */
    public void removeIssue(TechnicalIssue issue) {
        logger.debug("removeIssue() – start, issue={}", issue);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TechnicalIssue managed = em.find(TechnicalIssue.class, issue.getId());
            if (managed != null) {
                em.remove(managed);
                logger.info("removeIssue() – usunięto zgłoszenie: {}", managed);
            } else {
                logger.warn("removeIssue() – brak zgłoszenia o id={}", issue.getId());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeIssue() – błąd podczas usuwania zgłoszenia", e);
            if (tx.isActive()) {
                tx.rollback();
                logger.info("removeIssue() – transakcja wycofana");
            }
        } finally {
            em.close();
            logger.debug("removeIssue() – EntityManager zamknięty");
        }
    }

    // ===========================
    // Metody wyszukiwania po kryteriach
    // ===========================

    /**
     * Wyszukuje zgłoszenia, których typ zawiera podany fragment (case insensitive).
     *
     * @param typeFragment fragment tekstu pola type
     * @return lista dopasowanych zgłoszeń lub pusta lista
     */
    public List<TechnicalIssue> findByType(String typeFragment) {
        logger.debug("findByType() – typeFragment={}", typeFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> list = em.createQuery(
                            "SELECT t FROM TechnicalIssue t WHERE LOWER(t.type) LIKE LOWER(CONCAT('%', :frag, '%'))",
                            TechnicalIssue.class)
                    .setParameter("frag", typeFragment)
                    .getResultList();
            logger.info("findByType() – znaleziono {} zgłoszeń", list.size());
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
     * Wyszukuje zgłoszenia z datą zgłoszenia w podanym przedziale.
     *
     * @param start początek przedziału (inclusive)
     * @param end   koniec przedziału (inclusive)
     * @return lista dopasowanych zgłoszeń lub pusta lista
     */
    public List<TechnicalIssue> findByDate(LocalDate start, LocalDate end) {
        logger.debug("findByDate() – start={}, end={}", start, end);
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> list = em.createQuery(
                            "SELECT t FROM TechnicalIssue t WHERE t.dateSubmitted BETWEEN :start AND :end",
                            TechnicalIssue.class)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();
            logger.info("findByDate() – znaleziono {} zgłoszeń", list.size());
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
     * Wyszukuje zgłoszenia o dokładnie podanym statusie.
     *
     * @param status status zgłoszenia
     * @return lista dopasowanych zgłoszeń lub pusta lista
     */
    public List<TechnicalIssue> findByStatus(String status) {
        logger.debug("findByStatus() – status={}", status);
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> list = em.createQuery(
                            "SELECT t FROM TechnicalIssue t WHERE t.status = :status",
                            TechnicalIssue.class)
                    .setParameter("status", status)
                    .getResultList();
            logger.info("findByStatus() – znaleziono {} zgłoszeń", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByStatus() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByStatus() – EntityManager zamknięty");
        }
    }

    /**
     * Wyszukuje zgłoszenia zgłoszone przez konkretnego pracownika.
     *
     * @param employeeId identyfikator pracownika
     * @return lista dopasowanych zgłoszeń lub pusta lista
     */
    public List<TechnicalIssue> findByEmployee(int employeeId) {
        logger.debug("findByEmployee() – employeeId={}", employeeId);
        EntityManager em = emf.createEntityManager();
        try {
            List<TechnicalIssue> list = em.createQuery(
                            "SELECT t FROM TechnicalIssue t WHERE t.employee.id = :pid",
                            TechnicalIssue.class)
                    .setParameter("pid", employeeId)
                    .getResultList();
            logger.info("findByEmployee() – znaleziono {} zgłoszeń", list.size());
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
