package com.login.apilogin;

import java.io.IOException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import classEntities.Benutzer;
import helpers.HibernateUtil;
import helpers.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

	// Methode zum Wechseln zur Registrierungsseite
	@FXML
	private void btnRegistrierungSwitch() throws IOException {
		App.setRoot("registration");  // Lädt das "registration"-View
	}
	// Methode zum Wechseln zur Flugseite nach erfolgreicher Anmeldung
	@FXML
	private void btnFlugSwitch() throws IOException {
		Benutzer loggedInUser = SessionManager.getInstance().getLoggedInUser();  // Holt den eingeloggten Benutzer aus der Session
		App.setRoot("flug", loggedInUser);  // Lädt die "flug"-Seite und übergibt den Benutzer
	}
	@FXML
	private Label loginMessageLabel;
	@FXML
	private TextField EmailTextField;
	@FXML
	private PasswordField passwordPasswordField;
	@FXML
	private TextField passwordTextField;
	@FXML
	private CheckBox logPasswordCheckbox;

	// Methode, die bei Initialisierung des Controllers aufgerufen wird
	public void initialize() {
		// Standardmäßig wird das PasswordField (verdeckt) angezeigt, das TextField (Klartext) wird versteckt
		passwordTextField.setManaged(false);
		passwordTextField.setVisible(false);

		// Bindet den Textinhalt des TextFields an das PasswordField und umgekehrt, damit beide synchron bleiben
		passwordTextField.textProperty().bindBidirectional(passwordPasswordField.textProperty());

		// Listener für die Checkbox: Umschaltung zwischen PasswordField (verdeckt) und TextField (Klartext)
		logPasswordCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				// Zeigt das TextField an (Passwort als Klartext), verbirgt das PasswordField
				passwordTextField.setManaged(true);
				passwordTextField.setVisible(true);
				passwordPasswordField.setManaged(false);
				passwordPasswordField.setVisible(false);
			} else {
				// Zeigt das PasswordField an (Passwort verdeckt), verbirgt das TextField
				passwordTextField.setManaged(false);
				passwordTextField.setVisible(false);
				passwordPasswordField.setManaged(true);
				passwordPasswordField.setVisible(true);
			}
		});
	}

	// Methode für die Anmeldung, prüft ob die Eingabefelder nicht leer sind
	public void anmelden(ActionEvent e) {
		// Überprüfen, ob sowohl die E-Mail als auch das Passwort ausgefüllt sind
		if (!EmailTextField.getText().isBlank() && !passwordPasswordField.getText().isBlank()) {
			validierungAnmeldung();  // Ruft die Validierungsmethode auf
		} else {
			// Zeigt eine Fehlermeldung an, wenn die Felder leer sind
			loginMessageLabel.setText("Bitte Email und Passwort eingeben");
		}
	}

	// Methode zur Validierung der Anmeldedaten in der Datenbank
	public void validierungAnmeldung() {
		SessionFactory factory = HibernateUtil.getSessionFactory();  // Holt die SessionFactory für die DB-Verbindung
		Session session = factory.getCurrentSession();  // Startet eine neue Session

		try {
			session.beginTransaction();  // Startet eine Transaktion

			// HQL-Abfrage zur Suche des Benutzers basierend auf der E-Mail
			String hql = "FROM Benutzer WHERE email = :email";
			Benutzer benutzer = session.createQuery(hql, Benutzer.class)
					.setParameter("email", EmailTextField.getText())  // Setzt den Wert der E-Mail als Parameter
					.uniqueResult();  // Holt das eindeutige Ergebnis der Abfrage

			if (benutzer != null) {
				// Überprüfen, ob das eingegebene Passwort mit dem in der Datenbank übereinstimmt
				if (benutzer.getPassword().equals(passwordPasswordField.getText())) {
					loginMessageLabel.setText("Erfolgreich angemeldet");

					// Speichert den angemeldeten Benutzer in der Session
					SessionManager.getInstance().setLoggedInUser(benutzer);

					// Wechselt zur Flugseite
					btnFlugSwitch();
				} else {
					loginMessageLabel.setText("Falsche Email oder Passwort");  // Fehlermeldung bei falschem Passwort
				}
			} else {
				loginMessageLabel.setText("Falsche Email oder Passwort");  // Fehlermeldung, wenn der Benutzer nicht existiert
			}

			session.getTransaction().commit();  // Bestätigt die Transaktion
		} catch (Exception ex) {
			ex.printStackTrace();
			// Fehlermeldung bei einem Fehler während der Anmeldung
			loginMessageLabel.setText("Ein Fehler ist bei der Anmeldung aufgetreten");
		} finally {
			session.close();  // Schließt die Session
		}
	}
	
	// Methode zur Passwortwiederherstellung
	@FXML
	private void handlePasswordReset() {
	    try {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/login/apilogin/forgotPassword.fxml"));
	        Parent root = loader.load();

	        Stage stage = new Stage();
	        stage.setTitle("Passwort vergessen");
	        stage.setScene(new Scene(root));
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}
