package org.example.sys;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Wnioski_o_nieobecnosc")
@Access(AccessType.FIELD)
public class AbsenceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "Typ_wniosku", length = 100, nullable = false)
    private String typWniosku;

    @Column(name = "Data_rozpoczecia", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataRozpoczecia;

    @Column(name = "Data_zakonczenia", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dataZakonczenia;

    @Column(name = "Opis", columnDefinition = "TEXT")
    private String opis;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private StatusWniosku status = StatusWniosku.OCZEKUJE; // domyślna wartość

    @ManyToOne
    @JoinColumn(name = "Id_pracownika", nullable = false)
    private Employee pracownik;

    // Enum odpowiadający możliwym statusom w tabeli SQL
    public enum StatusWniosku {
        OCZEKUJE("Oczekuje"),
        NIEPRZYJETY("Nie przyjęty"),
        PRZYJETY("Przyjęty");

        private final String value;

        StatusWniosku(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public AbsenceRequest() {}

    public AbsenceRequest(String typWniosku, Date dataRozpoczecia, Date dataZakonczenia,
                          String opis, Employee pracownik, StatusWniosku status) {
        this.typWniosku = typWniosku;
        this.dataRozpoczecia = dataRozpoczecia;
        this.dataZakonczenia = dataZakonczenia;
        this.opis = opis;
        this.pracownik = pracownik;
        this.status = status;
    }

    // === Gettery i settery ===
    public int getId() {
        return id;
    }

    public String getTypWniosku() {
        return typWniosku;
    }

    public void setTypWniosku(String typWniosku) {
        this.typWniosku = typWniosku;
    }

    public Date getDataRozpoczecia() {
        return dataRozpoczecia;
    }

    public void setDataRozpoczecia(Date dataRozpoczecia) {
        this.dataRozpoczecia = dataRozpoczecia;
    }

    public Date getDataZakonczenia() {
        return dataZakonczenia;
    }

    public void setDataZakonczenia(Date dataZakonczenia) {
        this.dataZakonczenia = dataZakonczenia;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public Employee getPracownik() {
        return pracownik;
    }

    public void setPracownik(Employee pracownik) {
        this.pracownik = pracownik;
    }

    public StatusWniosku getStatus() {
        return status;
    }

    public void setStatus(StatusWniosku status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format(
                "AbsenceRequest{id=%d, typ='%s', od=%s, do=%s, opis='%s', status='%s', pracownik=%s %s}",
                id, typWniosku, dataRozpoczecia, dataZakonczenia, opis, status,
                pracownik != null ? pracownik.getName() : "null",
                pracownik != null ? pracownik.getSurname() : ""
        );
    }
}