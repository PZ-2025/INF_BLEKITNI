/*
 * Classname: Task
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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

    private String name;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Czas trwania zmiany pracownika przy zadaniu
     */
    @Column(name = "czas_trwania_zmiany")
    private LocalTime durationOfTheShift;

    /** Konstruktor bezparametrowy wymagany przez JPA. */
    public Task() {
    }

    /**
     * Konstruktor pełny (z czasem zmiany).
     *
     * @param name               name zadania
     * @param date                termin wykonania
     * @param status              status zadania
     * @param description                description zadania
     * @param durationOfTheShift   czas trwania zmiany przy zadaniu
     */
    public Task(String name, Date date, String status, String description, LocalTime durationOfTheShift) {
        this.name = name;
        this.date = date;
        this.status = status;
        this.description = description;
        this.durationOfTheShift = durationOfTheShift;
    }

    // ==================== Gettery i Settery ====================

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalTime getDurationOfTheShift() {
        return durationOfTheShift;
    }

    public void setDurationOfTheShift(LocalTime durationOfTheShift) {
        this.durationOfTheShift = durationOfTheShift;
    }

    /**
     * Zwraca reprezentację tekstową zadania.
     */
    @Override
    public String toString() {
        return String.format(
                "Zadanie: %s, Termin: %s, Czas zmiany: %s",
                name,
                date != null ? date.toString() : "brak daty",
                durationOfTheShift != null ? durationOfTheShift.toString() : "brak"
        );
    }
}
