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
@Table(name="hotel")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hotelId")
    private int hotelId;

    @Column(name="name")
    private String name;

    @Column(name="standort")
    private String standort;

    @Column(name="checkinDatum")
    private LocalDate checkinDatum;

    @Column(name="checkoutDatum")
    private LocalDate checkoutDatum;

    @Column(name="preis")
    private double preis;

    @ManyToOne
    @JoinColumn(name = "buchungId")
    private Buchung buchung;


    public Hotel() {}

    public Hotel(String name, String standort, LocalDate checkinDatum, LocalDate checkoutDatum, double preis) {
        this.name = name;
        this.standort = standort;
        this.checkinDatum = checkinDatum;
        this.checkoutDatum = checkoutDatum;
        this.preis = preis;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStandort() {
        return standort;
    }

    public void setStandort(String standort) {
        this.standort = standort;
    }

    public LocalDate getCheckinDatum() {
        return checkinDatum;
    }

    public void setCheckinDatum(LocalDate checkinDatum) {
        this.checkinDatum = checkinDatum;
    }

    public LocalDate getCheckoutDatum() {
        return checkoutDatum;
    }

    public void setCheckoutDatum(LocalDate checkoutDatum) {
        this.checkoutDatum = checkoutDatum;
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

