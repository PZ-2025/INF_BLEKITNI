package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Raport;

import java.io.File;
import java.util.List;

// TODO: dodać metody do wyszukiwania raportów po różnych kryteriach

public class RaportRepository {

    private final EntityManagerFactory emf;

    public RaportRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    public void dodajRaport(Raport raport) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(raport);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public Raport znajdzRaportPoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Raport.class, id);
        } finally {
            em.close();
        }
    }

    public List<Raport> pobierzWszystkieRaporty() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Raport r", Raport.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void aktualizujRaport(Raport raport) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(raport);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public void usunRaport(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Raport raport = em.find(Raport.class, id);
            if (raport != null) {
                // Spróbuj usunąć plik z dysku jeśli istnieje
                File file = new File(raport.getSciezkaPliku());
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
                em.remove(raport);
            }
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
