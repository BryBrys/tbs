package classEntities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "flug")
public class Flug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int flugId;

    @Column(name = "abflugOrt")
    private String abflugOrt;

    @Column(name = "zielOrt")
    private String zielOrt;

    @Column(name = "abflugDatum", columnDefinition = "DATE")
    private LocalDate abflugDatum;

    @Column(name = "rueckflugDatum", columnDefinition = "DATE")
    private LocalDate rueckflugDatum;

    @Column(name = "preis")
    private double preis;

    @ManyToOne
    @JoinColumn(name = "buchungId")
    private Buchung buchung;

    public Flug() {}

    public Flug(String abflugOrt, String zielOrt, LocalDate abflugDatum, LocalDate rueckflugDatum, double preis) {
        this.abflugOrt = abflugOrt;
        this.zielOrt = zielOrt;
        this.abflugDatum = abflugDatum;
        this.rueckflugDatum = rueckflugDatum;
        this.preis = preis;
    }

    public int getFlugId() {
        return flugId;
    }

    public void setFlugId(int flugId) {
        this.flugId = flugId;
    }

    public String getAbflugOrt() {
        return abflugOrt;
    }

    public void setAbflugOrt(String abflugOrt) {
        this.abflugOrt = abflugOrt;
    }

    public String getZielOrt() {
        return zielOrt;
    }

    public void setZielOrt(String zielOrt) {
        this.zielOrt = zielOrt;
    }

    public LocalDate getAbflugDatum() {
        return abflugDatum;
    }

    public void setAbflugDatum(LocalDate abflugDatum) {
        this.abflugDatum = abflugDatum;
    }

    public LocalDate getRueckflugDatum() {
        return rueckflugDatum;
    }

    public void setRueckflugDatum(LocalDate rueckflugDatum) {
        this.rueckflugDatum = rueckflugDatum;
    }

    public double getPreis() {
        return preis;
    }

    public void setPreis(double preis) {
        this.preis = preis;
    }

    public Buchung getBuchung() {
        return buchung;
    }

    public void setBuchung(Buchung buchung) {
        this.buchung = buchung;
    }
}

