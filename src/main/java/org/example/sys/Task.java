package org.example.sys;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.Date;

/**
 * Reprezentuje zadanie w systemie.
 */
@Entity
@Table(name = "Zadania")
@Access(AccessType.FIELD)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nazwa;

    @Temporal(TemporalType.DATE)
    private Date data;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String opis;

    /**
     * Czas trwania zmiany pracownika przy zadaniu
     */
    @Column(name = "czas_trwania_zmiany")
    private LocalTime czasTrwaniaZmiany;

    /** Konstruktor bezparametrowy wymagany przez JPA. */
    public Task() {
    }

    /**
     * Konstruktor pełny (bez czasu zmiany).
     *
     * @param nazwa  nazwa zadania
     * @param data   termin wykonania
     * @param status status zadania
     * @param opis   opis zadania
     */
    public Task(String nazwa, Date data, String status, String opis) {
        this.nazwa = nazwa;
        this.data = data;
        this.status = status;
        this.opis = opis;
    }

    /**
     * Konstruktor pełny (z czasem zmiany).
     *
     * @param nazwa               nazwa zadania
     * @param data                termin wykonania
     * @param status              status zadania
     * @param opis                opis zadania
     * @param czasTrwaniaZmiany   czas trwania zmiany przy zadaniu
     */
    public Task(String nazwa, Date data, String status, String opis, LocalTime czasTrwaniaZmiany) {
        this.nazwa = nazwa;
        this.data = data;
        this.status = status;
        this.opis = opis;
        this.czasTrwaniaZmiany = czasTrwaniaZmiany;
    }

    // ==================== Gettery i Settery ====================

    public int getId() {
        return id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public LocalTime getCzasTrwaniaZmiany() {
        return czasTrwaniaZmiany;
    }

    public void setCzasTrwaniaZmiany(LocalTime czasTrwaniaZmiany) {
        this.czasTrwaniaZmiany = czasTrwaniaZmiany;
    }

    /**
     * Zwraca reprezentację tekstową zadania.
     */
    @Override
    public String toString() {
        return String.format(
                "Zadanie: %s, Termin: %s, Czas zmiany: %s",
                nazwa,
                data != null ? data.toString() : "brak daty",
                czasTrwaniaZmiany != null ? czasTrwaniaZmiany.toString() : "brak"
        );
    }
}
