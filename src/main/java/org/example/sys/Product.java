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

    @Column(name = "Cena", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "Ilosc", nullable = false)
    private int quantity;

    public Product() { }

    public Product(String name, String category, double price) {
        this(name, category, BigDecimal.valueOf(price));
    }

    public Product(String name, String category, BigDecimal price) {
        this.name = name;
        this.category = category;
        setPrice(price);
        this.quantity = 0; // domyślna ilość
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

    public int getQuantity() {
        return quantity;
    }

    /**
     * Ustawia ilość produktu tylko jeśli jest większa lub równa 0.
     */
    public void setQuantity(int nowaIlosc) {
        if (nowaIlosc >= 0) {
            this.quantity = nowaIlosc;
        }
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category='%s', price=%s, quantity=%d}",
                id, name, category, price, quantity);
    }
}
