package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Warehouse;

import java.util.List;

// TODO: dodać metody do wyszukiwania stanów magazynowych po różnych kryteriach

public class WarehouseRepository {

    private final EntityManagerFactory emf;

    public WarehouseRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    public void dodajStanMagazynowy(Warehouse stan) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(stan);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public Warehouse znajdzStanPoIdProduktu(int idProduktu) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Warehouse.class, idProduktu);
        } finally {
            em.close();
        }
    }

    public List<Warehouse> pobierzWszystkieStany() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT w FROM Warehouse w", Warehouse.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void usunStan(int idProduktu) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Warehouse stan = em.find(Warehouse.class, idProduktu);
            if (stan != null) {
                em.remove(stan);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void aktualizujStan(Warehouse stan) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(stan);
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
