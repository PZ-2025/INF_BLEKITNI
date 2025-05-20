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

    @Column(name = "Miejscowosc")
    private String miejscowosc;

    @Column(name = "Numer_domu")
    private String numerDomu;

    @Column(name = "Numer_mieszkania")
    private String numerMieszkania;

    @Column(name = "Kod_pocztowy")
    private String kodPocztowy;

    @Column(name = "Miasto")
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

    @Override
    public String toString() {
        String mieszkanie = (numerMieszkania != null && !numerMieszkania.isEmpty())
                ? "/" + numerMieszkania
                : "";
        return miejscowosc + ", ul. " + numerDomu + mieszkanie + ", " + kodPocztowy + " " + miasto;
    }
}