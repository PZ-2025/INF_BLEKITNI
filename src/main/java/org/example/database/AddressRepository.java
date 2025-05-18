/*
 * Classname: AddressRepository
 * Version information: 1.1
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Address;

import java.util.List;

public class AddressRepository {

    private final EntityManagerFactory emf;

    public AddressRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    public void dodajAdres(Address address) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(address);
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    public Address znajdzAdresPoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Address.class, id);
        } finally {
            em.close();
        }
    }

    public List<Address> pobierzWszystkieAdresy() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM Address a", Address.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void usunAdres(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Address address = em.find(Address.class, id);
            if (address != null) {
                em.remove(address);
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
