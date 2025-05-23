/*
 * Classname: Report
 * Version information: 1.1
 * Date: 2025-05-22
 * Copyright notice: © BŁĘKITNI
 */

// src/main/java/org/example/sys/Report.java
package org.example.sys;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

/**
 * Klasa reprezentująca raport w systemie.
 * Zawiera informacje o typie raportu, dacie początkowej, dacie zakończenia,
 * pracowniku oraz ścieżce do pliku raportu.
 */
@Entity
@Table(name = "Raporty")
@Access(AccessType.FIELD)
public class Report {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Typ_raportu", nullable = false)
    private String reportType;

    @Column(name = "Data_poczatku", nullable = false)
    private LocalDate startDate;

    @Column(name = "Data_zakonczenia", nullable = false)
    private LocalDate endDate;

    @ManyToOne @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee employee;

    @Column(name = "Plik", nullable = false)
    private String filePath;

    /**
     * Domyślny konstruktor.
     * Używany przez Hibernate do tworzenia instancji klasy.
     */
    public Report() {}

    /**
     * Konstruktor z parametrami.
     * Umożliwia ustawienie typu raportu, daty początkowej, daty zakończenia,
     * pracownika oraz ścieżki do pliku raportu.
     *
     * @param reportType   Typ raportu
     * @param startDate    Data początkowa
     * @param endDate      Data zakończenia
     * @param employee     Pracownik
     * @param filePath     Ścieżka do pliku raportu
     */
    public Report(String reportType, LocalDate startDate,
                  LocalDate endDate, Employee employee, String filePath) {
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employee = employee;
        this.filePath = filePath;
    }

    /* --- gettery / settery --- */
    /**
     * Zwraca identyfikator raportu.
     *
     * @return Identyfikator raportu
     */
    public int getId(){
        return id;
    }

    /**
     * Zwraca typ raportu.
     *
     * @return Typ raportu
     */
    public String getReportType(){
        return reportType;
    }
    /**
     * Ustawia typ raportu.
     *
     * @param t Typ raportu
     */
    public void setReportType(String t){
        this.reportType = t;
    }

    /**
     * Zwraca datę początkową raportu.
     *
     * @return Data początkowa
     */
    public LocalDate getStartDate(){
        return startDate;
    }

    /**
     * Ustawia datę początkową raportu.
     *
     * @param d Data początkowa
     */
    public void setStartDate(LocalDate d){
        this.startDate = d;
    }

    /**
     * Zwraca datę zakończenia raportu.
     *
     * @return Data zakończenia
     */
    public LocalDate getEndDate(){
        return endDate;
    }

    /**
     * Ustawia datę zakończenia raportu.
     *
     * @param d Data zakończenia
     */
    public void setEndDate(LocalDate d){
        this.endDate = d;
    }

    /**
     * Zwraca pracownika, który wygenerował raport.
     *
     * @return Pracownik
     */
    public Employee getEmployee(){
        return employee;
    }

    /**
     * Ustawia pracownika, który wygenerował raport.
     *
     * @param p Pracownik
     */
    public void setEmployee(Employee p){
        this.employee = p;
    }

    /**
     * Zwraca ścieżkę do pliku raportu.
     *
     * @return Ścieżka do pliku
     */
    public String getFilePath(){
        return filePath;
    }

    /**
     * Ustawia ścieżkę do pliku raportu.
     *
     * @param p Ścieżka do pliku
     */
    public void setFilePath(String p){
        this.filePath = p;
    }

    public String getSciezkaPliku() {
        String katalogProjektu = System.getProperty("user.dir");
        return katalogProjektu + "\\reports";
    }
}

