/*
 * Classname: Task
 * Version information: 1.0
 * Date: 2025-04-27
 * Copyright notice: © BŁĘKITNI
 */

package org.example.sys;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    // Relacja wiele-do-wielu z Employee (Pracownicy)
    @ManyToMany
    @JoinTable(
            name = "Zadania_Pracownicy",
            joinColumns = @JoinColumn(name = "Id_zadania"),
            inverseJoinColumns = @JoinColumn(name = "Id_pracownika")
    )
    private Set<Employee> employees = new HashSet<>();

    public Task() {
        // Pusty konstruktor wymagany przez JPA
    }

    public Task(String nazwa, Date data, String status, String opis) {
        this.nazwa = nazwa;
        this.data = data;
        this.status = status;
        this.opis = opis;
    }

    // Gettery i settery

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

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    // Przydatne metody do obsługi relacji

    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.getTasks().add(this);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employee.getTasks().remove(this);
    }

    @Override
    public String toString() {
        return String.format("Zadanie: %s, Termin: %s",
                nazwa,
                data != null ? data.toString() : "brak daty");
    }
}
