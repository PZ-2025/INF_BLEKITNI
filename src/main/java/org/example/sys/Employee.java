/*
 * Classname: Employee
 * Version information: 1.1
 * Date: 2025-05-23
 * Copyright notice: © BŁĘKITNI
 */


package org.example.sys;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import org.example.wyjatki.PasswordException;
import org.example.wyjatki.SalaryException;
import org.example.wyjatki.NameException;
import org.example.wyjatki.AgeException;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;

import java.math.BigDecimal;
import java.util.Date;

@SqlResultSetMapping(
        name = "EmployeeWorkloadMapping",
        classes = @ConstructorResult(
                targetClass = pdf.WorkloadReportGenerator.EmployeeWorkload.class,
                columns = {
                        @ColumnResult(name = "employeeName", type = String.class),
                        @ColumnResult(name = "department", type = String.class),
                        @ColumnResult(name = "totalHours", type = Double.class)
                }
        )
)

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
    private Address address;

    @Column(name = "Zarobki", precision = 10, scale = 2, nullable = false)
    private BigDecimal salary;

    @Column(name = "Stanowisko", nullable = false)
    private String position;

    @Column(name = "onSickLeave", nullable = false)
    private boolean onSickLeave;

    @Column(name = "sickLeaveStartDate")
    @Temporal(TemporalType.DATE)
    private Date sickLeaveStartDate;

    // Dodane pole do usuwania miękkiego
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    /*
     * Konstruktor domyślny
     */
    public Employee() {}

    /**
     * Konstruktor z parametrami.
     *
     * @param name       Imię pracownika
     * @param surname    Nazwisko pracownika
     * @param age        Wiek pracownika
     * @param email      Adres e-mail pracownika
     * @param login      Login pracownika
     * @param password   Hasło pracownika
     * @param address      Adres pracownika
     * @param position Stanowisko pracownika
     * @param salary    Zarobki pracownika
     */
    public Employee(String name, String surname, int age, String email,
                    String login, String password, Address address,
                    String position, BigDecimal salary)
            throws NameException, AgeException, PasswordException, SalaryException {
        super(name, surname, age, email);
        setLogin(login);
        setPassword(password);
        this.address = address;
        this.position = position;
        setSalary(salary);
        this.onSickLeave = false;
    }

    /**
     * Konstruktor z parametrami.
     *
     * @param name       Imię pracownika
     * @param surname    Nazwisko pracownika
     * @param age        Wiek pracownika
     * @param address      Adres pracownika
     * @param login      Login pracownika
     * @param password   Hasło pracownika
     * @param position Stanowisko pracownika
     * @param salary    Zarobki pracownika
     */
    public Employee(String name, String surname, int age, Address address,
                    String login, String password, String position, BigDecimal salary)
            throws NameException, AgeException, SalaryException, PasswordException {
        super(name, surname, age, null);
        this.address = address;
        setLogin(login);
        setPassword(password);
        this.position = position;
        setSalary(salary);
        this.onSickLeave = false;
        this.sickLeaveStartDate = null;
    }

    // Getter i setter dla pola 'deleted'
    /**
     * Zwraca informację, czy pracownik został usunięty.
     *
     * @return true, jeśli pracownik został usunięty, false w przeciwnym razie
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Ustawia informację, czy pracownik został usunięty.
     *
     * @param deleted true, jeśli pracownik został usunięty, false w przeciwnym razie
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Zwraca identyfikator pracownika.
     *
     * @return Identyfikator pracownika
     */
    public int getId() {
        return id;
    }

    /**
     * Zwraca login pracownika.
     *
     * @return Login pracownika
     */
    public String getLogin() {
        return login;
    }

    /**
     * Ustawia login pracownika.
     *
     * @param login Login do ustawienia
     * @throws IllegalArgumentException jeśli login jest pusty
     */
    public void setLogin(String login) {
        if (login == null || login.isEmpty()) {
            throw new IllegalArgumentException("Login nie może być pusty");
        }
        this.login = login;
    }

    /**
     * Zwraca hasło pracownika.
     *
     * @return Hasło pracownika
     */
    public String getPassword() {
        return password;
    }

    /**
     * Ustawia hasło pracownika.
     *
     * @param password Hasło do ustawienia
     * @throws PasswordException jeśli hasło jest puste lub ma mniej niż 8 znaków
     */
    public void setPassword(String password) throws PasswordException {
        if (password == null || password.length() < 8) {
            throw new PasswordException("Hasło musi mieć co najmniej 8 znaków");
        }
        this.password = password;
    }

    /**
     * Zwraca address pracownika.
     *
     * @return Adres pracownika
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Ustawia address pracownika.
     *
     * @param address Adres do ustawienia
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Zwraca salary pracownika.
     *
     * @return Zarobki pracownika
     */
    public BigDecimal getSalary() {
        return salary;
    }

    /**
     * Ustawia salary pracownika.
     *
     * @param salary Zarobki do ustawienia
     * @throws SalaryException jeśli salary są mniejsze lub równe 0
     */
    public void setSalary(BigDecimal salary) throws SalaryException {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SalaryException("Zarobki muszą być większe od zera");
        }
        this.salary = salary;
    }

    /**
     * Zwraca position pracownika.
     *
     * @return Stanowisko pracownika
     */
    public String getPosition() {
        return position;
    }

    /**
     * Ustawia position pracownika.
     *
     * @param position Stanowisko do ustawienia
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * Zwraca informację, czy pracownik jest na zwolnieniu lekarskim.
     *
     * @return true, jeśli pracownik jest na zwolnieniu lekarskim, false w przeciwnym razie
     */
    public boolean isOnSickLeave() {
        return onSickLeave;
    }

    /**
     * Ustawia informację, czy pracownik jest na zwolnieniu lekarskim.
     *
     * @param startDate Data rozpoczęcia zwolnienia lekarskiego
     */
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
        return "root".equalsIgnoreCase(this.position);
    }

    /**
     * Zwraca datę końca zwolnienia lekarskiego.
     *
     * @return Data końca zwolnienia lekarskiego
     */
    public Date getSickLeaveStartDate() {
        return sickLeaveStartDate;
    }

    /**
     * Ustawia końca zwolnienia lekarskiego.
     */
    public void endSickLeave() {
        this.onSickLeave = false;
        this.sickLeaveStartDate = null;
    }

    public boolean isUsuniety() {
        return deleted;
    }

    public String getStanowisko() {
        return this.position;
    }
}