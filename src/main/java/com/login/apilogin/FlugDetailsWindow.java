package com.login.apilogin;

import helpers.FlightData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class FlugDetailsWindow {

	// Öffnet ein neues Fenster mit den Flugdetails
	public void openFlightDetailsWindow(FlightData flight) {
		try {
			// Lädt die flugDetails.fxml Datei
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/login/apilogin/flugDetails.fxml"));
			Parent root = loader.load();

			// Holt den Controller und übergibt den ausgewählten Flug
			FlugDetailsController controller = loader.getController();
			controller.setSelectedFlight(flight);

			// Erstellt eine neue Stage für das Flug-Details-Fenster
			Stage stage = new Stage();
			stage.setTitle("Flug Details");
			stage.setScene(new Scene(root));
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
