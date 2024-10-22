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
@Table(name="transfer")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="transferId")
    private int transferId;

    @Column(name="startOrt")
    private String startOrt;

    @Column(name="zielOrt")
    private String zielOrt;

    @Column(name="mietDatum")
    private LocalDate mietDatum;

    @Column(name="preis")
    private double preis;

    @ManyToOne
    @JoinColumn(name = "buchungId")
    private Buchung buchung;

    public Transfer(String startOrt, String zielOrt, LocalDate mietDatum, double preis) {
        this.startOrt = startOrt;
        this.zielOrt = zielOrt;
        this.mietDatum = mietDatum;
        this.preis = preis;
    }

    public Transfer() {}

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public String getStartOrt() {
        return startOrt;
    }

    public void setStartOrt(String startOrt) {
        this.startOrt = startOrt;
    }

    public String getZielOrt() {
        return zielOrt;
    }

    public void setZielOrt(String zielOrt) {
        this.zielOrt = zielOrt;
    }

    public LocalDate getMietDatum() {
        return mietDatum;
    }

    public void setMietDatum(LocalDate mietDatum) {
        this.mietDatum = mietDatum;
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

	@Override
	public String toString() {
		return "Transfer [transferId=" + transferId + ", startOrt=" + startOrt + ", zielOrt=" + zielOrt + ", mietDatum="
				+ mietDatum + ", preis=" + preis + ", buchung=" + buchung + "]";
	}

}
