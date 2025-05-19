package org.example.pdflib;

import java.time.LocalDate;

public class TaskRecord {
    private long id;
    private String opis;
    private String stanowisko;
    private LocalDate data;
    private String priorytet;

    public TaskRecord(long id, String opis, String stanowisko, LocalDate data, String priorytet) {
        this.id = id;
        this.opis = opis;
        this.stanowisko = stanowisko;
        this.data = data;
        this.priorytet = priorytet;
    }

    // Gettery
    public long getId() { return id; }
    public String getOpis() { return opis; }
    public String getStanowisko() { return stanowisko; }
    public LocalDate getData() { return data; }
    public String getPriorytet() { return priorytet; }
}
