package org.example.database;

import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Task;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public class TaskRepository {
    private static final Logger logger = LogManager.getLogger(TaskRepository.class);
    private final EntityManagerFactory emf;

    /** Konstruktor inicjalizujący EntityManagerFactory. */
    public TaskRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono TaskRepository, EMF={}", emf);
    }

    /** Dodaje nowe zadanie. */
    public void dodajZadanie(Task task) {
        logger.debug("dodajZadanie() – start, task={}", task);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(task);
            tx.commit();
            logger.info("dodajZadanie() – zadanie dodane: {}", task);
        } catch (Exception e) {
            logger.error("dodajZadanie() – błąd podczas dodawania zadania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("dodajZadanie() – EntityManager zamknięty");
        }
    }

    /** Pobiera zadanie po ID. */
    public Task znajdzZadaniePoId(int id) {
        logger.debug("znajdzZadaniePoId() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Task t = em.find(Task.class, id);
            logger.info("znajdzZadaniePoId() – znaleziono: {}", t);
            return t;
        } catch (Exception e) {
            logger.error("znajdzZadaniePoId() – błąd podczas wyszukiwania zadania id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzZadaniePoId() – EntityManager zamknięty");
        }
    }

    /** Pobiera wszystkie zadania. */
    public List<Task> pobierzWszystkieZadania() {
        logger.debug("pobierzWszystkieZadania() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Task> list = em.createQuery("SELECT t FROM Task t", Task.class)
                    .getResultList();
            logger.info("pobierzWszystkieZadania() – pobrano {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzWszystkieZadania() – błąd podczas pobierania zadań", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzWszystkieZadania() – EntityManager zamknięty");
        }
    }

    /** Aktualizuje istniejące zadanie. */
    public void aktualizujZadanie(Task task) {
        logger.debug("aktualizujZadanie() – start, task={}", task);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(task);
            tx.commit();
            logger.info("aktualizujZadanie() – zadanie zaktualizowane: {}", task);
        } catch (Exception e) {
            logger.error("aktualizujZadanie() – błąd podczas aktualizacji zadania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("aktualizujZadanie() – EntityManager zamknięty");
        }
    }

    /** Usuwa zadanie. */
    public void usunZadanie(Task task) {
        logger.debug("usunZadanie() – start, task={}", task);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Task managed = em.find(Task.class, task.getId());
            if (managed != null) {
                em.remove(managed);
                logger.info("usunZadanie() – usunięto zadanie: {}", managed);
            } else {
                logger.warn("usunZadanie() – brak zadania o id={}", task.getId());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("usunZadanie() – błąd podczas usuwania zadania", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("usunZadanie() – EntityManager zamknięty");
        }
    }

    // === metody wyszukiwania ===

    /** Znajduje zadania, których nazwa zawiera podany fragment. */
    public List<Task> znajdzPoNazwie(String nazwaFragment) {
        logger.debug("znajdzPoNazwie() – nazwaFragment={}", nazwaFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Task> list = em.createQuery(
                            "SELECT t FROM Task t WHERE LOWER(t.nazwa) LIKE LOWER(CONCAT('%', :frag, '%'))",
                            Task.class)
                    .setParameter("frag", nazwaFragment)
                    .getResultList();
            logger.info("znajdzPoNazwie() – znaleziono {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoNazwie() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoNazwie() – EntityManager zamknięty");
        }
    }

    /** Znajduje zadania o dokładnie podanej dacie. */
    public List<Task> znajdzPoDacie(Date data) {
        logger.debug("znajdzPoDacie() – data={}", data);
        EntityManager em = emf.createEntityManager();
        try {
            List<Task> list = em.createQuery(
                            "SELECT t FROM Task t WHERE t.data = :data", Task.class)
                    .setParameter("data", data, TemporalType.DATE)
                    .getResultList();
            logger.info("znajdzPoDacie() – znaleziono {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoDacie() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoDacie() – EntityManager zamknięty");
        }
    }

    /** Znajduje zadania o dokładnie podanym statusie. */
    public List<Task> znajdzPoStatusie(String status) {
        logger.debug("znajdzPoStatusie() – status={}", status);
        EntityManager em = emf.createEntityManager();
        try {
            List<Task> list = em.createQuery(
                            "SELECT t FROM Task t WHERE t.status = :status", Task.class)
                    .setParameter("status", status)
                    .getResultList();
            logger.info("znajdzPoStatusie() – znaleziono {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoStatusie() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoStatusie() – EntityManager zamknięty");
        }
    }

    /** Znajduje zadania, których opis zawiera podany fragment. */
    public List<Task> znajdzPoOpisie(String opisFragment) {
        logger.debug("znajdzPoOpisie() – opisFragment={}", opisFragment);
        EntityManager em = emf.createEntityManager();
        try {
            List<Task> list = em.createQuery(
                            "SELECT t FROM Task t WHERE LOWER(t.opis) LIKE LOWER(CONCAT('%', :frag, '%'))",
                            Task.class)
                    .setParameter("frag", opisFragment)
                    .getResultList();
            logger.info("znajdzPoOpisie() – znaleziono {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoOpisie() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoOpisie() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje zadania, których czas trwania zmiany mieści się w podanym przedziale.
     */
    public List<Task> znajdzPoCzasieTrwaniaZmiany(LocalTime od, LocalTime doTime) {
        logger.debug("znajdzPoCzasieTrwaniaZmiany() – od={}, doTime={}", od, doTime);
        EntityManager em = emf.createEntityManager();
        try {
            List<Task> list = em.createQuery(
                            "SELECT t FROM Task t WHERE t.czasTrwaniaZmiany BETWEEN :od AND :doTime",
                            Task.class)
                    .setParameter("od", od)
                    .setParameter("doTime", doTime)
                    .getResultList();
            logger.info("znajdzPoCzasieTrwaniaZmiany() – znaleziono {} zadań", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoCzasieTrwaniaZmiany() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoCzasieTrwaniaZmiany() – EntityManager zamknięty");
        }
    }

    /** Zamknięcie EntityManagerFactory. */
    public void close() {
        logger.debug("close() – zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() – EMF zamknięty");
        }
    }
}
