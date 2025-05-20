package org.example.sys;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Raporty")
@Access(AccessType.FIELD)
public class Raport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Typ_raportu", nullable = false)
    private String typRaportu;

    @Column(name = "Data_poczatku", nullable = false)
    private LocalDate dataPoczatku;

    @Column(name = "Data_zakonczenia", nullable = false)
    private LocalDate dataZakonczenia;

    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee pracownik;

    @Column(name = "Plik", nullable = false)
    private String sciezkaPliku;

    public Raport() {}

    public Raport(String typRaportu, LocalDate dataPoczatku, LocalDate dataZakonczenia, Employee pracownik, String sciezkaPliku) {
        this.typRaportu = typRaportu;
        this.dataPoczatku = dataPoczatku;
        this.dataZakonczenia = dataZakonczenia;
        this.pracownik = pracownik;
        this.sciezkaPliku = sciezkaPliku;
    }

    public int getId() {
        return id;
    }

    public String getTypRaportu() {
        return typRaportu;
    }

    public void setTypRaportu(String typRaportu) {
        this.typRaportu = typRaportu;
    }

    public LocalDate getDataPoczatku() {
        return dataPoczatku;
    }

    public void setDataPoczatku(LocalDate dataPoczatku) {
        this.dataPoczatku = dataPoczatku;
    }

    public LocalDate getDataZakonczenia() {
        return dataZakonczenia;
    }

    public void setDataZakonczenia(LocalDate dataZakonczenia) {
        this.dataZakonczenia = dataZakonczenia;
    }

    public Employee getPracownik() {
        return pracownik;
    }

    public void setPracownik(Employee pracownik) {
        this.pracownik = pracownik;
    }

    public String getSciezkaPliku() {
        return sciezkaPliku;
    }

    public void setSciezkaPliku(String sciezkaPliku) {
        this.sciezkaPliku = sciezkaPliku;
    }
}
