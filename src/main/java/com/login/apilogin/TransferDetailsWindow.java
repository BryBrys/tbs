package com.login.apilogin;

import java.io.IOException;

import helpers.TransferData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TransferDetailsWindow {

    // Methode zum Öffnen des Transfer-Details-Fensters
    public void openTransferDetailsWindow(TransferData transfer) {
        try {
            // FXML-Datei laden, die das Layout für das Fenster enthält
            FXMLLoader loader = new FXMLLoader(getClass().getResource("transferDetails.fxml"));
            Parent root = loader.load(); // Das Parent-Objekt, das die FXML-Struktur repräsentiert, laden

            // Den Controller für das Transfer-Details-Fenster abrufen
            TransferDetailsController controller = loader.getController();
            controller.setSelectedMietwagen(transfer); // Setzt die Transferdaten in den Controller

            // Ein neues Fenster (Stage) erstellen
            Stage stage = new Stage();
            stage.setTitle("Transfer Details"); // Titel des Fensters setzen
            stage.setScene(new Scene(root)); // Die Szene dem Fenster hinzufügen
            stage.show(); // Das Fenster anzeigen
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }
}
