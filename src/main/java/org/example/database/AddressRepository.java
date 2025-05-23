package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Address;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa odpowiedzialna za operacje CRUD na encji Address przy użyciu JPA.
 */
public class AddressRepository {

    private static final Logger logger = Logger.getLogger(AddressRepository.class.getName());
    private final EntityManagerFactory emf;

    /**
     * Tworzy instancję AddressRepository i inicjalizuje EntityManagerFactory.
     */
    public AddressRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono EntityManagerFactory w AddressRepository");
    }

    /**
     * Dodaje nowy adres do bazy danych.
     *
     * @param address obiekt Address do zapisania
     */
    public void dodajAdres(Address address) {
        logger.info("Dodawanie adresu: " + address);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(address);
            tx.commit();
            logger.info("Dodano adres o id: " + address.getId());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Błąd podczas dodawania adresu", e);
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Wyszukuje adres na podstawie identyfikatora.
     *
     * @param id identyfikator adresu
     * @return znaleziony Address lub null, jeśli brak
     */
    public Address znajdzAdresPoId(int id) {
        logger.info("Wyszukiwanie adresu po id: " + id);
        EntityManager em = emf.createEntityManager();
        try {
            Address result = em.find(Address.class, id);
            if (result != null) {
                logger.info("Znaleziono adres: " + result);
            } else {
                logger.warning("Nie znaleziono adresu o id: " + id);
            }
            return result;
        } finally {
            em.close();
        }
    }

    /**
     * Pobiera listę wszystkich adresów w bazie danych.
     *
     * @return lista obiektów Address
     */
    public List<Address> pobierzWszystkieAdresy() {
        logger.info("Pobieranie wszystkich adresów");
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM Address a", Address.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Usuwa adres o podanym identyfikatorze.
     *
     * @param id identyfikator adresu do usunięcia
     */
    public void usunAdres(int id) {
        logger.info("Usuwanie adresu o id: " + id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Address address = em.find(Address.class, id);
            if (address != null) {
                em.remove(address);
                logger.info("Usunięto adres o id: " + id);
            } else {
                logger.warning("Nie znaleziono adresu do usunięcia o id: " + id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Błąd podczas usuwania adresu", e);
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Zamyka EntityManagerFactory i zwalnia zasoby.
     */
    public void close() {
        if (emf.isOpen()) {
            emf.close();
            logger.info("Zamknięto EntityManagerFactory w AddressRepository");
        }
    }

    public Address getAllAddresses() {
        return null;
    }

    public void addAddress(Address newAddress) {
    }
}
