/*
 * Classname: Address
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Adresy")
@Access(AccessType.FIELD)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String miejscowosc;
    private String numerDomu;
    private String numerMieszkania;
    private String kodPocztowy;
    private String miasto;

    @OneToMany(mappedBy = "adres")
    private List<Employee> pracownicy;

    // Gettery i settery
    public int getId() { return id; }

    public String getMiejscowosc() { return miejscowosc; }
    public void setMiejscowosc(String miejscowosc) { this.miejscowosc = miejscowosc; }

    public String getNumerDomu() { return numerDomu; }
    public void setNumerDomu(String numerDomu) { this.numerDomu = numerDomu; }

    public String getNumerMieszkania() { return numerMieszkania; }
    public void setNumerMieszkania(String numerMieszkania) { this.numerMieszkania = numerMieszkania; }

    public String getKodPocztowy() { return kodPocztowy; }
    public void setKodPocztowy(String kodPocztowy) { this.kodPocztowy = kodPocztowy; }

    public String getMiasto() { return miasto; }
    public void setMiasto(String miasto) { this.miasto = miasto; }
}