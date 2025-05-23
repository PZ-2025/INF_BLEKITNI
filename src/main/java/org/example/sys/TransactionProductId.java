/*
 * Classname: TransactionProductId
 * Version information: 1.0
 * Date: 2025-05-20
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TransactionProductId implements Serializable {

    @Column(name = "Id_transakcji")
    private int transactionId;

    @Column(name = "Id_produktu")
    private int productId;

    // Konstruktory
    public TransactionProductId() {
    }

    public TransactionProductId(int transactionId, int productId) {
        this.transactionId = transactionId;
        this.productId = productId;
    }

    // Gettery i settery
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    // Equals i hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionProductId that = (TransactionProductId) o;
        return transactionId == that.transactionId && productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, productId);
    }
}