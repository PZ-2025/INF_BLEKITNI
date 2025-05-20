package org.example.database;

import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Address;

import java.util.List;

public class AddressRepository {
    private static final Logger logger = LogManager.getLogger(AddressRepository.class);
    private final EntityManagerFactory emf;

    public AddressRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono AddressRepository, EMF={}", emf);
    }

    public void dodajAdres(Address address) {
        logger.debug("dodajAdres() - start, address={}", address);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(address);
            tx.commit();
            logger.info("dodajAdres() - dodano adres: {}", address);
        } catch (Exception e) {
            logger.error("dodajAdres() - błąd podczas dodawania adresu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("dodajAdres() - EM zamknięty");
        }
    }

    public Address znajdzAdresPoId(int id) {
        logger.debug("znajdzAdresPoId() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Address a = em.find(Address.class, id);
            logger.info("znajdzAdresPoId() - znaleziono: {}", a);
            return a;
        } catch (Exception e) {
            logger.error("znajdzAdresPoId() - błąd podczas pobierania adresu o id=" + id, e);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzAdresPoId() - EM zamknięty");
        }
    }

    public List<Address> pobierzWszystkieAdresy() {
        logger.debug("pobierzWszystkieAdresy() - start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Address> list = em.createQuery("SELECT a FROM Address a", Address.class)
                    .getResultList();
            logger.info("pobierzWszystkieAdresy() - pobrano {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzWszystkieAdresy() - błąd podczas pobierania adresów", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzWszystkieAdresy() - EM zamknięty");
        }
    }

    public void usunAdres(int id) {
        logger.debug("usunAdres() - start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Address address = em.find(Address.class, id);
            if (address != null) {
                em.remove(address);
                logger.info("usunAdres() - usunięto adres: {}", address);
            } else {
                logger.warn("usunAdres() - brak adresu o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("usunAdres() - błąd podczas usuwania adresu o id=" + id, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("usunAdres() - EM zamknięty");
        }
    }

    public List<Address> znajdzPoMiejscowosci(String miejscowosc) {
        logger.debug("znajdzPoMiejscowosci() - miejscowosc={}", miejscowosc);
        EntityManager em = emf.createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a WHERE LOWER(a.miejscowosc) LIKE LOWER(CONCAT('%', :miejscowosc, '%'))",
                            Address.class)
                    .setParameter("miejscowosc", miejscowosc)
                    .getResultList();
            logger.info("znajdzPoMiejscowosci() - znaleziono {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoMiejscowosci() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoMiejscowosci() - EM zamknięty");
        }
    }

    public List<Address> znajdzPoNumerzeDomu(String numerDomu) {
        logger.debug("znajdzPoNumerzeDomu() - numerDomu={}", numerDomu);
        EntityManager em = emf.createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a WHERE a.numerDomu = :numerDomu",
                            Address.class)
                    .setParameter("numerDomu", numerDomu)
                    .getResultList();
            logger.info("znajdzPoNumerzeDomu() - znaleziono {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoNumerzeDomu() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoNumerzeDomu() - EM zamknięty");
        }
    }

    public List<Address> znajdzPoNumerzeMieszkania(String numerMieszkania) {
        logger.debug("znajdzPoNumerzeMieszkania() - numerMieszkania={}", numerMieszkania);
        EntityManager em = emf.createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a WHERE a.numerMieszkania = :numerMieszkania",
                            Address.class)
                    .setParameter("numerMieszkania", numerMieszkania)
                    .getResultList();
            logger.info("znajdzPoNumerzeMieszkania() - znaleziono {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoNumerzeMieszkania() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoNumerzeMieszkania() - EM zamknięty");
        }
    }

    public List<Address> znajdzPoKodPocztowym(String kodPocztowy) {
        logger.debug("znajdzPoKodPocztowym() - kodPocztowy={}", kodPocztowy);
        EntityManager em = emf.createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a WHERE a.kodPocztowy = :kodPocztowy",
                            Address.class)
                    .setParameter("kodPocztowy", kodPocztowy)
                    .getResultList();
            logger.info("znajdzPoKodPocztowym() - znaleziono {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoKodPocztowym() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoKodPocztowym() - EM zamknięty");
        }
    }

    public List<Address> znajdzPoMiescie(String miasto) {
        logger.debug("znajdzPoMiescie() - miasto={}", miasto);
        EntityManager em = emf.createEntityManager();
        try {
            List<Address> list = em.createQuery(
                            "SELECT a FROM Address a WHERE LOWER(a.miasto) = LOWER(:miasto)",
                            Address.class)
                    .setParameter("miasto", miasto)
                    .getResultList();
            logger.info("znajdzPoMiescie() - znaleziono {} adresów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoMiescie() - błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoMiescie() - EM zamknięty");
        }
    }

    public void close() {
        logger.debug("close() - zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() - EMF zamknięty");
        }
    }
}
