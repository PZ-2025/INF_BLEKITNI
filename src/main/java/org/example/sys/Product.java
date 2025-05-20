package org.example.sys;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Produkty")
@Access(AccessType.FIELD)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Nazwa", nullable = false, length = 100)
    private String name;

    @Column(name = "Kategoria", nullable = false, length = 100)
    private String category;

    // Zmieniamy typ na BigDecimal
    @Column(name = "Cena", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public Product() {}

    public Product(String name, String category, double price) {
        this.name = name;
        this.category = category;
        // konwersja z double na BigDecimal
        this.price = BigDecimal.valueOf(price);
    }

    // === Gettery i settery ===

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price != null && price.compareTo(BigDecimal.ZERO) >= 0) {
            this.price = price;
        }
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category='%s', price=%.2f}",
                id, name, category, price);
    }
}
