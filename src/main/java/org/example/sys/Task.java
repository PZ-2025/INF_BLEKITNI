/*
 * Classname: Task
 * Version information: 1.0
 * Date: 2025-04-27
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import jakarta.persistence.*;
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

    private String priorytet;

    /**
     * Konstruktor bezparametrowy wymagany przez JPA.
     */
    public Task() {
        // Pusty konstruktor
    }

    /**
     * Konstruktor pełny.
     *
     * @param nazwa  nazwa zadania
     * @param data   termin wykonania
     * @param status status zadania
     * @param opis   opis zadania
     * @param priorytet priorytet zadania
     */
    public Task(String nazwa, Date data, String status, String opis, String priorytet) {
        this.nazwa = nazwa;
        this.data = data;
        this.status = status;
        this.opis = opis;
        this.priorytet = priorytet;
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

    public String getPriorytet() { return priorytet; }

    public void setPriorytet(String priorytet) { this.priorytet = priorytet; }

    /**
     * Zwraca reprezentację tekstową zadania.
     *
     * @return opis zadania
     */
    @Override
    public String toString() {
        return String.format(
                "Zadanie: %s, Termin: %s, Priorytet: %s",
                nazwa,
                data != null ? data.toString() : "brak daty",
                priorytet != null ? priorytet : "brak"
        );
    }
}
