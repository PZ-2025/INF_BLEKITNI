/*
 * Classname: TechnicalIssueRepository
 * Version information: 1.0
 * Date: 2025-04-27
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.sys.TechnicalIssue;

import java.util.List;

/**
 * Repozytorium do obsługi zgłoszeń technicznych.
 */
public class TechnicalIssueRepository {

    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący EntityManagerFactory.
     */
    public TechnicalIssueRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    /**
     * Dodaje nowe zgłoszenie techniczne.
     *
     * @param issue zgłoszenie do dodania
     */
    public void dodajZgloszenie(TechnicalIssue issue) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(issue);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Pobiera zgłoszenie po ID.
     *
     * @param id identyfikator zgłoszenia
     * @return znalezione zgłoszenie lub null
     */
    public TechnicalIssue znajdzZgloszeniePoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(TechnicalIssue.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera wszystkie zgłoszenia techniczne.
     *
     * @return lista wszystkich zgłoszeń
     */
    public List<TechnicalIssue> pobierzWszystkieZgloszenia() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT t FROM TechnicalIssue t", TechnicalIssue.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Aktualizuje istniejące zgłoszenie (np. zmienia status).
     *
     * @param issue zgłoszenie do aktualizacji
     */
    public void aktualizujZgloszenie(TechnicalIssue issue) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(issue);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * Usuwa zgłoszenie techniczne.
     *
     * @param issue zgłoszenie do usunięcia
     */
    public void usunZgloszenie(TechnicalIssue issue) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TechnicalIssue managed = em.find(TechnicalIssue.class, issue.getId());
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