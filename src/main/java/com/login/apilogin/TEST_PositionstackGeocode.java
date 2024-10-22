package com.login.apilogin;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TEST_PositionstackGeocode {

	private static final String API_KEY = "f19bc36a9907d39a8dbff435a23492fc";
	private static final String GEOCODE_URL = "http://api.positionstack.com/v1/forward";

	public static void main(String[] args) throws IOException, InterruptedException {
		TEST_PositionstackGeocode test = new TEST_PositionstackGeocode();

		// Beispieladresse zur Geokodierung
		String address = "Draschestraße, 90, Vienna, 1230, Austria";
		test.getCoordinates(address);
	}

	// Methode, um Koordinaten für eine gegebene Adresse zu erhalten
	public void getCoordinates(String address) throws IOException, InterruptedException {
		// Die Adresse kodieren, um sie URL-sicher zu machen
		String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);

		// Konstruiere die vollständige URL für den API-Aufruf
		String fullUrl = GEOCODE_URL + "?access_key=" + API_KEY + "&query=" + encodedAddress;

		// Erstelle den HTTP-Client und die Anfrage
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(fullUrl))
				.header("Content-Type", "application/json")
				.GET()
				.build();

		// Sende die Anfrage und erhalte die Antwort
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		// Ausgabe der Anfrage-URL und der Antwort
		System.out.println("Anfrage-URL: " + fullUrl);
		System.out.println("Antwortinhalt:");
		System.out.println(response.body());

		// Parsen und Extrahieren von Breitengrad und Längengrad aus der JSONP-Antwort
		parseCoordinates(response.body());
	}

	// Methode zum Parsen der JSON-Antwort und Extrahieren der Koordinaten
	private void parseCoordinates(String jsonResponse) {
		JsonElement jsonElement = JsonParser.parseString(jsonResponse);
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		if (jsonObject.has("data")) {
			JsonArray dataArray = jsonObject.getAsJsonArray("data");
			if (dataArray.size() > 0) {
				JsonObject firstResult = dataArray.get(0).getAsJsonObject();
				String latitude = firstResult.get("latitude").getAsString();
				String longitude = firstResult.get("longitude").getAsString();
				System.out.println("Breitengrad: " + latitude);
				System.out.println("Längengrad: " + longitude);
			} else {
				System.out.println("Keine Ergebnisse für die angegebene Adresse gefunden.");
			}
		} else {
			System.out.println("Unerwartetes Antwortformat.");
		}
	}
}
