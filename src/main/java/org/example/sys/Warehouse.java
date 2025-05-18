/*
 * Classname: Warehouse
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Produkty")
@Access(AccessType.FIELD)
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Nazwa", nullable = false, length = 100)
    private String nazwa;

    @Column(name = "Cena", precision = 10, scale = 2, nullable = false)
    private BigDecimal cena;

    @Column(name = "IloscWmagazynie", nullable = false)
    private int iloscWmagazynie;

    public Warehouse() {}

    public Warehouse(String nazwa, BigDecimal cena, int iloscWmagazynie) {
        this.nazwa = nazwa;
        this.cena = cena;
        this.iloscWmagazynie = iloscWmagazynie;
    }

    // === Gettery i settery ===

    public int getId() {
        return id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public BigDecimal getCena() {
        return cena;
    }

    public void setCena(BigDecimal cena) {
        this.cena = cena;
    }

    public int getIloscWmagazynie() {
        return iloscWmagazynie;
    }

    public void setIloscWmagazynie(int iloscWmagazynie) {
        this.iloscWmagazynie = iloscWmagazynie;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "id=" + id +
                ", nazwa='" + nazwa + '\'' +
                ", cena=" + cena +
                ", iloscWmagazynie=" + iloscWmagazynie +
                '}';
    }
}
