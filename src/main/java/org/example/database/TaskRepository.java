/*
 * Classname: TaskRepository
 * Version information: 1.0
 * Date: 2025-04-27
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.sys.Task;

import java.util.List;

// TODO: dodać metody do wyszukiwania zadań po różnych kryteriach

/**
 * Repozytorium do obsługi zadań.
 */
public class TaskRepository {

    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący EntityManagerFactory.
     */
    public TaskRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    /**
     * Dodaje nowe zadanie.
     *
     * @param task zadanie do dodania
     */
    public void dodajZadanie(Task task) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(task);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Pobiera zadanie po ID.
     *
     * @param id identyfikator zadania
     * @return znalezione zadanie lub null
     */
    public Task znajdzZadaniePoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Task.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera wszystkie zadania.
     *
     * @return lista wszystkich zadań
     */
    public List<Task> pobierzWszystkieZadania() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT t FROM Task t", Task.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Aktualizuje istniejące zadanie.
     *
     * @param task zadanie do aktualizacji
     */
    public void aktualizujZadanie(Task task) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(task);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Usuwa zadanie.
     * TODO: Obsłużyć usuwanie przypisania do pracownika przy usunięciu zadania (Więzy integralności).
     *
     * @param task zadanie do usunięcia
     */
    public void usunZadanie(Task task) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Task managed = em.find(Task.class, task.getId());
            if (managed != null) {
                em.remove(managed);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Zamknięcie EntityManagerFactory.
     */
    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
