package com.login.apilogin;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import classEntities.Benutzer;
import helpers.HibernateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrationController {

	// Regex-Muster, um das E-Mail-Format zu validieren
	private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

	@FXML
	private void btnLoginSwitch() throws IOException {
		App.setRoot("login");
	}

	@FXML
	private Label registerMessageLabel;

	@FXML
	private TextField regEmailTextField;

	@FXML
	private PasswordField regPasswordPasswordField;

	@FXML
	private TextField regPasswordTextField;

	@FXML
	private TextField regLastNameTextField;

	@FXML
	private TextField regFirstNameTextField;

	@FXML
	private CheckBox regPasswordCheckbox;  // Toggle für Passwort anzeigen

	// Methode, die bei Initialisierung des Controllers aufgerufen wird
	public void initialize() {
		// Initialerweise das PasswordField anzeigen und das TextField ausblenden
		regPasswordTextField.setManaged(false);
		regPasswordTextField.setVisible(false);

		// Binde den Wert des TextFields an das PasswordField und umgekehrt
		regPasswordTextField.textProperty().bindBidirectional(regPasswordPasswordField.textProperty());

		// Füge einen Listener hinzu, um die Sichtbarkeit des Passworts umzuschalten
		regPasswordCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				// Zeige das TextField (Klartext) und blende das PasswortFeld aus
				regPasswordTextField.setManaged(true);
				regPasswordTextField.setVisible(true);
				regPasswordPasswordField.setManaged(false);
				regPasswordPasswordField.setVisible(false);
			} else {
				// Zeige das PasswortFeld und blende das TextField aus
				regPasswordTextField.setManaged(false);
				regPasswordTextField.setVisible(false);
				regPasswordPasswordField.setManaged(true);
				regPasswordPasswordField.setVisible(true);
			}
		});
	}

	// Methode zur Registrierung eines neuen Kunden/Benutzers
	public void registrierung() {
		// Überprüfe, ob keines der erforderlichen Felder leer ist
		if (regEmailTextField.getText().isBlank() || 
				regPasswordPasswordField.getText().isBlank() || 
				regFirstNameTextField.getText().isBlank() || 
				regLastNameTextField.getText().isBlank()) {
			registerMessageLabel.setText("Bitte alle Felder ausfüllen");
			return;
		}

		//  Überprüfe das E-Mail-Format
		String email = regEmailTextField.getText();
		if (!isValidEmail(email)) {
			registerMessageLabel.setText("Bitte eine korrekte E-Mail eingeben");
			return;
		}

		SessionFactory factory = HibernateUtil.getSessionFactory();
		Session session = factory.getCurrentSession();

		try {
			session.beginTransaction();

			// Überprüfe, ob die E-Mail bereits in der Datenbank existiert
			String hql = "FROM Benutzer WHERE email = :email";
			Benutzer existingBenutzer = session.createQuery(hql, Benutzer.class)
					.setParameter("email", email)
					.uniqueResult();

			if (existingBenutzer != null) {
				// E-Mail existiert bereits, Registrierung abbrechen
				registerMessageLabel.setText("E-Mail bereits vorhanden");
			} else {
				// Wenn die E-Mail nicht existiert, registriere den neuen Benutzer
				Benutzer tempBenutzer = new Benutzer(
						regFirstNameTextField.getText(),
						regLastNameTextField.getText(),
						regEmailTextField.getText(),
						regPasswordPasswordField.getText()
						);
				session.persist(tempBenutzer);

				// Transaktion bestätigen
				session.getTransaction().commit();

				registerMessageLabel.setText("Registrierung erfolgreich");
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			registerMessageLabel.setText("Ein Fehler ist bei der Registrierung aufgetreten");
		} finally {
			session.close();
		}
	}

	// Methode zur E-Mail-Validierung mit Regex
	private boolean isValidEmail(String email) {
		Pattern pattern = Pattern.compile(EMAIL_REGEX);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
