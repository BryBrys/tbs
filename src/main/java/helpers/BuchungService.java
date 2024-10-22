package helpers;
import classEntities.Transfer;
import classEntities.Benutzer;
import classEntities.Buchung;
import classEntities.Flug;
import classEntities.Hotel;
import classEntities.Zahlung;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BuchungService {

	private SessionFactory sessionFactory;

	public BuchungService() {
		try {
			this.sessionFactory = HibernateUtil.getSessionFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveOrder(String bookingType, String paymentStatus, double preis, FlightData flightData, HotelData hotelData, TransferData transferData, String paymentMethod, Benutzer loggedInUser) {
		if (loggedInUser == null) {
			throw new IllegalArgumentException("Eingeloggter Benutzer darf nicht null sein.");
		}

		// Log
		System.out.println("Aktueller Benutzer: " + loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
		System.out.println("Buchungstyp: " + bookingType + ", Preis: " + preis);

		Session session = sessionFactory.getCurrentSession();

		try {
			session.beginTransaction();

			// Erstellen eines neuen Buchung-Objekts und Festlegen der gemeinsamen Felder
			Buchung buchung = new Buchung();
			buchung.setBuchungstyp(bookingType);
			buchung.setZahlungsstatus(paymentStatus);
			buchung.setBuchungsdatum(LocalDateTime.now());
			buchung.setBenutzer(loggedInUser);
			buchung.setPreis((float) preis);  // Setzt das 'preis'-Feld

			// Erstellen eines neuen Zahlung-Objekts und Festlegen der Zahlungsdetails
			Zahlung zahlung = new Zahlung();
			zahlung.setZahlungsmethode(paymentMethod);
			zahlung.setStatus(paymentStatus);
			zahlung.setBetrag(preis);
			zahlung.setZahlungsdatum(LocalDateTime.now());
			zahlung.setBuchung(buchung);
			buchung.setZahlung(zahlung);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			// Verarbeitung von FlightData, falls vorhanden
			if (flightData != null) {
				Flug flug = new Flug();
				LocalDate abflugDatum = LocalDate.parse(flightData.getOutboundDepartureDate(), formatter);
				LocalDate rueckflugDatum = LocalDate.parse(flightData.getReturnDepartureDate(), formatter);

				flug.setAbflugDatum(abflugDatum);
				flug.setAbflugOrt(flightData.getOutboundDepartureIataCode());
				flug.setZielOrt(flightData.getOutboundArrivalIataCode());
				flug.setRueckflugDatum(rueckflugDatum);
				flug.setPreis(preis);

				flug.setBuchung(buchung);  // Verknüpfe den Flug mit der Buchung
				buchung.getFluege().add(flug);  // Füge den Flug zur Buchung hinzu
			}

			// Verarbeitung von HotelData, falls vorhanden
			if (hotelData != null) {
				Hotel hotel = new Hotel();
				LocalDate checkInDate = LocalDate.parse(hotelData.getCheckInDate(), formatter);
				LocalDate checkOutDate = LocalDate.parse(hotelData.getCheckOutDate(), formatter);

				hotel.setCheckinDatum(checkInDate);
				hotel.setCheckoutDatum(checkOutDate);
				hotel.setName(hotelData.getHotelName());
				hotel.setStandort(hotelData.getCityCode());
				hotel.setPreis(preis);

				hotel.setBuchung(buchung);  // Verknüpfe das Hotel mit der Buchung
				buchung.getHotels().add(hotel);
			}

			// Verarbeitung von TransferData, falls vorhanden
			if (transferData != null) {
				Transfer transfer = new Transfer();
				transfer.setMietDatum(LocalDate.parse(transferData.getPickUpDateTime().substring(0, 10), formatter));
				transfer.setStartOrt(transferData.getPickUpLocation());
				transfer.setPreis(preis);
				transfer.setZielOrt(transferData.getEndAddressLine() + "," + transferData.getEndCityName());

				transfer.setBuchung(buchung);
				buchung.getTransfer().add(transfer);
			}

			// Speichern der Buchung (zusammen mit dem verknüpften Flug, Hotel oder Transfer)
			session.persist(buchung);
			System.out.println("Buchung erfolgreich gespeichert.");
			session.getTransaction().commit();

		} catch (Exception e) {
			if (session.getTransaction() != null) {
				session.getTransaction().rollback();
			}
			System.out.println("Fehler beim Speichern der Buchung: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
				System.out.println("Session geschlossen.");
			}
		}
	}

	public void close() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}

}