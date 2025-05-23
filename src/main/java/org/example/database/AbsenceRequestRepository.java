/*
 * Classname: AbsenceRequestRepository
 * Version information: 1.5
 * Date: 2025-05-23
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.sys.AbsenceRequest;
import org.example.sys.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Repozytorium do zarządzania wnioskami o nieobecność.
 * Oferuje pełen zestaw metod CRUD oraz filtrowania wniosków.
 */
public class AbsenceRequestRepository {
    private static final Logger logger = LoggerFactory.getLogger(AbsenceRequestRepository.class);
    private final EntityManagerFactory emf;

    /** Konstruktor inicjalizujący EntityManagerFactory. */
    public AbsenceRequestRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono AbsenceRequestRepository, EMF={}", emf);
    }

    /**
     * Dodaje nowy wniosek o nieobecność.
     *
     * @param request obiekt wniosku do dodania
     */
    public void addRequest(AbsenceRequest request) {
        logger.debug("addRequest() - start, request={}", request);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(request);
            tx.commit();
            logger.info("addRequest() - wniosek dodany: {}", request);
        } catch (Exception e) {
            logger.error("addRequest() - błąd podczas dodawania wniosku", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("addRequest() - EM zamknięty");
        }
    }

    /**
     * Pobiera wniosek o nieobecność po jego ID.
     *
     * @param id identyfikator wniosku
     * @return znaleziony wniosek lub null
     */
    public AbsenceRequest findRequestById(int id) {
        logger.debug("findRequestById() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            AbsenceRequest w = em.find(AbsenceRequest.class, id);
            logger.info("findRequestById() - znaleziono: {}", w);
            return w;
        } catch (Exception e) {
            logger.error("findRequestById() - błąd podczas pobierania wniosku o id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findRequestById() - EM zamknięty");
        }
    }

    /**
     * Pobiera wszystkie wnioski o nieobecność.
     *
     * @return lista wniosków (może być pusta)
     */
    public List<AbsenceRequest> getAllRequests() {
        logger.debug("getAllRequests() - start");
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery("SELECT w FROM AbsenceRequest w", AbsenceRequest.class).getResultList();
            logger.info("getAllRequests() - pobrano {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllRequests() - błąd podczas pobierania wszystkich wniosków", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("getAllRequests() - EM zamknięty");
        }
    }

    /**
     * Usuwa wniosek o nieobecność o podanym ID.
     *
     * @param id identyfikator wniosku do usunięcia
     */
    public void removeRequest(int id) {
        logger.debug("removeRequest() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AbsenceRequest w = em.find(AbsenceRequest.class, id);
            if (w != null) {
                em.remove(w);
                logger.info("removeRequest() - usunięto wniosek: {}", w);
            } else {
                logger.warn("removeRequest() - brak wniosku o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeRequest() - błąd podczas usuwania wniosku o id={}", id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("removeRequest() - EM zamknięty");
        }
    }

    /**
     * Aktualizuje istniejący wniosek o nieobecność.
     *
     * @param request obiekt wniosku z zmienionymi danymi
     */
    public void updateRequest(AbsenceRequest request) {
        logger.debug("updateRequest() - start, request={}", request);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(request);
            tx.commit();
            logger.info("updateRequest() - zaktualizowano wniosek: {}", request);
        } catch (Exception e) {
            logger.error("updateRequest() - błąd podczas aktualizacji wniosku", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("updateRequest() - EM zamknięty");
        }
    }

    /** Wyszukiwanie według pracownika. */
    public List<AbsenceRequest> findEmployeeRequests(Employee employee) {
        logger.debug("findEmployeeRequests() - employee={}", employee);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery("SELECT w FROM AbsenceRequest w WHERE w.employee = :employee", AbsenceRequest.class)
                    .setParameter("employee", employee)
                    .getResultList();
            logger.info("findEmployeeRequests() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findEmployeeRequests() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findEmployeeRequests() - EM zamknięty");
        }
    }

    /** Wyszukiwanie według ID pracownika. */
    public List<AbsenceRequest> findEmployeeRequestsById(int employeeId) {
        logger.debug("findEmployeeRequestsById() - employeeId={}", employeeId);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery("SELECT w FROM AbsenceRequest w WHERE w.employee.id = :id", AbsenceRequest.class)
                    .setParameter("id", employeeId)
                    .getResultList();
            logger.info("findEmployeeRequestsById() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findEmployeeRequestsById() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findEmployeeRequestsById() - EM zamknięty");
        }
    }

    /** Wyszukiwanie według typu wniosku. */
    public List<AbsenceRequest> findRequestsByType(String requestType) {
        logger.debug("findRequestsByType() - requestType={}", requestType);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery("SELECT w FROM AbsenceRequest w WHERE w.type = :type", AbsenceRequest.class)
                    .setParameter("type", requestType)
                    .getResultList();
            logger.info("findRequestsByType() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsByType() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsByType() - EM zamknięty");
        }
    }

    /** Wyszukiwanie według statusu wniosku. */
    public List<AbsenceRequest> findRequestsByStatus(AbsenceRequest.RequestStatus status) {
        logger.debug("findRequestsByStatus() - status={}", status);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery("SELECT w FROM AbsenceRequest w WHERE w.status = :status", AbsenceRequest.class)
                    .setParameter("status", status)
                    .getResultList();
            logger.info("findRequestsByStatus() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsByStatus() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsByStatus() - EM zamknięty");
        }
    }

    /** Wyszukiwanie od określonej daty. */
    public List<AbsenceRequest> findRequestsFromDate(Date fromDate) {
        logger.debug("findRequestsFromDate() - fromDate={}", fromDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery("SELECT w FROM AbsenceRequest w WHERE w.fromDate >= :fromDate", AbsenceRequest.class)
                    .setParameter("fromDate", fromDate)
                    .getResultList();
            logger.info("findRequestsFromDate() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsFromDate() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsFromDate() - EM zamknięty");
        }
    }

    /** Wyszukiwanie do określonej daty. */
    public List<AbsenceRequest> findRequestsToDate(Date toDate) {
        logger.debug("findRequestsToDate() - toDate={}", toDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery("SELECT w FROM AbsenceRequest w WHERE w.toDate <= :toDate", AbsenceRequest.class)
                    .setParameter("toDate", toDate)
                    .getResultList();
            logger.info("findRequestsToDate() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsToDate() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsToDate() - EM zamknięty");
        }
    }

    /** Wnioski w zakresie dat. */
    public List<AbsenceRequest> findDateRangeRequests(Date fromDate, Date toDate) {
        logger.debug("findDateRangeRequests() - fromDate={}, toDate={}", fromDate, toDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.fromDate >= :fromDate AND w.toDate <= :toDate", AbsenceRequest.class)
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .getResultList();
            logger.info("findDateRangeRequests() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findDateRangeRequests() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findDateRangeRequests() - EM zamknięty");
        }
    }

    /** Wnioski nachodzące na przedział dat. */
    public List<AbsenceRequest> findRequestsOverlappingDateRange(Date fromDate, Date toDate) {
        logger.debug("findRequestsOverlappingDateRange() - fromDate={}, toDate={}", fromDate, toDate);
        EntityManager em = emf.createEntityManager();
        try {
            List<AbsenceRequest> list = em.createQuery(
                            "SELECT w FROM AbsenceRequest w WHERE w.fromDate <= :toDate AND w.toDate >= :fromDate", AbsenceRequest.class)
                    .setParameter("toDate", toDate)
                    .setParameter("fromDate", fromDate)
                    .getResultList();
            logger.info("findRequestsOverlappingDateRange() - znaleziono {} wniosków", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findRequestsOverlappingDateRange() - błąd podczas wyszukiwania", e);
            return Collections.emptyList();
        } finally {
            em.close();
            logger.debug("findRequestsOverlappingDateRange() - EM zamknięty");
        }
    }

    /** Zamyka fabrykę EntityManagerFactory. */
    public void close() {
        logger.debug("close() - start");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() - EMF zamknięty");
        } else {
            logger.warn("close() - EMF już zamknięty");
        }
    }
}