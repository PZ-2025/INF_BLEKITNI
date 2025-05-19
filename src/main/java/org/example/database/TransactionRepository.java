package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Transaction;

import java.util.List;

// TODO: dodaj metody do wyszukiwania transakcji po różnych kryteriach

public class TransactionRepository {

    private final EntityManagerFactory emf;

    public TransactionRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    public void dodajTransakcje(Transaction transakcja) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(transakcja);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public Transaction znajdzTransakcjePoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Transaction.class, id);
        } finally {
            em.close();
        }
    }

    public List<Transaction> pobierzWszystkieTransakcje() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void usunTransakcje(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Transaction transakcja = em.find(Transaction.class, id);
            if (transakcja != null) {
                em.remove(transakcja);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void aktualizujTransakcje(Transaction transakcja) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(transakcja);
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
