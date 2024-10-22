package classEntities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="zahlung")
public class Zahlung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="zahlungsId")
    private int zahlungsId;

    @Column(name="zahlungsmethode")
    private String zahlungsmethode;

    @Column(name="status")
    private String status;

    @Column(name="betrag")
    private double betrag;

    @Column(name="zahlungsdatum")
    private LocalDateTime zahlungsdatum;

    @OneToOne
    @JoinColumn(name = "buchungId")
    private Buchung buchung;

    public Zahlung() {}

    public Zahlung(String zahlungsmethode, String status, double betrag, LocalDateTime zahlungsdatum) {
        this.zahlungsmethode = zahlungsmethode;
        this.status = status;
        this.betrag = betrag;
        this.zahlungsdatum = zahlungsdatum;
    }

    public int getZahlungsId() {
        return zahlungsId;
    }

    public void setZahlungsId(int zahlungsId) {
        this.zahlungsId = zahlungsId;
    }

    public String getZahlungsmethode() {
        return zahlungsmethode;
    }

    public void setZahlungsmethode(String zahlungsmethode) {
        this.zahlungsmethode = zahlungsmethode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getBetrag() {
        return betrag;
    }

    public void setBetrag(double betrag) {
        this.betrag = betrag;
    }

    public LocalDateTime getZahlungsdatum() {
        return zahlungsdatum;
    }

    public void setZahlungsdatum(LocalDateTime zahlungsdatum) {
        this.zahlungsdatum = zahlungsdatum;
    }

    public Buchung getBuchung() {
        return buchung;
    }

    public void setBuchung(Buchung buchung) {
        this.buchung = buchung;
    }
}

