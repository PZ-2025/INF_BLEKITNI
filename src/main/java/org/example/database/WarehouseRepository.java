package org.example.database;

import jakarta.persistence.*;
import org.example.sys.Warehouse;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repozytorium do zarządzania stanem magazynowym.
 * Umożliwia tworzenie, odczyt, aktualizację, usuwanie oraz wyszukiwanie stanów magazynowych.
 */
public class WarehouseRepository {

    private static final Logger logger = Logger.getLogger(WarehouseRepository.class.getName());

    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący fabrykę EntityManagerFactory dla persistence unit "myPU".
     */
    public WarehouseRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("EntityManagerFactory utworzony dla WarehouseRepository");
    }

    /**
     * Dodaje nowy stan magazynowy (produkt) do bazy.
     *
     * @param produkt obiekt Warehouse reprezentujący stan magazynowy do zapisania
     */
    public void dodajProdukt(Warehouse produkt) {
        logger.info("Rozpoczynam dodawanie produktu: " + produkt);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(produkt);
            tx.commit();
            logger.info("Produkt dodany pomyślnie: " + produkt);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Błąd podczas dodawania produktu: " + produkt, e);
            if (tx.isActive()) {
                tx.rollback();
                logger.info("Transakcja wycofana (rollback) podczas dodawania produktu");
            }
        } finally {
            em.close();
            logger.info("EntityManager zamknięty po dodaniu produktu");
        }
    }

    /**
     * Pobiera stan magazynowy (produkt) na podstawie identyfikatora produktu.
     *
     * @param id identyfikator produktu
     * @return obiekt Warehouse lub null, jeśli nie znaleziono
     */
    public Warehouse znajdzProduktPoId(int id) {
        logger.info("Wyszukiwanie produktu po ID: " + id);
        EntityManager em = emf.createEntityManager();
        try {
            Warehouse produkt = em.find(Warehouse.class, id);
            logger.info("Produkt znaleziony: " + produkt);
            return produkt;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Błąd podczas wyszukiwania produktu po ID: " + id, e);
            return null;
        } finally {
            em.close();
            logger.info("EntityManager zamknięty po wyszukiwaniu produktu");
        }
    }

    /**
     * Pobiera wszystkie stany magazynowe (produkty) z bazy.
     *
     * @return lista obiektów Warehouse lub null w przypadku błędu
     */
    public List<Warehouse> pobierzWszystkieProdukty() {
        logger.info("Pobieranie wszystkich produktów");
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> produkty = em.createQuery("SELECT w FROM Warehouse w", Warehouse.class).getResultList();
            logger.info("Pobrano " + produkty.size() + " produktów");
            return produkty;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Błąd podczas pobierania wszystkich produktów", e);
            return null;
        } finally {
            em.close();
            logger.info("EntityManager zamknięty po pobraniu produktów");
        }
    }

    /**
     * Usuwa stan magazynowy (produkt) na podstawie identyfikatora produktu.
     *
     * @param id identyfikator produktu
     */
    public void usunProdukt(int id) {
        logger.info("Usuwanie produktu o ID: " + id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Warehouse produkt = em.find(Warehouse.class, id);
            if (produkt != null) {
                em.remove(produkt);
                logger.info("Produkt usunięty: " + produkt);
            } else {
                logger.warning("Produkt o ID " + id + " nie znaleziony, nie usunięto");
            }
            tx.commit();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Błąd podczas usuwania produktu o ID: " + id, e);
            if (tx.isActive()) {
                tx.rollback();
                logger.info("Transakcja wycofana (rollback) podczas usuwania produktu");
            }
        } finally {
            em.close();
            logger.info("EntityManager zamknięty po usunięciu produktu");
        }
    }

    /**
     * Aktualizuje istniejący stan magazynowy (produkt).
     *
     * @param produkt obiekt Warehouse z zaktualizowanymi danymi
     */
    public void aktualizujProdukt(Warehouse produkt) {
        logger.info("Aktualizacja produktu: " + produkt);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(produkt);
            tx.commit();
            logger.info("Produkt zaktualizowany pomyślnie: " + produkt);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Błąd podczas aktualizacji produktu: " + produkt, e);
            if (tx.isActive()) {
                tx.rollback();
                logger.info("Transakcja wycofana (rollback) podczas aktualizacji produktu");
            }
        } finally {
            em.close();
            logger.info("EntityManager zamknięty po aktualizacji produktu");
        }
    }

    /**
     * Zamyka fabrykę EntityManagerFactory, zwalniając wszystkie zasoby.
     * Po wywołaniu tej metody instancja repozytorium nie może być używana.
     */
    public void close() {
        if (emf.isOpen()) {
            emf.close();
            logger.info("EntityManagerFactory zamknięty w WarehouseRepository");
        }
    }
}
