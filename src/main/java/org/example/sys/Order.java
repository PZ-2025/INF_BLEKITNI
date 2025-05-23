/*
 * Classname: Order
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Klasa reprezentująca zamówienie w systemie.
 * Zawiera informacje o produkcie, pracowniku, ilości, cenie i dacie zamówienia.
 */
@Entity
@Table(name = "Zamowienia")
@Access(AccessType.FIELD)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "Id_produktu", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee employee;

    @Column(name = "Ilosc", nullable = false)
    private int quantity;

    @Column(name = "Cena", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Temporal(TemporalType.DATE)
    @Column(name = "Data", nullable = false)
    private Date date;

    /**
     * Domyślny konstruktor.
     */
    public Order() {
    }

    /**
     * Konstruktor z parametrami.
     *
     * @param product  Produkt zamówienia
     * @param employee Pracownik realizujący zamówienie
     * @param quantity    Ilość zamówionego produktu
     * @param price     Cena zamówienia
     * @param date     Data zamówienia
     */
    public Order(Product product, Employee employee, int quantity, BigDecimal price, Date date) {
        this.product = product;
        this.employee = employee;
        this.quantity = quantity;
        this.price = price;
        this.date = date;
    }

    // === Gettery i settery ===

    /**
     * Zwraca id zamówienia.
     *
     * @return Identyfikator zamówienia
     */
    public int getId() {
        return id;
    }

    /**
     * Zwraca product zamówienia.
     *
     * @return product Produkt zamówienia
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Ustawia product zamówienia.
     *
     * @param product Produkt zamówienia
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Zwraca pracownika realizującego zamówienie.
     *
     * @return employee Pracownik realizujący zamówienie
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Ustawia pracownika realizującego zamówienie.
     *
     * @param employee Pracownik realizujący zamówienie
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    /**
     * Zwraca ilość zamówionego produktu.
     *
     * @return quantity Ilość zamówionego produktu
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Ustawia ilość zamówionego produktu.
     *
     * @param quantity Ilość zamówionego produktu
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Zwraca cenę zamówienia.
     *
     * @return price Cena zamówienia
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Ustawia cenę zamówienia.
     *
     * @param price Cena zamówienia
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Zwraca datę zamówienia.
     *
     * @return date Data zamówienia
     */
    public Date getDate() {
        return date;
    }

    /**
     * Ustawia datę zamówienia.
     *
     * @param date Data zamówienia
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Zwraca reprezentację tekstową zamówienia.
     *
     * @return Reprezentacja tekstowa zamówienia
     */
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", product=" + (product != null ? product.getName() : "null") +
                ", employee=" + (employee != null ? employee.getLogin() : "null") +
                ", quantity=" + quantity +
                ", price=" + price +
                ", date=" + date +
                '}';
    }
}
