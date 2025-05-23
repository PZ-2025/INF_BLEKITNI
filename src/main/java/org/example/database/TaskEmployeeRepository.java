/*
 * Classname: TaskEmployeeRepository
 * Version information: 1.4
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.TaskEmployee;
import org.example.sys.TaskEmployeeId;

import java.util.List;

/**
 * Repozytorium zarządzające powiązaniami zadań z pracownikami.
 * Umożliwia tworzenie, usuwanie oraz wyszukiwanie przypisań.
 */
public class TaskEmployeeRepository {
    private static final Logger logger = LogManager.getLogger(TaskEmployeeRepository.class);
    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący EntityManagerFactory dla persistence unit "myPU".
     */
    public TaskEmployeeRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono TaskEmployeeRepository, EMF={}", emf);
    }

    /**
     * Dodaje nowe przypisanie zadania do pracownika.
     *
     * @param te obiekt TaskEmployee reprezentujący relację zadanie–pracownik
     */
    public void add(TaskEmployee te) {
        logger.debug("add() – start, te={}", te);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(te);
            tx.commit();
            logger.info("add() – przypisanie zapisane: {}", te);
        } catch (Exception e) {
            logger.error("add() – błąd podczas zapisywania przypisania: {}", te, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("add() – EntityManager zamknięty");
        }
    }

    /**
     * Usuwa istniejące przypisanie zadania do pracownika.
     *
     * @param te obiekt TaskEmployee do usunięcia
     */
    public void remove(TaskEmployee te) {
        logger.debug("remove() – start, te={}", te);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TaskEmployee managed = em.merge(te);
            em.remove(managed);
            tx.commit();
            logger.info("remove() – przypisanie usunięte: {}", te);
        } catch (Exception e) {
            logger.error("remove() – błąd podczas usuwania przypisania: {}", te, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("remove() – EntityManager zamknięty");
        }
    }

    /**
     * Wyszukuje przypisanie zadania do pracownika po kluczu złożonym (EmbeddedId).
     *
     * @param taskId     identyfikator zadania
     * @param employeeId identyfikator pracownika
     * @return obiekt TaskEmployee lub null, jeśli nie istnieje
     */
    public TaskEmployee findById(int taskId, int employeeId) {
        logger.debug("findById() – start, taskId={}, employeeId={}", taskId, employeeId);
        EntityManager em = emf.createEntityManager();
        try {
            TaskEmployee te = em.find(
                    TaskEmployee.class,
                    new TaskEmployeeId(taskId, employeeId)
            );
            logger.info("findById() – wynik {}", te);
            return te;
        } catch (Exception e) {
            logger.error("findById() – błąd podczas wyszukiwania przypisania dla ({},{})", taskId, employeeId, e);
            return null;
        } finally {
            em.close();
            logger.debug("findById() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera wszystkie przypisania zadań dla danego pracownika.
     *
     * @param employeeId identyfikator pracownika
     * @return lista obiektów TaskEmployee lub pusta lista w przypadku błędu lub braku wyników
     */
    public List<TaskEmployee> findByEmployee(int employeeId) {
        logger.debug("findByEmployee() – start, employeeId={}", employeeId);
        EntityManager em = emf.createEntityManager();
        try {
            List<TaskEmployee> list = em.createQuery(
                            "SELECT te FROM TaskEmployee te WHERE te.id.employeeId = :eid",
                            TaskEmployee.class
                    )
                    .setParameter("eid", employeeId)
                    .getResultList();
            logger.info("findByEmployee() – znaleziono {} przypisań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByEmployee() – błąd podczas wyszukiwania dla employeeId={}", employeeId, e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByEmployee() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera wszystkie przypisania pracowników dla danego zadania.
     *
     * @param taskId identyfikator zadania
     * @return lista obiektów TaskEmployee lub pusta lista w przypadku błędu lub braku wyników
     */
    public List<TaskEmployee> findByTask(int taskId) {
        logger.debug("findByTask() – start, taskId={}", taskId);
        EntityManager em = emf.createEntityManager();
        try {
            List<TaskEmployee> list = em.createQuery(
                            "SELECT te FROM TaskEmployee te WHERE te.id.taskId = :tid",
                            TaskEmployee.class
                    )
                    .setParameter("tid", taskId)
                    .getResultList();
            logger.info("findByTask() – znaleziono {} przypisań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByTask() – błąd podczas wyszukiwania dla taskId={}", taskId, e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByTask() – EntityManager zamknięty");
        }
    }

    /**
     * Zamyka fabrykę EntityManagerFactory, zwalniając zasoby.
     * Po wywołaniu tej metody instancja nie może być używana do dalszych operacji.
     */
    public void close() {
        if (emf.isOpen()) {
            emf.close();
            logger.info("Zamknięto EntityManagerFactory dla TaskEmployeeRepository");
        }
    }
}
