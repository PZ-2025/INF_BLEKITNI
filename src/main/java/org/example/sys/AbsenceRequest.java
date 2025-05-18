/*
 * Classname: AbsenceRequest
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Wnioski_o_nieobecnosc")
@Access(AccessType.FIELD)
public class AbsenceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Typ_wniosku", nullable = false)
    private String typWniosku;

    @Column(name = "Data_rozpoczecia", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataRozpoczecia;

    @Column(name = "Data_zakonczenia", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataZakonczenia;

    @Column(name = "Opis")
    private String opis;

    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee pracownik;

    // === Konstruktory ===
    public AbsenceRequest() {}

    public AbsenceRequest(String typWniosku, Date dataRozpoczecia, Date dataZakonczenia, String opis, Employee pracownik) {
        this.typWniosku = typWniosku;
        this.dataRozpoczecia = dataRozpoczecia;
        this.dataZakonczenia = dataZakonczenia;
        this.opis = opis;
        this.pracownik = pracownik;
    }

    // === Gettery i settery ===
    public int getId() {
        return id;
    }

    public String getTypWniosku() {
        return typWniosku;
    }

    public void setTypWniosku(String typWniosku) {
        this.typWniosku = typWniosku;
    }

    public Date getDataRozpoczecia() {
        return dataRozpoczecia;
    }

    public void setDataRozpoczecia(Date dataRozpoczecia) {
        this.dataRozpoczecia = dataRozpoczecia;
    }

    public Date getDataZakonczenia() {
        return dataZakonczenia;
    }

    public void setDataZakonczenia(Date dataZakonczenia) {
        this.dataZakonczenia = dataZakonczenia;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public Employee getPracownik() {
        return pracownik;
    }

    public void setPracownik(Employee pracownik) {
        this.pracownik = pracownik;
    }

    @Override
    public String toString() {
        return String.format(
                "AbsenceRequest{id=%d, typ='%s', od=%s, do=%s, opis='%s', pracownik=%s %s}",
                id, typWniosku, dataRozpoczecia, dataZakonczenia, opis,
                pracownik != null ? pracownik.getName() : "null",
                pracownik != null ? pracownik.getSurname() : ""
        );
    }
}
