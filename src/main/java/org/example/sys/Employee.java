package org.example.sys;

import jakarta.persistence.*;
import org.example.wyjatki.SalaryException;

import org.example.sys.Address;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Pracownicy")
@Access(AccessType.FIELD)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String imie;
    private String nazwisko;
    private int wiek;

    @ManyToOne
    @JoinColumn(name = "Id_adresu")
    private Address adres;

    private String login;
    private String haslo;

    @Column(name = "Zarobki", precision = 10, scale = 2)
    private BigDecimal zarobki;

    @Column(name = "Stanowisko")
    private String stanowisko;

    @OneToMany(mappedBy = "pracownik", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> zadania = new ArrayList<>();

    // Pola związane z urlopem
    private boolean onSickLeave;
    private Date sickLeaveStartDate;
    public Employee() {
        // Konstruktor bezparametrowy wymagany przez JPA
    }
    public Employee(String imie, String nazwisko, int wiek, Address address, String login, String haslo, String stanowisko, BigDecimal zarobki) throws SalaryException {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.wiek = wiek;
        this.login = login;
        this.haslo = haslo;
        this.stanowisko = stanowisko;
        setZarobki(zarobki);  // używamy settera dla walidacji
        this.onSickLeave = false;
        this.sickLeaveStartDate = null;
        this.adres = address;
    }

    // Gettery i settery
    public int getId() { return id; }

    public String getImie() { return imie; }
    public void setImie(String imie) { this.imie = imie; }

    public String getNazwisko() { return nazwisko; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }

    public int getWiek() { return wiek; }
    public void setWiek(int wiek) { this.wiek = wiek; }

    public Address getAdres() { return adres; }
    public void setAdres(Address adres) { this.adres = adres; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getHaslo() { return haslo; }
    public void setHaslo(String haslo) { this.haslo = haslo; }

    public BigDecimal getZarobki() { return zarobki; }
    public void setZarobki(BigDecimal zarobki) throws SalaryException {
        if(zarobki.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SalaryException("Wynagrodzenie musi być większe od zera");
        }
        this.zarobki = zarobki;
    }

    public List<Task> getZadania() {
        return zadania;
    }

    public void addTask(Task zadanie) {
        zadania.add(zadanie);
    }

    public void removeTask(Task zadanie) {
        zadania.remove(zadanie);
    }

    public String getStanowisko() { return stanowisko; }
    public void setStanowisko(String stanowisko) { this.stanowisko = stanowisko; }

    // Metody związane z urlopem
    public boolean isOnSickLeave() { return onSickLeave; }
    public void startSickLeave(Date startDate) {
        this.sickLeaveStartDate = startDate;
        this.onSickLeave = true;
    }
}