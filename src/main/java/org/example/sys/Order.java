package org.example.sys;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

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
    private Product produkt;

    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee pracownik;

    @Column(name = "Ilosc", nullable = false)
    private int ilosc;

    @Column(name = "Cena", nullable = false, precision = 10, scale = 2)
    private BigDecimal cena;

    @Temporal(TemporalType.DATE)
    @Column(name = "Data", nullable = false)
    private Date data;

    public Order() {
    }

    public Order(Product produkt, Employee pracownik, int ilosc, BigDecimal cena, Date data) {
        this.produkt = produkt;
        this.pracownik = pracownik;
        this.ilosc = ilosc;
        this.cena = cena;
        this.data = data;
    }

    // === Gettery i settery ===

    public int getId() {
        return id;
    }

    public Product getProdukt() {
        return produkt;
    }

    public void setProdukt(Product produkt) {
        this.produkt = produkt;
    }

    public Employee getPracownik() {
        return pracownik;
    }

    public void setPracownik(Employee pracownik) {
        this.pracownik = pracownik;
    }

    public int getIlosc() {
        return ilosc;
    }

    public void setIlosc(int ilosc) {
        this.ilosc = ilosc;
    }

    public BigDecimal getCena() {
        return cena;
    }

    public void setCena(BigDecimal cena) {
        this.cena = cena;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", produkt=" + (produkt != null ? produkt.getName() : "null") +
                ", pracownik=" + (pracownik != null ? pracownik.getLogin() : "null") +
                ", ilosc=" + ilosc +
                ", cena=" + cena +
                ", data=" + data +
                '}';
    }
}
