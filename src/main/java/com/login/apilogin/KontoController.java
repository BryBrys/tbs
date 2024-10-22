package com.login.apilogin;

import java.io.IOException;

import classEntities.Benutzer;
import helpers.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class KontoController {

	@FXML
	private TextField txt_firstName;
	@FXML
	private TextField txt_lastName;
	@FXML
	private TextField txt_address;
	@FXML
	private TextField txt_plz;
	@FXML
	private TextField txt_city;
	@FXML
	private TextField txt_country;
	@FXML
	private Button btn_abbrechen;
	@FXML
	private Button btn_speichern;
	@FXML
	private Button btn_benutzerLoeschen;
	@FXML
	private Label errorMessageLabel;

	private Stage mainStage;

	// Methode zum Setzen der Hauptbühne, wird verwendet, um das Hauptfenster (flug.fxml) zu steuern
	public void setMainStage(Stage stage) {
		this.mainStage = stage;
	}

	private Benutzer loggedInUser;

	// Methode, die bei Initialisierung des Controllers aufgerufen wird
	public void initialize() {
		// Lädt die Daten des aktuell eingeloggten Benutzers
		loggedInUser = SessionManager.getInstance().getLoggedInUser();

		if (loggedInUser != null) {
			// Befüllt die Felder mit den Benutzerdaten
			txt_firstName.setText(loggedInUser.getFirstName());
			txt_lastName.setText(loggedInUser.getLastName());
			txt_address.setText(loggedInUser.getAddress());
			txt_plz.setText(loggedInUser.getPlz());
			txt_city.setText(loggedInUser.getCity());
			txt_country.setText(loggedInUser.getCountry());
		}
	}

	// Methode zum Schließen des Fensters, wenn der "Abbrechen"-Button gedrückt wird
	@FXML
	private void handleCancel() {
		Stage stage = (Stage) btn_abbrechen.getScene().getWindow();
		stage.close();  // Schließt das Fenster
	}

	// Methode zum Speichern der Benutzerdaten, wenn der "Speichern"-Button gedrückt wird
	@FXML
	private void handleSave() {
		// Validiert und speichert die Benutzerdaten
		String firstName = txt_firstName.getText();
		String lastName = txt_lastName.getText();
		String address = txt_address.getText() != null ? txt_address.getText().trim() : "";
		String plz = txt_plz.getText() != null ? txt_plz.getText().trim() : "";
		String city = txt_city.getText() != null ? txt_city.getText().trim() : "";
		String country = txt_country.getText() != null ? txt_country.getText().trim() : "";

		// Validiert nur Vorname und Nachname (Pflichtfelder)
		if (firstName.isEmpty() || lastName.isEmpty()) {
			showErrorMessage("Vorname und Nachname bitte ausfüllen.");
			return;
		}

		// Setzt die Daten des Benutzers in die Textfelder
		loggedInUser.setFirstName(firstName);
		loggedInUser.setLastName(lastName);

		// Felder werden nur aktualisiert, wenn sie nicht leer sind
		if (!address.isEmpty()) {
			loggedInUser.setAddress(address);
		}
		if (!plz.isEmpty()) {
			loggedInUser.setPlz(plz);
		}
		if (!city.isEmpty()) {
			loggedInUser.setCity(city);
		}
		if (!country.isEmpty()) {
			loggedInUser.setCountry(country);
		}

		// Speichert die aktualisierten Daten in der Datenbank
		SessionManager.getInstance().updateUser(loggedInUser);

		// Schließt das Fenster nach dem Speichern
		Stage stage = (Stage) btn_speichern.getScene().getWindow();
		stage.close();
	}

	// Methode zum Löschen des Benutzers, wenn der "Benutzer löschen"-Button gedrückt wird
	@FXML
	private void handleDeleteUser() {
		// Erstellt eine Bestätigungsabfrage
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Benutzer löschen");
		alert.setHeaderText(null);
		alert.setContentText("Sind Sie sicher, dass Sie den Benutzer löschen möchten?");

		ButtonType buttonTypeYes = new ButtonType("Ja");
		ButtonType buttonTypeNo = new ButtonType("Nein", ButtonBar.ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

		// Wartet auf die Antwort des Benutzers
		alert.showAndWait().ifPresent(response -> {
			if (response == buttonTypeYes) {
				// Wenn der Benutzer bestätigt, wird der Benutzer gelöscht
				SessionManager.getInstance().deleteUser();  // Benutzer löschen

				// Benutzer wird ausgeloggt
				SessionManager.getInstance().logout();

				// Schließt das konto.fxml-Fenster
				Stage kontoStage = (Stage) btn_benutzerLoeschen.getScene().getWindow();
				kontoStage.close();

				// Schließt das Hauptfenster (flug.fxml)
				if (mainStage != null) {
					mainStage.close();
				}

				// Öffnet das login.fxml-Fenster im gleichen Fenster
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
					Parent root = loader.load();

					// Nutzt die gleiche Bühne
					mainStage.setScene(new Scene(root));
					mainStage.show();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Methode zur Anzeige von Fehlermeldungen
	private void showErrorMessage(String message) {
		if (errorMessageLabel != null) {
			errorMessageLabel.setText(message);
		}
	}
}
