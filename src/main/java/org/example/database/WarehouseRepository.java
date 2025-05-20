package org.example.database;

import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Warehouse;

import java.util.List;

public class WarehouseRepository {
    private static final Logger logger = LogManager.getLogger(WarehouseRepository.class);
    private final EntityManagerFactory emf;

    public WarehouseRepository() {
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono WarehouseRepository, EMF={}", emf);
    }

    public void dodajStanMagazynowy(Warehouse stan) {
        logger.debug("dodajStanMagazynowy() – start, stan={}", stan);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(stan);
            tx.commit();
            logger.info("dodajStanMagazynowy() – dodano stan: {}", stan);
        } catch (Exception e) {
            logger.error("dodajStanMagazynowy() – błąd podczas dodawania stanu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("dodajStanMagazynowy() – EM zamknięty");
        }
    }

    public Warehouse znajdzStanPoIdProduktu(int idProduktu) {
        logger.debug("znajdzStanPoIdProduktu() – start, idProduktu={}", idProduktu);
        EntityManager em = emf.createEntityManager();
        try {
            Warehouse stan = em.find(Warehouse.class, idProduktu);
            logger.info("znajdzStanPoIdProduktu() – znaleziono: {}", stan);
            return stan;
        } catch (Exception e) {
            logger.error("znajdzStanPoIdProduktu() – błąd podczas wyszukiwania idProduktu={}", idProduktu, e);
            return null;
        } finally {
            em.close();
            logger.debug("znajdzStanPoIdProduktu() – EM zamknięty");
        }
    }

    public List<Warehouse> pobierzWszystkieStany() {
        logger.debug("pobierzWszystkieStany() – start");
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery("SELECT w FROM Warehouse w", Warehouse.class)
                    .getResultList();
            logger.info("pobierzWszystkieStany() – pobrano {} rekordów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("pobierzWszystkieStany() – błąd podczas pobierania wszystkich stanów", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("pobierzWszystkieStany() – EM zamknięty");
        }
    }

    public void usunStan(int idProduktu) {
        logger.debug("usunStan() – start, idProduktu={}", idProduktu);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Warehouse stan = em.find(Warehouse.class, idProduktu);
            if (stan != null) {
                em.remove(stan);
                logger.info("usunStan() – usunięto stan: {}", stan);
            } else {
                logger.warn("usunStan() – brak rekordu dla idProduktu={}", idProduktu);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("usunStan() – błąd podczas usuwania idProduktu={}", idProduktu, e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("usunStan() – EM zamknięty");
        }
    }

    public void aktualizujStan(Warehouse stan) {
        logger.debug("aktualizujStan() – start, stan={}", stan);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(stan);
            tx.commit();
            logger.info("aktualizujStan() – zaktualizowano stan: {}", stan);
        } catch (Exception e) {
            logger.error("aktualizujStan() – błąd podczas aktualizacji stanu", e);
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
            logger.debug("aktualizujStan() – EM zamknięty");
        }
    }

    public List<Warehouse> znajdzPoIlosci(int ilosc) {
        logger.debug("znajdzPoIlosci() – ilosc={}", ilosc);
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w WHERE w.ilosc = :ilosc", Warehouse.class)
                    .setParameter("ilosc", ilosc)
                    .getResultList();
            logger.info("znajdzPoIlosci() – znaleziono {} rekordów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoIlosci() – błąd dla ilosc={}", ilosc, e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoIlosci() – EM zamknięty");
        }
    }

    public List<Warehouse> znajdzPoIlosciMniejszejNiz(int max) {
        logger.debug("znajdzPoIlosciMniejszejNiz() – max={}", max);
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w WHERE w.ilosc < :max", Warehouse.class)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("znajdzPoIlosciMniejszejNiz() – znaleziono {} rekordów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoIlosciMniejszejNiz() – błąd dla max={}", max, e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoIlosciMniejszejNiz() – EM zamknięty");
        }
    }

    public List<Warehouse> znajdzPoIlosciWiekszejNiz(int min) {
        logger.debug("znajdzPoIlosciWiekszejNiz() – min={}", min);
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w WHERE w.ilosc > :min", Warehouse.class)
                    .setParameter("min", min)
                    .getResultList();
            logger.info("znajdzPoIlosciWiekszejNiz() – znaleziono {} rekordów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoIlosciWiekszejNiz() – błąd dla min={}", min, e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoIlosciWiekszejNiz() – EM zamknięty");
        }
    }

    public List<Warehouse> znajdzPoIlosciWMiedzy(int min, int max) {
        logger.debug("znajdzPoIlosciWMiedzy() – min={}, max={}", min, max);
        EntityManager em = emf.createEntityManager();
        try {
            List<Warehouse> list = em.createQuery(
                            "SELECT w FROM Warehouse w WHERE w.ilosc BETWEEN :min AND :max", Warehouse.class)
                    .setParameter("min", min)
                    .setParameter("max", max)
                    .getResultList();
            logger.info("znajdzPoIlosciWMiedzy() – znaleziono {} rekordów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("znajdzPoIlosciWMiedzy() – błąd dla min={}, max={}", min, max, e);
            return List.of();
        } finally {
            em.close();
            logger.debug("znajdzPoIlosciWMiedzy() – EM zamknięty");
        }
    }

    public void close() {
        logger.debug("close() – zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() – EMF zamknięty");
        }
    }
}
