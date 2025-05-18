/*
 * Classname: TechnicalIssue
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.*;
import org.example.sys.Employee;

import java.time.LocalDate;

@Entity
@Table(name = "Zgłoszenia_techniczne")
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
    public TechnicalIssue(String type, String description, LocalDate dateSubmitted, Employee employee, String status) {
        this.type = type;
        this.description = description;
        this.dateSubmitted = dateSubmitted;
        this.employee = employee;
        this.status = status;
    }

    // Konstruktor z ID (dla istniejących zgłoszeń)
    public TechnicalIssue(int id, String type, String description, LocalDate dateSubmitted, Employee employee, String status) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.dateSubmitted = dateSubmitted;
        this.employee = employee;
        this.status = status;
    }

    // Konstruktor domyślny (wymagany przez JPA)
    public TechnicalIssue() {}

    // Gettery i settery
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDateSubmitted() { return dateSubmitted; }
    public void setDateSubmitted(LocalDate dateSubmitted) { this.dateSubmitted = dateSubmitted; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}