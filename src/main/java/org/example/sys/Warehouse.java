package org.example.sys;

import jakarta.persistence.*;

@Entity
@Table(name = "StanyMagazynowe")
@Access(AccessType.FIELD)
public class Warehouse {

    @Id
    @Column(name = "Id_produktu")
    private int productId;

    @OneToOne
    @JoinColumn(name = "Id_produktu", insertable = false, updatable = false)
    private Product product;

    @Column(name = "Ilosc", nullable = false)
    private int quantity;

    public Warehouse() {
    }

    public Warehouse(Product product, int quantity) {
        this.product = product;
        this.productId = product.getId(); // synchronizacja z kluczem głównym
        this.quantity = quantity;
    }

    // === Gettery i settery ===

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product.getId();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "product=" + (product != null ? product.getName() : "null") +
                ", quantity=" + quantity +
                '}';
    }
}