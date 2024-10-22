package com.login.apilogin;

import helpers.TransferData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TransferDetailsController {

	@FXML
	private Label carTypeLabel;
	@FXML
	private Label providerLabel;
	@FXML
	private Label lbl_price;
	@FXML
	private Label passengersLabel;
	@FXML
	private Label baggageLabel;
	@FXML
	private Label pickUpLocationLabel;
	@FXML
	private Label dropOffLocationLabel;
	@FXML
	private Button btn_cancel, btn_book;

	private TransferData selectedTransfer;

	// Methode, um den ausgewählten Transfer zu setzen
	public void setSelectedMietwagen(TransferData transfer) {
		this.selectedTransfer = transfer;
		updateDetails();
	}
	
	// Methode, um die Details des ausgewählten Transfers anzuzeigen
	private void updateDetails() {
		carTypeLabel.setText("Autotyp: " + selectedTransfer.getCarType());
		providerLabel.setText("AutoProvider: " +selectedTransfer.getProvider());
		lbl_price.setText("Preis: " + String.format("%.2f %s", selectedTransfer.getPrice(), selectedTransfer.getCurrency()));
		passengersLabel.setText("Passagiere: " +String.valueOf(selectedTransfer.getPassengers()));
		baggageLabel.setText("Gepäck: " + String.valueOf(selectedTransfer.getBaggage()));
		pickUpLocationLabel.setText("Startort: " + selectedTransfer.getPickUpLocation());
		dropOffLocationLabel.setText("Zielort: " + selectedTransfer.getEndAddressLine() + ", " 
				+ selectedTransfer.getEndZipCode() + ", " + selectedTransfer.getEndCityName());
	}

	// Methode, um das Fenster zu schließen, wenn der Abbrechen-Button gedrückt wird
	@FXML
	private void handleCancel() {
		Stage stage = (Stage) btn_cancel.getScene().getWindow();
		stage.close();
	}

	// Methode, um das Zahlungsfenster zu öffnen, wenn der Buchen-Button gedrückt wird
	@FXML
	private void handleBook() {
		if (selectedTransfer != null) {
			PaymentWindow paymentWindow = new PaymentWindow();
			paymentWindow.openPaymentWindow(selectedTransfer);
		}
	}
}

