package com.login.apilogin;

import java.io.IOException;

import helpers.HotelData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HotelDetailsWindow {

    // Öffnet ein neues Fenster mit den Hoteldetails
    public void openHotelDetailsWindow(HotelData hotel) {
        try {
            // Lädt die hotelDetails.fxml Datei
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/login/apilogin/hotelDetails.fxml"));
            Parent root = loader.load();

            // Holt den Controller und übergibt das ausgewählte Hotel
            HotelDetailsController controller = loader.getController();
            controller.setSelectedHotel(hotel);

            // Erstellt eine neue Stage für das Hotel-Details-Fenster
            Stage stage = new Stage();
            stage.setTitle("Hotel Details");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
