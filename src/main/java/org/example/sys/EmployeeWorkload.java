package org.example.sys;

import jakarta.persistence.*;

@Entity
public class EmployeeWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String imie;
    private String nazwisko;
    private String stanowisko;
    private int liczbaZadan;

    // Konstruktory
    public EmployeeWorkload() {}

    public EmployeeWorkload(String imie, String nazwisko, String stanowisko, int liczbaZadan) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.stanowisko = stanowisko;
        this.liczbaZadan = liczbaZadan;
    }

    // Gettery i settery
    public int getId() { return id; }

    public String getImie() { return imie; }

    public void setImie(String imie) { this.imie = imie; }

    public String getNazwisko() { return nazwisko; }

    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }

    public String getStanowisko() { return stanowisko; }

    public void setStanowisko(String stanowisko) { this.stanowisko = stanowisko; }

    public int getLiczbaZadan() { return liczbaZadan; }

    public void setLiczbaZadan(int liczbaZadan) { this.liczbaZadan = liczbaZadan; }
}
