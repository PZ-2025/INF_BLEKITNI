/*
 * Classname: ProductRepository
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Product;

import java.util.List;

public class ProductRepository {

    private final EntityManagerFactory emf;

    public ProductRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
    }

    /**
     * Dodaje nowy produkt do bazy danych
     * @param produkt Obiekt produktu do dodania
     */
    public void dodajProdukt(Product produkt) {
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

    /**
     * Znajduje produkt po jego identyfikatorze
     * @param id Identyfikator produktu
     * @return Znaleziony produkt lub null jeśli nie istnieje
     */
    public Product znajdzProduktPoId(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera wszystkie produkty z bazy danych
     * @return Lista wszystkich produktów
     */
    public List<Product> pobierzWszystkieProdukty() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p", Product.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera produkty należące do określonej kategorii
     * @param kategoria Nazwa kategorii
     * @return Lista produktów z danej kategorii
     */
    public List<Product> pobierzProduktyPoKategorii(String kategoria) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p WHERE p.category = :kategoria", Product.class)
                    .setParameter("kategoria", kategoria)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Usuwa produkt o podanym identyfikatorze
     * @param id Identyfikator produktu do usunięcia
     */
    public void usunProdukt(int id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Product produkt = em.find(Product.class, id);
            if (produkt != null) {
                em.remove(produkt);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    /**
     * Aktualizuje istniejący produkt
     * @param produkt Zaktualizowany obiekt produktu
     */
    public void aktualizujProdukt(Product produkt) {
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

    /**
     * Aktualizuje ilość produktu o podanym identyfikatorze
     * @param id Identyfikator produktu
     * @param nowaIlosc Nowa ilość produktu
     */
    public void aktualizujIloscProduktu(int id, int nowaIlosc) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Product produkt = em.find(Product.class, id);
            if (produkt != null && nowaIlosc >= 0) {
                produkt.setQuantity(nowaIlosc);
                em.merge(produkt);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    /**
     * Aktualizuje cenę produktu o podanym identyfikatorze
     * @param id Identyfikator produktu
     * @param nowaCena Nowa cena produktu
     */
    public void aktualizujCeneProduktu(int id, double nowaCena) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Product produkt = em.find(Product.class, id);
            if (produkt != null && nowaCena >= 0) {
                produkt.setPrice(nowaCena);
                em.merge(produkt);
            }
            tx.commit();
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    /**
     * Pobiera produkty, których ilość jest mniejsza od podanej wartości
     * @param ilosc Wartość graniczna ilości
     * @return Lista produktów z ilością mniejszą od podanej
     */
    public List<Product> pobierzProduktyPonizejIlosci(int ilosc) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p WHERE p.quantity < :ilosc", Product.class)
                    .setParameter("ilosc", ilosc)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Usuwa wszystkie produkty z określonej kategorii
     * @param kategoria Nazwa kategorii
     * @return Liczba usuniętych produktów
     */
    public int usunProduktyZKategorii(String kategoria) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int usuniete = em.createQuery("DELETE FROM Product p WHERE p.category = :kategoria")
                    .setParameter("kategoria", kategoria)
                    .executeUpdate();
            tx.commit();
            return usuniete;
        } finally {
            if (tx.isActive()) tx.rollback();
            em.close();
        }
    }

    /**
     * Pobiera produkty w określonym zakresie cenowym
     * @param minCena Minimalna cena
     * @param maxCena Maksymalna cena
     * @return Lista produktów w podanym zakresie cenowym
     */
    public List<Product> pobierzProduktyWZakresieCenowym(double minCena, double maxCena) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p WHERE p.price BETWEEN :minCena AND :maxCena", Product.class)
                    .setParameter("minCena", minCena)
                    .setParameter("maxCena", maxCena)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera wszystkie dostępne kategorie produktów.
     *
     * @return lista kategorii produktów
     */
    public List<String> pobierzKategorie() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT p.category FROM Product p ORDER BY p.category",
                            String.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Zamyka fabrykę EntityManager
     */
    public void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}