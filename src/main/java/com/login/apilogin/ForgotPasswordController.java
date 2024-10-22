package com.login.apilogin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ForgotPasswordController {

    @FXML
    private TextField emailTextField;

    @FXML
    private void handleSendPassword() {
        String email = emailTextField.getText().trim();

        if (email.isEmpty()) {
            // Zeigt Fehlermeldung an, wenn keine Email vorhanden ist
            showErrorMessage("Bitte geben Sie eine E-Mail-Adresse ein.");
            return;
        }

        // Simuliert eine ein Passwort-Anfrage
        sendNewPassword(email);

        Stage stage = (Stage) emailTextField.getScene().getWindow();
        stage.close();
    }

    // Simuliert eine Methode um dem Benutzer eine neues Passwort zu schicken
    private void sendNewPassword(String email) {
        showSuccessMessage("Ein neues Passwort wurde an " + email + " gesendet.");
    }

    // Zeigt Fehlermeldung an
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Zeigt Erfolgsmeldung an
    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Erfolg");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
