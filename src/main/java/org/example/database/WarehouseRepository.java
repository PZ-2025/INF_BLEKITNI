/*
 * Classname: WarehouseRepository
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Warehouse;

import java.util.List;

public class WarehouseRepository {

    private final EntityManagerFactory emf;

    public WarehouseRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    public void dodajProdukt(Warehouse produkt) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(produkt);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public Warehouse znajdzProduktPoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Warehouse.class, id);
        } finally {
            em.close();
        }
    }

    public List<Warehouse> pobierzWszystkieProdukty() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT w FROM Warehouse w", Warehouse.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void usunProdukt(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Warehouse produkt = em.find(Warehouse.class, id);
            if (produkt != null) {
                em.remove(produkt);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void aktualizujProdukt(Warehouse produkt) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(produkt);
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
