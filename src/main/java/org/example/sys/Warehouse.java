package org.example.sys;

import jakarta.persistence.*;

@Entity
@Table(name = "StanyMagazynowe")
@Access(AccessType.FIELD)
public class Warehouse {

    @Id
    @Column(name = "Id_produktu")
    private int idProduktu;

    @OneToOne
    @JoinColumn(name = "Id_produktu", insertable = false, updatable = false)
    private Product produkt;

    @Column(name = "Ilosc", nullable = false)
    private int ilosc;

    public Warehouse() {
    }

    public Warehouse(Product produkt, int ilosc) {
        this.produkt = produkt;
        this.idProduktu = produkt.getId(); // synchronizacja z kluczem głównym
        this.ilosc = ilosc;
    }

    // === Gettery i settery ===

    public int getIdProduktu() {
        return idProduktu;
    }

    public void setIdProduktu(int idProduktu) {
        this.idProduktu = idProduktu;
    }

    public Product getProdukt() {
        return produkt;
    }

    public void setProdukt(Product produkt) {
        this.produkt = produkt;
        this.idProduktu = produkt.getId();
    }

    public int getIlosc() {
        return ilosc;
    }

    public void setIlosc(int ilosc) {
        this.ilosc = ilosc;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "produkt=" + (produkt != null ? produkt.getName() : "null") +
                ", ilosc=" + ilosc +
                '}';
    }
}
