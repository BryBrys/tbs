package com.login.apilogin;

import java.time.LocalDate;

import classEntities.Benutzer;
import helpers.BraintreeService;
import helpers.BuchungService;
import helpers.FlightData;
import helpers.HotelData;
import helpers.SessionManager; // SessionManager importieren
import helpers.TransferData;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PaymentController {

	@FXML
	private TextField txt_cardNumber;
	@FXML
	private TextField txt_expiryDate;
	@FXML
	private TextField txt_cvc;
	@FXML
	private Button btn_cancelPayment;
	@FXML
	private Button btn_pay;
	@FXML
	private Label price_label;

	private FlightData flight;
	private HotelData hotel;
	private TransferData transfer;

	private BuchungService buchungService = new BuchungService(); // Erstellen einer Instanz von BuchungService
	
	private BuchungenController buchungenController;  // Referenz zu BuchungenController für Aktuallisierung der Buchungsliste
	

	// Methode zum Setzen des BuchungenController
	public void setBuchungenController(BuchungenController buchungenController) {
	    this.buchungenController = buchungenController;
	}

	// Methode zum Setzen der Flugdaten
	public void setFlightDetails(FlightData flight) {
		this.flight = flight;
		this.hotel = null; // Lösche Hoteldaten, falls ein Flug gesetzt wird
		this.transfer = null;
		price_label.setText(String.format("Preis: %.2f €", flight.getPrice()));
	}

	// Methode zum Setzen der Hoteldaten
	public void setHotelDetails(HotelData hotel) {
		this.hotel = hotel;
		this.flight = null; // Lösche Flugdaten, falls ein Hotel gesetzt wird
		this.transfer = null;

		// Sicherstellen, dass das Hotelobjekt nicht null ist und der Preis korrekt gesetzt ist
		if (hotel != null) {
			System.out.println("Hotel Preis: " + hotel.getPrice());  // Debugging - Überprüfen, ob der Preis korrekt ist
			price_label.setText(String.format("Preis: %.2f €", hotel.getPrice()));
		}
	}

	// Methode zum Setzen der Transferdaten
	public void setTransferDetails(TransferData transfer) {
		this.transfer = transfer;
		this.flight = null; 
		this.hotel = null; 

		if (transfer != null) {
			System.out.println("Transfer Preis: " + transfer.getPrice()); 
			price_label.setText(String.format("Preis: %.2f €", transfer.getPrice()));
		}
	}

	// Methode zum Schließen des Zahlungsfensters
	@FXML
	private void handleCancelPayment() {
		Stage stage = (Stage) btn_cancelPayment.getScene().getWindow();
		stage.close();
	}

	// Methode für den Zahlungsprozess
	@FXML
	private void handlePayment() {
		// Zahlung Details erfassen
		String cardNumber = txt_cardNumber.getText().trim();
		String expiryDate = txt_expiryDate.getText().trim();
		String cvc = txt_cvc.getText().trim();

		// Eingaben validieren
		if (cardNumber.isEmpty() || expiryDate.isEmpty() || cvc.isEmpty()) {
			showErrorMessage("Bitte füllen Sie alle Zahlungsfelder aus.");
			return;
		}

		// Ablaufdatum validieren (Format MM/YY)
		if (!expiryDate.matches("\\d{2}/\\d{2}")) {
			showErrorMessage("Ungültiges Ablaufdatum. Erwartetes Format MM/YY.");
			return;
		}

		// Ablaufdatum aufteilen
		String[] expiryParts = expiryDate.split("/");
		int expiryMonth = Integer.parseInt(expiryParts[0]);
		int expiryYear = Integer.parseInt("20" + expiryParts[1]); // Umwandeln von YY in YYYY
		
		// Aktuelles Jahr und Monat abrufen
	    LocalDate currentDate = LocalDate.now();
	    int currentMonth = currentDate.getMonthValue();
	    int currentYear = currentDate.getYear();
	    
	    // Prüfen, ob das Ablaufdatum in der Vergangenheit liegt
	    if (expiryYear < currentYear || (expiryYear == currentYear && expiryMonth < currentMonth)) {
	        showErrorMessage("Das Ablaufdatum der Kreditkarte darf nicht in der Vergangenheit liegen.");
	        return;
	    }

		// Sicherstellen, dass mindestens eines von Flug, Hotel oder Transfer nicht null ist
		if (flight == null && hotel == null && transfer == null) {
			showErrorMessage("Es wurden keine Buchungsdetails gefunden. Bitte prüfen Sie die Auswahl.");
			return;
		}

		// Preis und Buchungstyp bestimmen
		double price = 0.0;
		String bookingType = "";

		if (flight != null) {
			price = flight.getPrice();
			bookingType = "Flugbuchung";
		} else if (hotel != null) {
			price = hotel.getPrice();
			bookingType = "Hotelbuchung";
		} else if (transfer != null) {
			price = transfer.getPrice();
			bookingType = "Transferbuchung";
		}

		// Zahlung mit BraintreeService verarbeiten
		BraintreeService braintreeService = new BraintreeService();
		boolean paymentSuccess = braintreeService.processPayment(cardNumber, String.valueOf(expiryMonth), String.valueOf(expiryYear), cvc, price);

		if (paymentSuccess) {
			showSuccessMessage("Zahlung erfolgreich!");

			// Buchungsdetails in der Datenbank speichern
			String paymentStatus = "Abgeschlossen";
			String paymentMethod = "Kreditkarte";

			// Den angemeldeten Benutzer vom SessionManager abrufen
			Benutzer loggedInUser = SessionManager.getInstance().getLoggedInUser();

			if (loggedInUser != null) {
				// Buchung basierend auf dem Typ speichern
				if (flight != null) {
					buchungService.saveOrder(bookingType, paymentStatus, flight.getPrice(), flight, null, null, paymentMethod, loggedInUser);
				} else if (hotel != null) {
					buchungService.saveOrder(bookingType, paymentStatus, hotel.getPrice(), null, hotel, null, paymentMethod, loggedInUser);
				} else if (transfer != null) {
					buchungService.saveOrder(bookingType, paymentStatus, transfer.getPrice(), null, null, transfer, paymentMethod, loggedInUser);
				}

				// Ruft refreshBookings um Liste in BuchungenController zu aktuallisieren
		        if (buchungenController != null) {
		            buchungenController.refreshBookings();
		        }
				
				// Schließt das Zahlungsfenster
				Stage stage = (Stage) btn_pay.getScene().getWindow();
				stage.close();
			} else {
				showErrorMessage("Benutzer nicht angemeldet. Bitte loggen Sie sich ein.");
			}
		} else {
			showErrorMessage("Zahlung fehlgeschlagen. Bitte versuchen Sie es erneut.");
		}
	}

	// Methode zum Anzeigen einer Fehlermeldung
	private void showErrorMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Zahlungsfehler");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	// Methode zum Anzeigen einer Erfolgsmeldung
	private void showSuccessMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Zahlung erfolgreich");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
