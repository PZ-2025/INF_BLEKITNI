/*
 * Classname: Transaction
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Transakcje")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "Id_pracownika")
    private Employee pracownik;

    @Temporal(TemporalType.DATE)
    private Date data;

    @ManyToMany
    @JoinTable(
            name = "Transakcje_Produkty",
            joinColumns = @JoinColumn(name = "Id_transakcji"),
            inverseJoinColumns = @JoinColumn(name = "Id_produktu")
    )
    private Set<Warehouse> produkty = new HashSet<>();

    // === Gettery i settery ===

    public int getId() {
        return id;
    }

    public Employee getPracownik() {
        return pracownik;
    }

    public void setPracownik(Employee pracownik) {
        this.pracownik = pracownik;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Set<Warehouse> getProdukty() {
        return produkty;
    }

    public void setProdukty(Set<Warehouse> produkty) {
        this.produkty = produkty;
    }
}
