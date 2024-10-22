package com.login.apilogin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TEST_TransferApi {

	private static final String API_KEY = "WQPzGohI8AMQm0qlG56eoA2mMAQz9zbq"; // Amadeus API-Schlüssel
	private static final String API_SECRET = "El6t4mRb2cX8u6IA"; // Amadeus API-Geheimnis
	private static final String AUTH_URL = "https://test.api.amadeus.com/v1/security/oauth2/token"; // URL für die Authentifizierung
	private static final String TRANSFER_OFFERS_URL = "https://test.api.amadeus.com/v1/shopping/transfer-offers"; // URL für Transferangebote
	private String accessToken; // Variable zum Speichern des Zugriffstokens

	public static void main(String[] args) throws IOException, InterruptedException {
		TEST_TransferApi test = new TEST_TransferApi();
		// Authentifizieren und Transferangebote abrufen
		test.authenticate();
		// Beispiel-Anfrage für Transferangebote
		String transferOffersResponse = test.getTransferOffers(
				"CDG",  // Startort (Charles de Gaulle Flughafen)
				"Avenue Anatole France, 5",  // Zieladresse
				"75007",  // Postleitzahl des Ziels
				"Paris",  // Stadt des Ziels
				"FR",  // Ländercode des Ziels
				"ChIJL-DOWeBv5kcRfTbh97PimNc", // Google Place ID für das Ziel
				"2024-11-10T10:30:00",  // Abholdatum und -uhrzeit
				2  // Anzahl der Passagiere
				);
		// Antwort der Transferangebote ausgeben
		System.out.println("Transfer Offers Response:");
		System.out.println(transferOffersResponse);
	}

	// Methode zur Authentifizierung bei der Amadeus API
	private void authenticate() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient(); // HTTP-Client erstellen
		String body = "grant_type=client_credentials&client_id=" + API_KEY + "&client_secret=" + API_SECRET;

		// HTTP-Anfrage zur Authentifizierung erstellen
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(AUTH_URL))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
				.build();

		// Anfrage senden und Antwort empfangen
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		// Überprüfen, ob die Authentifizierung erfolgreich war
		if (response.statusCode() == 200) {
			Gson gson = new Gson();
			// Antwort in ein JSON-Objekt umwandeln
			JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
			accessToken = jsonResponse.get("access_token").getAsString(); // Zugriffstoken extrahieren
			System.out.println("Access Token: " + accessToken); // Zugriffstoken ausgeben
		} else {
			// Falls die Authentifizierung fehlschlägt, die Fehlermeldung ausgeben
			System.out.println("Failed to authenticate: " + response.body());
		}
	}

	// Methode zum Abrufen der Transferangebote
	private String getTransferOffers(String pickUpLocation, String endAddressLine, String endZipCode, String endCityName, 
			String endCountryCode, String endGooglePlaceId, 
			String pickUpDateTime, int passengers) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient(); // HTTP-Client erstellen

		// Transfer-Anfrage-Parameter in einer Map speichern
		Map<String, Object> transferRequest = new HashMap<>();
		transferRequest.put("startLocationCode", pickUpLocation);  // Startort (z.B. CDG Flughafen)
		transferRequest.put("endAddressLine", endAddressLine);     // Zieladresse (z.B. Avenue Anatole France, 5)
		transferRequest.put("endZipCode", endZipCode);             // Postleitzahl des Ziels
		transferRequest.put("endCityName", endCityName);           // Stadt des Ziels (z.B. Paris)
		transferRequest.put("endCountryCode", endCountryCode);     // Ländercode des Ziels (z.B. FR)
		transferRequest.put("endGooglePlaceId", endGooglePlaceId); // Google Place ID für das Ziel
		transferRequest.put("startDateTime", pickUpDateTime);      // Abholdatum und -uhrzeit
		transferRequest.put("passengers", passengers);             // Anzahl der Passagiere
		transferRequest.put("transferType", "PRIVATE");            // Transfer-Typ (privater Transfer)

		Gson gson = new Gson();
		String requestBody = gson.toJson(transferRequest); // Die Transfer-Anfrage in JSON umwandeln

		// Ausgabe des Anfragekörpers für Debugging-Zwecke
		System.out.println("Request Body:");
		System.out.println(requestBody);

		// HTTP-Anfrage für Transferangebote erstellen
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(TRANSFER_OFFERS_URL))
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + accessToken)  // Zugriffstoken in den Header einfügen
				.POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8)) // Anfragekörper senden
				.build();

		// Anfrage senden und Antwort empfangen
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		// Überprüfen, ob die Anfrage erfolgreich war
		if (response.statusCode() == 200) {
			return response.body(); // Die Antwort zurückgeben, falls erfolgreich
		} else {
			// Fehlermeldung zurückgeben, falls die Anfrage fehlschlägt
			return "Failed to retrieve transfer offers: " + response.body();
		}
	}
}
