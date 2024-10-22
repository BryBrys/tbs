package com.login.apilogin;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import classEntities.Benutzer;
import classEntities.Buchung;
import helpers.HibernateUtil;
import helpers.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class BuchungenController {

	private Stage mainStage;

	public void setMainStage(Stage mainStage) {
		this.mainStage = mainStage;
	}

	private Benutzer loggedInUser;

	@FXML
	private ListView<Buchung> listViewBuchungen;

	// Methode zur Initialisierung des eingeloggten Benutzers/aktuelle Buchungen
	public void initialize() {
		loggedInUser = SessionManager.getInstance().getLoggedInUser();

		if (loggedInUser != null) {
			try (Session session = HibernateUtil.getSessionFactory().openSession()) {
				Benutzer user = session.get(Benutzer.class, loggedInUser.getBenutzerId());
				Hibernate.initialize(user.getBuchungen());

				// Sortiere die Buchungen nach Datum
				List<Buchung> sortedBuchungen = user.getBuchungen().stream()
						.sorted((b1, b2) -> b1.getBuchungsdatum().compareTo(b2.getBuchungsdatum()))
						.collect(Collectors.toList());

				listViewBuchungen.getItems().addAll(sortedBuchungen);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Methode zum Löschen einer Buchung
	@FXML
	private void handleDeleteBooking() {
		Buchung selectedBuchung = listViewBuchungen.getSelectionModel().getSelectedItem();

		if (selectedBuchung == null) {
			// Inline-Warnmeldung erstellen
			Alert warningAlert = new Alert(AlertType.WARNING);
			warningAlert.setTitle("Keine Auswahl");
			warningAlert.setHeaderText(null);
			warningAlert.setContentText("Bitte wählen Sie eine Buchung zum Löschen aus.");
			warningAlert.showAndWait();
			return;
		}

		Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
		confirmAlert.setTitle("Buchung löschen");
		confirmAlert.setHeaderText(null);
		confirmAlert.setContentText("Sind Sie sicher, dass Sie diese Buchung löschen möchten?");
		confirmAlert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				// Die Buchung löschen und die Ansicht aktualisieren
				deleteBooking(selectedBuchung);
				refreshBookings();
			}
		});
	}

	// Methode zur Aktualisierung der ListView in Booking
	public void refreshBookings() {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			Benutzer user = session.get(Benutzer.class, loggedInUser.getBenutzerId());
			Hibernate.initialize(user.getBuchungen());

			// Sortiere die Buchungen nach Datum (assuming getBuchungsDatum() returns a date)
			List<Buchung> sortedBuchungen = user.getBuchungen().stream()
					.sorted((b1, b2) -> b1.getBuchungsdatum().compareTo(b2.getBuchungsdatum()))
					.collect(Collectors.toList());

			// Aktualisierung der ListView
			listViewBuchungen.getItems().clear();
			listViewBuchungen.getItems().addAll(sortedBuchungen);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Methode zur Löschung einer Buchung
	private void deleteBooking(Buchung selectedBuchung) {
		try {
			SessionManager.getInstance().deleteBooking(selectedBuchung);

			// Die Buchung aus der Buchungsliste des Benutzers entfernen
			loggedInUser.getBuchungen().remove(selectedBuchung);

		} catch (Exception e) {
			Alert errorAlert = new Alert(AlertType.ERROR);
			errorAlert.setTitle("Fehler");
			errorAlert.setHeaderText(null);
			errorAlert.setContentText("Fehler beim Löschen der Buchung: " + e.getMessage());
			errorAlert.showAndWait();
			e.printStackTrace();
		}
	}

	// Methode für das Schließen "Meine Buchung"-Fensters
	@FXML
	private void handleZurueck() {
		Stage stage = (Stage) listViewBuchungen.getScene().getWindow();
		stage.close();
	}
}
