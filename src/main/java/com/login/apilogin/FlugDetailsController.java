package com.login.apilogin;

import helpers.FlightData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class FlugDetailsController {

	@FXML
	private Label lbl_outDepDate;
	@FXML
	private Label lbl_outArrDate;
	@FXML
	private Label lbl_outAirline;
	@FXML
	private Label lbl_outFlightnumber;
	@FXML
	private Label lbl_outDepTime;
	@FXML
	private Label lbl_outArrTime;
	@FXML
	private Label lbl_outDepLoc;
	@FXML
	private Label lbl_outArrLoc;

	@FXML
	private Label lbl_retDepDate;
	@FXML
	private Label lbl_retArrDate;
	@FXML
	private Label lbl_retAirline;
	@FXML
	private Label lbl_retFlightnumber;
	@FXML
	private Label lbl_retDepTime;
	@FXML
	private Label lbl_retArrTime;
	@FXML
	private Label lbl_retDepLoc;
	@FXML
	private Label lbl_retArrLoc;

	@FXML
	private Label lbl_price;

	@FXML
	private Button btn_cancel;

	private FlightData selectedFlight;
	
	// Anzeigen der Daten ausgewählten Fluges
	public void setSelectedFlight(FlightData flight) {
		this.selectedFlight = flight;

		// Daten des Hinfluges
		lbl_outDepDate.setText("Abflugdatum: " + flight.getOutboundDepartureDate());
		lbl_outArrDate.setText("Ankunftsdatum: " + flight.getOutboundArrivalDate());
		lbl_outAirline.setText("Airline: " + flight.getOutboundAirlineName());
		lbl_outFlightnumber.setText("Flugnummer: " + flight.getOutboundFlightNumber());
		lbl_outDepTime.setText("Abflugszeit: " + flight.getOutboundDepartureTime());
		lbl_outArrTime.setText("Ankunftszeit: " + flight.getOutboundArrivalTime());
		lbl_outDepLoc.setText("Abflugsort: " + flight.getOutboundDepartureIataCode());
		lbl_outArrLoc.setText("Ankunftsort: " + flight.getOutboundArrivalIataCode());

		// Daten des Rückfluges
		lbl_retDepDate.setText("Abflugdatum: " + flight.getReturnDepartureDate());
		lbl_retArrDate.setText("Ankunftsdatum: " + flight.getReturnArrivalDate());
		lbl_retAirline.setText("Airline: " + flight.getRetirnAirlineName());
		lbl_retFlightnumber.setText("Flugnummer: " + flight.getReturnFlightNumber());
		lbl_retDepTime.setText("Abflugszeit: " + flight.getReturnDepartureTime());
		lbl_retArrTime.setText("Ankunftszeit: " + flight.getReturnArrivalTime());
		lbl_retDepLoc.setText("Abflugsort: " + flight.getReturnDepartureIataCode());
		lbl_retArrLoc.setText("Ankunftsort: " + flight.getReturnArrivalIataCode());

		lbl_price.setText("Preis für " + flight.getAdults() + (flight.getAdults() == "1" ? " Erwachsener: " : " Erwachsene: ")
				+ String.format("%.2f €", flight.getPrice()));        
	}

	// Methode zum Schlißene des Detailfensters
	@FXML
	private void handleCancel() {
		Stage stage = (Stage) btn_cancel.getScene().getWindow();
		stage.close();
	}

	// Methode zum öffnen des Zahlungsfensters
	@FXML
	private void handleBook() {
		PaymentWindow paymentWindow = new PaymentWindow();
		paymentWindow.openPaymentWindow(selectedFlight);
	}
}
