/*
 * Classname: AbsenceRequestRepository
 * Version information: 1.1
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.*;
import org.example.sys.AbsenceRequest;

import java.util.List;

public class AbsenceRequestRepository {

    private final EntityManagerFactory emf;

    public AbsenceRequestRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    public void dodajWniosek(AbsenceRequest request) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(request);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public AbsenceRequest znajdzWniosekPoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(AbsenceRequest.class, id);
        } finally {
            em.close();
        }
    }

    public List<AbsenceRequest> pobierzWszystkieWnioski() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT w FROM AbsenceRequest w", AbsenceRequest.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void usunWniosek(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AbsenceRequest wniosek = em.find(AbsenceRequest.class, id);
            if (wniosek != null) {
                em.remove(wniosek);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void aktualizujWniosek(AbsenceRequest request) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(request);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
