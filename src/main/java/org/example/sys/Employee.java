/*
 * Classname: Employee
 * Version information: 1.0
 * Date: 2025-05-16
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.*;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.example.wyjatki.NameException;
import org.example.wyjatki.AgeException;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Klasa reprezentująca pracownika w systemie.
 */
@Entity
@Table(name = "Pracownicy")
@Access(AccessType.FIELD)
public class Employee extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Login", nullable = false)
    private String login;

    @Column(name = "Haslo", nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "Id_adresu")
    private Address adres;

    @Column(name = "Zarobki", precision = 10, scale = 2, nullable = false)
    private BigDecimal zarobki;

    @Column(name = "Stanowisko", nullable = false)
    private String stanowisko;

    @Column(name = "onSickLeave", nullable = false)
    private boolean onSickLeave;

    @Column(name = "sickLeaveStartDate")
    @Temporal(TemporalType.DATE)
    private Date sickLeaveStartDate;

    // Dodane pole do usuwania miękkiego
    @Column(name = "usuniety", nullable = false)
    private boolean usuniety = false;

    public Employee() {}

    public Employee(String name, String surname, int age, String email,
                    String login, String password, Address adres,
                    String stanowisko, BigDecimal zarobki)
            throws NameException, AgeException, PasswordException, SalaryException {
        super(name, surname, age, email);
        setLogin(login);
        setPassword(password);
        this.adres = adres;
        this.stanowisko = stanowisko;
        setZarobki(zarobki);
        this.onSickLeave = false;
    }

    public Employee(String name, String surname, int age, Address adres,
                    String login, String password, String stanowisko, BigDecimal zarobki)
            throws NameException, AgeException, SalaryException, PasswordException {
        super(name, surname, age, null);
        this.adres = adres;
        setLogin(login);
        setPassword(password);
        this.stanowisko = stanowisko;
        setZarobki(zarobki);
        this.onSickLeave = false;
        this.sickLeaveStartDate = null;
    }

    // Getter i setter dla pola 'usuniety'
    public boolean isUsuniety() {
        return usuniety;
    }

    public void setUsuniety(boolean usuniety) {
        this.usuniety = usuniety;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        if (login == null || login.isEmpty()) {
            throw new IllegalArgumentException("Login nie może być pusty");
        }
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) throws PasswordException {
        if (password == null || password.length() < 8) {
            throw new PasswordException("Hasło musi mieć co najmniej 8 znaków");
        }
        this.password = password;
    }

    public Address getAdres() {
        return adres;
    }

    public void setAdres(Address adres) {
        this.adres = adres;
    }

    public BigDecimal getZarobki() {
        return zarobki;
    }

    public void setZarobki(BigDecimal zarobki) throws SalaryException {
        if (zarobki == null || zarobki.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SalaryException("Zarobki muszą być większe od zera");
        }
        this.zarobki = zarobki;
    }

    public String getStanowisko() {
        return stanowisko;
    }

    public void setStanowisko(String stanowisko) {
        this.stanowisko = stanowisko;
    }

    public boolean isOnSickLeave() {
        return onSickLeave;
    }

    public void startSickLeave(Date startDate) {
        this.sickLeaveStartDate = startDate;
        this.onSickLeave = true;
    }

    /**
     * Sprawdza, czy pracownik ma rolę "root".
     *
     * @return true jeśli pracownik ma rolę "root", false w przeciwnym przypadku
     */
    public boolean isRoot() {
        return "root".equalsIgnoreCase(this.stanowisko);
    }

    public Date getSickLeaveStartDate() {
        return sickLeaveStartDate;
    }

    public void endSickLeave() {
        this.onSickLeave = false;
        this.sickLeaveStartDate = null;
    }
}