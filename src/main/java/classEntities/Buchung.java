package classEntities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="buchung")
public class Buchung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="buchungId")
    private int buchungId;

    @Column(name="buchungstyp")
    private String buchungstyp;

    @Column(name="buchungsdatum")
    private LocalDateTime buchungsdatum;

    @Column(name="zahlungsstatus")
    private String zahlungsstatus;
    
    @Column(name ="preis")
    private float preis;

    @ManyToOne
    @JoinColumn(name = "benutzerId", referencedColumnName = "benutzerId")
    private Benutzer benutzer;

    @OneToMany(mappedBy = "buchung", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flug> fluege = new ArrayList<>();
    
    @OneToOne(mappedBy = "buchung", cascade = CascadeType.ALL, orphanRemoval = true)
    private Zahlung zahlung;
    
    @OneToMany(mappedBy = "buchung", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hotel> hotels = new ArrayList<>();
    
    @OneToMany(mappedBy = "buchung", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transfer> transfer = new ArrayList<>();

    public List<Transfer> getTransfer() {
        return transfer;
    }

    public void setTransfer(List<Transfer> transfer) {
        this.transfer = transfer;
    }

    public List<Hotel> getHotels() {
        return hotels;
    }

    public void setHotels(List<Hotel> hotels) {
        this.hotels = hotels;
    }

    public Buchung() {}

    // Konstruktor mit Buchungstyp, Buchungsdatum und Zahlungsstatus
    public Buchung(String buchungstyp, LocalDateTime buchungsdatum, String zahlungsstatus) {
        this.buchungstyp = buchungstyp;
        this.buchungsdatum = buchungsdatum;
        this.zahlungsstatus = zahlungsstatus;
    }
    
    public int getBuchungId() {
        return buchungId;
    }

    public void setBuchungId(int buchungId) {
        this.buchungId = buchungId;
    }

    public String getBuchungstyp() {
        return buchungstyp;
    }

    public void setBuchungstyp(String buchungstyp) {
        this.buchungstyp = buchungstyp;
    }

    public LocalDateTime getBuchungsdatum() {
        return buchungsdatum;
    }

    public void setBuchungsdatum(LocalDateTime buchungsdatum) {
        this.buchungsdatum = buchungsdatum;
    }

    public String getZahlungsstatus() {
        return zahlungsstatus;
    }

    public void setZahlungsstatus(String zahlungsstatus) {
        this.zahlungsstatus = zahlungsstatus;
    }

    public Benutzer getBenutzer() {
        return benutzer;
    }

    public void setBenutzer(Benutzer benutzer) {
        this.benutzer = benutzer;
    }

    public List<Flug> getFluege() {
        return fluege;
    }

    public void setFluege(List<Flug> fluege) {
        this.fluege = fluege;
    }

    public Zahlung getZahlung() {
        return zahlung;
    }

    public void setZahlung(Zahlung zahlung) {
        this.zahlung = zahlung;
        zahlung.setBuchung(this);  // Setzt die Buchung in der Zahlung
    }

    // Methode zum Hinzufügen eines Flugs zur Buchung
    public void addFlug(Flug flug) {
        fluege.add(flug);
        flug.setBuchung(this);  // Setzt die Buchung im Flug
    }

    public float getPreis() {
        return preis;
    }

    public void setPreis(float preis) {
        this.preis = preis;
    }
    
    @Override
    public String toString() {
        LocalDateTime buchungsDatum = getBuchungsdatum();
        String buchungsTyp = getBuchungstyp();
        float preis = getPreis();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String formattedDate = buchungsDatum.format(dateFormatter);
        String formattedTime = buchungsDatum.format(timeFormatter);

        return String.format(buchungsTyp + " am %s um %s Uhr - Preis %.2f €", formattedDate, formattedTime, preis);
    }
}
