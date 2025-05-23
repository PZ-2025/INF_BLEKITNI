/*
 * Classname: ProductRepository
 * Version information: 1.5
 * Date: 2025-05-23
 * Copyright notice: © BŁĘKITNI
 */

package org.example.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.sys.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repozytorium do obsługi operacji CRUD na encjach Product.
 * <p>
 * Zapewnia metody dodawania, wyszukiwania, aktualizacji oraz usuwania produktów
 * wraz z wieloma metodami filtrującymi po nazwie, cenie czy kategorii.
 * <p>
 * Logowanie zdarzeń i wyjątków realizowane jest przy pomocy biblioteki Log4j.
 */
public class ProductRepository {

    private static final Logger logger = LogManager.getLogger(ProductRepository.class);

    /**
     * Fabryka EntityManagerFactory do tworzenia EntityManagerów.
     */
    private final EntityManagerFactory emf;

    /**
     * Konstruktor inicjalizujący fabrykę EntityManagerFactory.
     */
    public ProductRepository() {
        logger.debug("Konstruktor ProductRepository() – tworzenie EMF");
        this.emf = Persistence.createEntityManagerFactory("myPU");
        logger.info("Utworzono ProductRepository, EMF={}", emf);
    }

    /**
     * Dodaje nowy produkt do bazy danych.
     *
     * @param product obiekt produktu do zapisania
     */
    public void addProduct(Product product) {
        logger.debug("addProduct() – start, product={}", product);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(product);
            tx.commit();
            logger.info("addProduct() – produkt dodany: {}", product);
        } catch (Exception e) {
            logger.error("addProduct() – błąd podczas dodawania produktu", e);
            if (tx.isActive()) {
                logger.warn("addProduct() – rollback transakcji");
                tx.rollback();
            }
        } finally {
            em.close();
            logger.debug("addProduct() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje produkt o podanym identyfikatorze.
     *
     * @param id identyfikator produktu
     * @return znaleziony produkt lub null, jeśli nie istnieje
     */
    public Product findProductById(int id) {
        logger.debug("findProductById() – start, id={}", id);
        EntityManager em = emf.createEntityManager();

        try {
            Product p = em.find(Product.class, id);
            logger.info("findProductById() – znaleziono: {}", p);
            return p;
        } catch (Exception e) {
            logger.error("findProductById() – błąd podczas wyszukiwania produktu o id={}", id, e);
            return null;
        } finally {
            em.close();
            logger.debug("findProductById() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera wszystkie produkty z bazy.
     *
     * @return lista wszystkich produktów, pusta lista w razie błędu
     */
    public List<Product> getAllProducts() {
        logger.debug("getAllProducts() – start");
        EntityManager em = emf.createEntityManager();

        try {
            List<Product> list = em.createQuery("SELECT p FROM Product p", Product.class)
                    .getResultList();
            logger.info("getAllProducts() – pobrano {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getAllProducts() – błąd podczas pobierania produktów", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getAllProducts() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera produkty z określonej kategorii.
     *
     * @param category nazwa kategorii
     * @return lista produktów w danej kategorii, pusta lista w razie błędu
     */
    public List<Product> getProductsByCategory(String category) {
        logger.debug("getProductsByCategory() – start, category={}", category);
        EntityManager em = emf.createEntityManager();

        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.category = :k", Product.class)
                    .setParameter("k", category)
                    .getResultList();
            logger.info("getProductsByCategory() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getProductsByCategory() – błąd podczas wyszukiwania produktów", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getProductsByCategory() – EntityManager zamknięty");
        }
    }

    /**
     * Usuwa produkt o wskazanym identyfikatorze.
     *
     * @param id identyfikator produktu do usunięcia
     */
    public void removeProduct(int id) {
        logger.debug("removeProduct() – start, id={}", id);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Product p = em.find(Product.class, id);
            if (p != null) {
                em.remove(p);
                logger.info("removeProduct() – usunięto produkt: {}", p);
            } else {
                logger.warn("removeProduct() – brak produktu o id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("removeProduct() – błąd podczas usuwania produktu o id={}", id, e);
            if (tx.isActive()) {
                logger.warn("removeProduct() – rollback transakcji");
                tx.rollback();
            }
        } finally {
            em.close();
            logger.debug("removeProduct() – EntityManager zamknięty");
        }
    }

    /**
     * Aktualizuje istniejący produkt.
     *
     * @param product obiekt produktu z nowymi danymi
     */
    public void updateProduct(Product product) {
        logger.debug("updateProduct() – start, product={}", product);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.merge(product);
            tx.commit();
            logger.info("updateProduct() – produkt zaktualizowany: {}", product);
        } catch (Exception e) {
            logger.error("updateProduct() – błąd podczas aktualizacji produktu", e);
            if (tx.isActive()) {
                logger.warn("updateProduct() – rollback transakcji");
                tx.rollback();
            }
        } finally {
            em.close();
            logger.debug("updateProduct() – EntityManager zamknięty");
        }
    }

    /**
     * Aktualizuje cenę produktu.
     *
     * @param id       identyfikator produktu
     * @param minPrice nowa cena ≥ 0
     */
    public void updateProductPrice(int id, BigDecimal minPrice) {
        logger.debug("updateProductPrice() – start, id={}, minPrice={}", id, minPrice);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Product p = em.find(Product.class, id);
            if (p != null && minPrice.compareTo(BigDecimal.ZERO) >= 0) {
                p.setPrice(minPrice);
                em.merge(p);
                logger.info("updateProductPrice() – cena zaktualizowana: {}", p);
            } else {
                logger.warn("updateProductPrice() – niepoprawne dane lub brak produktu id={}", id);
            }
            tx.commit();
        } catch (Exception e) {
            logger.error("updateProductPrice() – błąd podczas aktualizacji ceny", e);
            if (tx.isActive()) {
                logger.warn("updateProductPrice() – rollback transakcji");
                tx.rollback();
            }
        } finally {
            em.close();
            logger.debug("updateProductPrice() – EntityManager zamknięty");
        }
    }

    /**
     * Usuwa wszystkie produkty z danej kategorii.
     *
     * @param category nazwa kategorii
     * @return liczba usuniętych rekordów
     */
    public int removeProductsFromCategory(String category) {
        logger.debug("removeProductsFromCategory() – start, category={}", category);
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            int removed = em.createQuery(
                            "DELETE FROM Product p WHERE p.category = :k")
                    .setParameter("k", category)
                    .executeUpdate();
            tx.commit();
            logger.info("removeProductsFromCategory() – usunięto {} produktów", removed);
            return removed;
        } catch (Exception e) {
            logger.error("removeProductsFromCategory() – błąd podczas usuwania produktów", e);
            if (tx.isActive()) {
                logger.warn("removeProductsFromCategory() – rollback transakcji");
                tx.rollback();
            }
            return 0;
        } finally {
            em.close();
            logger.debug("removeProductsFromCategory() – EntityManager zamknięty");
        }
    }

    /**
     * Pobiera produkty w zadanym przedziale cenowym.
     *
     * @param minPrice cena minimalna
     * @param maxPrice cena maksymalna
     * @return lista produktów, pusta lista w razie błędu
     */
    public List<Product> getPriceRangeProducts(BigDecimal minPrice, BigDecimal maxPrice) {
        logger.debug("getPriceRangeProducts() – start, minPrice={}, maxPrice={}", minPrice, maxPrice);
        EntityManager em = emf.createEntityManager();

        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.price BETWEEN :min AND :max",
                            Product.class)
                    .setParameter("min", minPrice)
                    .setParameter("max", maxPrice)
                    .getResultList();
            logger.info("getPriceRangeProducts() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getPriceRangeProducts() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getPriceRangeProducts() – EntityManager zamknięty");
        }
    }

    /**
     * Zwraca listę wszystkich unikalnych kategorii produktów.
     *
     * @return lista kategorii lub pusta lista w razie błędu
     */
    public List<String> getCategories() {
        logger.debug("getCategories() – start");
        EntityManager em = emf.createEntityManager();

        try {
            List<String> list = em.createQuery(
                            "SELECT DISTINCT p.category FROM Product p", String.class)
                    .getResultList();
            logger.info("getCategories() – znaleziono {} kategorii", list.size());
            return list;
        } catch (Exception e) {
            logger.error("getCategories() – błąd podczas pobierania kategorii", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("getCategories() – EntityManager zamknięty");
        }
    }

    // === DODATKOWE METODY WYSZUKIWANIA ===

    /**
     * Znajduje produkty zawierające dany fragment w nazwie.
     *
     * @param fragName fragment nazwy
     * @return lista produktów, pusta lista w razie błędu
     */
    public List<Product> findByName(String fragName) {
        logger.debug("findByName() – start, fragName={}", fragName);
        EntityManager em = emf.createEntityManager();

        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :f, '%'))",
                            Product.class)
                    .setParameter("f", fragName)
                    .getResultList();
            logger.info("findByName() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByName() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByName() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje produkty o dokładnej cenie.
     *
     * @param price dokładna wartość ceny
     * @return lista produktów, pusta lista w razie błędu
     */
    public List<Product> findByExactPrice(BigDecimal price) {
        logger.debug("findByExactPrice() – start, price={}", price);
        EntityManager em = emf.createEntityManager();

        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.price = :c", Product.class)
                    .setParameter("c", price)
                    .getResultList();
            logger.info("findByExactPrice() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByExactPrice() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByExactPrice() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje produkty o cenie nie mniejszej niż podana.
     *
     * @param minPrice minimalna cena
     * @return lista produktów, pusta lista w razie błędu
     */
    public List<Product> findByMinPrice(BigDecimal minPrice) {
        logger.debug("findByMinPrice() – start, minPrice={}", minPrice);
        EntityManager em = emf.createEntityManager();

        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.price >= :min", Product.class)
                    .setParameter("min", minPrice)
                    .getResultList();
            logger.info("findByMinPrice() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByMinPrice() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByMinPrice() – EntityManager zamknięty");
        }
    }

    /**
     * Znajduje produkty o cenie nie większej niż podana.
     *
     * @param maxPrice maksymalna cena
     * @return lista produktów, pusta lista w razie błędu
     */
    public List<Product> findByMaxPrice(BigDecimal maxPrice) {
        logger.debug("findByMaxPrice() – start, maxPrice={}", maxPrice);
        EntityManager em = emf.createEntityManager();

        try {
            List<Product> list = em.createQuery(
                            "SELECT p FROM Product p WHERE p.price <= :max", Product.class)
                    .setParameter("max", maxPrice)
                    .getResultList();
            logger.info("findByMaxPrice() – znaleziono {} produktów", list.size());
            return list;
        } catch (Exception e) {
            logger.error("findByMaxPrice() – błąd podczas wyszukiwania", e);
            return List.of();
        } finally {
            em.close();
            logger.debug("findByMaxPrice() – EntityManager zamknięty");
        }
    }

    /**
     * Zamyka fabrykę EntityManagerFactory.
     */
    public void close() {
        logger.debug("close() – zamykanie EMF");
        if (emf.isOpen()) {
            emf.close();
            logger.info("close() – EMF zamknięty");
        }
    }
}
