/*
 * Classname: TechnicalIssue
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

/**
 * Klasa reprezentująca zgłoszenie techniczne w systemie.
 * Zawiera informacje o typie zgłoszenia, opisie, dacie zgłoszenia, pracowniku oraz statusie.
 */
@Entity
@Table(name = "Zgloszenia_techniczne")
public class TechnicalIssue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Typ", nullable = false)
    private String type; // "Awaria sprzętu", "Błąd oprogramowania", "Inne"

    @Column(name = "Opis", columnDefinition = "TEXT")
    private String description;

    @Column(name = "Data_zgłoszenia")
    private LocalDate dateSubmitted;

    @ManyToOne
    @JoinColumn(name = "Id_pracownika", referencedColumnName = "Id")
    private Employee employee;

    @Column(name = "Status", length = 50)
    private String status = "Nowe"; // Domyślnie "Nowe"

    // Konstruktor bez ID (dla nowych zgłoszeń)

    /**
     *
     * @param type
     * @param description
     * @param dateSubmitted
     * @param employee
     * @param status
     */
    public TechnicalIssue(String type, String description, LocalDate dateSubmitted, Employee employee, String status) {
        this.type = type;
        this.description = description;
        this.dateSubmitted = dateSubmitted;
        this.employee = employee;
        this.status = status;
    }

    // Konstruktor z ID (dla istniejących zgłoszeń)
    /**
     * Konstruktor z ID (dla istniejących zgłoszeń)
     *
     * @param id
     * @param type
     * @param description
     * @param dateSubmitted
     * @param employee
     * @param status
     */
    public TechnicalIssue(int id, String type, String description, LocalDate dateSubmitted, Employee employee, String status) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.dateSubmitted = dateSubmitted;
        this.employee = employee;
        this.status = status;
    }

    // Konstruktor domyślny (wymagany przez JPA)
    /**
     * Domyślny konstruktor wymagany przez JPA.
     * Używany do tworzenia instancji klasy przez Hibernate.
     */
    public TechnicalIssue() {}

    // Gettery i settery
    /**
     * Zwraca identyfikator zgłoszenia.
     *
     * @return Identyfikator zgłoszenia
     */
    public int getId() { return id; }

    /**
     * Ustawia identyfikator zgłoszenia.
     *
     * @param id Identyfikator zgłoszenia
     */
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }

    /**
     * Ustawia typ zgłoszenia.
     *
     * @param type Typ zgłoszenia
     */
    public void setType(String type) { this.type = type; }

    /**
     * Zwraca opis zgłoszenia.
     *
     * @return Opis zgłoszenia
     */
    public String getDescription() { return description; }

    /**
     * Ustawia opis zgłoszenia.
     *
     * @param description Opis zgłoszenia
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Zwraca datę zgłoszenia.
     *
     * @return Data zgłoszenia
     */
    public LocalDate getDateSubmitted() { return dateSubmitted; }

    /**
     * Ustawia datę zgłoszenia.
     *
     * @param dateSubmitted Data zgłoszenia
     */
    public void setDateSubmitted(LocalDate dateSubmitted) { this.dateSubmitted = dateSubmitted; }

    /**
     * Zwraca pracownika zgłaszającego problem.
     *
     * @return Pracownik zgłaszający problem
     */
    public Employee getEmployee() { return employee; }

    /**
     * Ustawia pracownika zgłaszającego problem.
     *
     * @param employee Pracownik zgłaszający problem
     */
    public void setEmployee(Employee employee) { this.employee = employee; }

    /**
     * Zwraca status zgłoszenia.
     *
     * @return Status zgłoszenia
     */
    public String getStatus() { return status; }

    /**
     * Ustawia status zgłoszenia.
     *
     * @param status Status zgłoszenia
     */
    public void setStatus(String status) { this.status = status; }
}