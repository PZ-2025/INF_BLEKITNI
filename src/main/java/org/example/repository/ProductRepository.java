package org.example.repository;

import org.example.sys.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ProductRepository {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");

    public List<Product> getAllProducts() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Product p", Product.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void close() {
        if (emf.isOpen()) emf.close();
    }
}
