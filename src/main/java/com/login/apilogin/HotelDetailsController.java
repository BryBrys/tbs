package com.login.apilogin;

import helpers.HotelData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class HotelDetailsController {

	@FXML
	private Label lbl_hotelName;
	@FXML
	private Label lbl_amenities;
	@FXML
	private Label lbl_description;
	@FXML
	private Label lbl_price;
	@FXML
	private Label lbl_rooms;
	@FXML
	private Label lbl_bedType;
	@FXML
	private Label lbl_beds;
	@FXML
	private Button btn_cancel;
	@FXML
	private TextFlow desc_flow;

	private HotelData selectedHotel;

	// Setzt das ausgewählte Hotel und aktualisiert die Anzeige mit den Hoteldetails
	public void setSelectedHotel(HotelData hotel) {
		this.selectedHotel = hotel;

		lbl_hotelName.setText("Hotel: " + hotel.getHotelName());
		lbl_amenities.setText("Zubehör: " + String.join(", ", hotel.getAmenities()));
		lbl_description.setText("Beschreibung:");
		Text descriptionText = new Text(hotel.getOffer().getDescription());
		descriptionText.setStyle("-fx-fill: #FFFFFF; -fx-font-size: 16px;");
		desc_flow.getChildren().add(descriptionText);
		lbl_price.setText("Preis: " + String.format("%.2f €", hotel.getOffer().getPrice()));
		lbl_rooms.setText("Zimmer: " + String.valueOf(hotel.getOffer().getRooms()));
		lbl_bedType.setText("Bett: " + hotel.getOffer().getBedType());
		lbl_beds.setText("Bettanzahl: " + String.valueOf(hotel.getOffer().getBeds()));
	}

	// Schließt das Fenster, wenn der "Abbrechen"-Button gedrückt wird
	@FXML
	private void handleCancel() {
		Stage stage = (Stage) btn_cancel.getScene().getWindow();
		stage.close();
	}

	// Öffnet das Zahlungsfenster, wenn der "Buchen"-Button gedrückt wird
	@FXML
	private void handleBook() {
		if (selectedHotel != null) {
			PaymentWindow paymentWindow = new PaymentWindow();
			paymentWindow.openPaymentWindow(selectedHotel);
		}
	}
}
