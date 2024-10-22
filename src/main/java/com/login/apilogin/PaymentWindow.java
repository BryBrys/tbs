package com.login.apilogin;

import java.io.IOException;

import helpers.FlightData;
import helpers.HotelData;
import helpers.TransferData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PaymentWindow {

    // Methode zum Öffnen des Zahlungsfensters für Flugdaten
    public void openPaymentWindow(FlightData flight) {
        try {
            // FXML-Datei laden, die das Layout für das Zahlungsfenster enthält
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/login/apilogin/paymentWindow.fxml"));
            Parent root = loader.load();  // FXML-Inhalt laden

            // Controller des Zahlungsfensters abrufen und Flugdaten setzen
            PaymentController controller = loader.getController();
            controller.setFlightDetails(flight);  // Flugdaten dem Controller übergeben

            // Neues Fenster erstellen und anzeigen
            Stage stage = new Stage();
            stage.setTitle("Zahlungsdetails");  // Titel des Fensters setzen
            stage.setScene(new Scene(root));  // Die Szene dem Fenster hinzufügen
            stage.show();  // Fenster anzeigen
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Methode zum Öffnen des Zahlungsfensters für Hoteldaten
    public void openPaymentWindow(HotelData hotel) {
        try {
            // FXML-Datei laden, die das Layout für das Zahlungsfenster enthält
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/login/apilogin/paymentWindow.fxml"));
            Parent root = loader.load();  // FXML-Inhalt laden

            // Controller des Zahlungsfensters abrufen und Hoteldaten setzen
            PaymentController controller = loader.getController();
            controller.setHotelDetails(hotel);  // Hoteldaten dem Controller übergeben

            // Neues Fenster erstellen und anzeigen
            Stage stage = new Stage();
            stage.setTitle("Zahlungsdetails");  // Titel des Fensters setzen
            stage.setScene(new Scene(root));  // Die Szene dem Fenster hinzufügen
            stage.show();  // Fenster anzeigen
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Methode zum Öffnen des Zahlungsfensters für Transferdaten (Mietwagen)
    public void openPaymentWindow(TransferData mietwagen) {
        try {
            // FXML-Datei laden, die das Layout für das Zahlungsfenster enthält
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/login/apilogin/paymentWindow.fxml"));
            Parent root = loader.load();  // FXML-Inhalt laden

            // Controller des Zahlungsfensters abrufen und Transferdaten setzen
            PaymentController controller = loader.getController();
            controller.setTransferDetails(mietwagen);  // Transferdaten dem Controller übergeben

            // Neues Fenster erstellen und anzeigen
            Stage stage = new Stage();
            stage.setTitle("Zahlungsdetails");  // Titel des Fensters setzen
            stage.setScene(new Scene(root));  // Die Szene dem Fenster hinzufügen
            stage.show();  // Fenster anzeigen
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
