/*
 * Classname: TransactionProduct
 * Version information: 1.0
 * Date: 2025-05-20
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MapsId;

@Entity
@Table(name = "Transakcje_Produkty")
public class TransactionProduct {

    @EmbeddedId
    private TransactionProductId id;

    @ManyToOne
    @MapsId("transactionId")
    @JoinColumn(name = "Id_transakcji")
    private Transaction transaction;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "Id_produktu")
    private Product product;

    @Column(name = "Ilosc", nullable = false)
    private int quantity;

    // Konstruktory
    public TransactionProduct() {
    }

    public TransactionProduct(Transaction transaction, Product product, int quantity) {
        this.id = new TransactionProductId(transaction.getId(), product.getId());
        this.transaction = transaction;
        this.product = product;
        this.quantity = quantity;
    }

    // Gettery i settery
    public TransactionProductId getId() {
        return id;
    }

    public void setId(TransactionProductId id) {
        this.id = id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}