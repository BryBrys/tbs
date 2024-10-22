package helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.login.apilogin.FlugController;

public class FlightService {
	private static final String API_KEY = "I2w5UqOxi9t8qmAACcboa4pah3HA9CoP";  // Dein Amadeus API-Schlüssel
	private static final String API_SECRET = "AjDGVWbFRMJS8ZwR";  // Dein Amadeus API-Geheimnis
	private static final String AUTH_URL = "https://test.api.amadeus.com/v1/security/oauth2/token";  // Authentifizierungs-URL
	private static final String FLIGHT_SEARCH_URL = "https://test.api.amadeus.com/v2/shopping/flight-offers";  // URL zur Flugpreissuche

	private String accessToken;  // Access Token für die API-Anfragen

	private Map<String, String> airlineCodeMap = new HashMap<>(); // Airline code-name mapping

	// Methode zum Laden der Airline-Codes und Namen aus der CSV-Datei
	private void loadAirlineCodes() throws IOException {
		String csvFilePath = "src/main/resources/AirlineCodes.csv"; // Pfad zur AirlineCodes CSV-Datei
		BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
		String line;

		br.readLine();

		while ((line = br.readLine()) != null) {
			String[] data = line.split(","); // Annahme: CSV-Datei ist durch Kommas getrennt
			if (data.length > 1) { // Kontrolle, dass genug Spalten vorhanden sind
				String code = data[0].trim(); // Airline-Code
				String name = data[1].trim(); // Airline-Name
				airlineCodeMap.put(code, name); // Speichere die Zuordnung in der Map
			}
		}
		br.close();
	}

	// Methode zur Authentifizierung und Erhalt des Access Tokens
	private void authenticate() throws Exception {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(AUTH_URL);  // HTTP-POST Anfrage an die Authentifizierungs-URL
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			String body = "grant_type=client_credentials&client_id=" + API_KEY + "&client_secret=" + API_SECRET;
			post.setEntity(new StringEntity(body));  // Setze den Anfrageinhalt für die Authentifizierung

			try (CloseableHttpResponse response = client.execute(post)) {
				String responseBody = EntityUtils.toString(response.getEntity());  // Antwort als String lesen
				JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);  // JSON-Antwort parsen
				accessToken = jsonObject.get("access_token").getAsString();  // Access Token speichern
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Methode zur Abfrage der Fluginformationen
	public List<FlightData> getFlights(String origin, String destination, String departureDate, String returnDate, String adults, String children) throws Exception {
		if (accessToken == null) {  // Wenn noch nicht authentifiziert, führe die Authentifizierung durch
			authenticate();
		}

		// Lade die Airline-Codes, falls sie noch nicht geladen wurden
		if (airlineCodeMap.isEmpty()) {
			loadAirlineCodes();
		}

		List<FlightData> flights = new ArrayList<>();
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			// Erstelle die URL für die Flugabfrage
			String url = FLIGHT_SEARCH_URL
					+ "?originLocationCode=" + origin  // Abflugort
					+ "&destinationLocationCode=" + destination  // Zielort
					+ "&departureDate=" + departureDate // Abflugdatum
					+ "&returnDate=" + returnDate // Rückflugdatum
					+ "&nonStop=true" // nur direkt Flüge
					+ "&adults=" + adults // Anzahl der Erwachsenen
					+ "&children=" + children // Anzahl der Kinder
					+ "&max=20"  // Maximale Anzahl der Ergebnisse
					+ "&currencyCode=EUR";  // Währung in Euro

			// Ausgabe API Anfrage
			System.out.println("API Call: " + url);

			HttpGet get = new HttpGet(url);  // HTTP-GET Anfrage für die Flugsuche
			get.setHeader("Authorization", "Bearer " + accessToken);  // Access Token zur Anfrage hinzufügen

			try (CloseableHttpResponse response = client.execute(get)) {
				String responseBody = EntityUtils.toString(response.getEntity());  // Antwort als String lesen

				// Ausgabe API RESPONSE
				System.out.println("API Response: " + responseBody);

				JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);  // JSON-Antwort parsen

				if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
					JsonArray flightOffers = jsonObject.getAsJsonArray("data");  // Flugdaten abrufen

					for (JsonElement flightOfferElement : flightOffers) {
						JsonObject flightData = flightOfferElement.getAsJsonObject();
						JsonObject firstSegment = flightData.getAsJsonArray("itineraries")
								.get(0).getAsJsonObject().getAsJsonArray("segments").get(0).getAsJsonObject();
						JsonObject secondSegment = flightData.getAsJsonArray("itineraries")
								.get(1).getAsJsonObject().getAsJsonArray("segments").get(0).getAsJsonObject();

						// Extrahiere den OutboundAirline-Code und hole den Namen
						String outboundAirlineCode = firstSegment.get("carrierCode").getAsString();
						String outboundAirlineName = airlineCodeMap.getOrDefault(outboundAirlineCode, outboundAirlineCode);

						// Extrahiere die ReturnAirline-Code und hole den Namen
						String returnAirlineCode = secondSegment.get("carrierCode").getAsString();
						String returnAirlineName = airlineCodeMap.getOrDefault(returnAirlineCode, returnAirlineCode);


						// Fluglinien und Flugnummern extrahieren
						String outboundAirline = firstSegment.get("carrierCode").getAsString();
						String outboundFlightNumber = outboundAirline + firstSegment.get("number").getAsString();
						String returnAirline = secondSegment.get("carrierCode").getAsString();
						String returnFlightNumber = returnAirline + secondSegment.get("number").getAsString();

						// Flugdauer formatieren
						String outboundFlightTime = formatFlightDuration(flightData.getAsJsonArray("itineraries")
								.get(0).getAsJsonObject().get("duration").getAsString());
						String returnFlightTime = formatFlightDuration(flightData.getAsJsonArray("itineraries")
								.get(1).getAsJsonObject().get("duration").getAsString());

						// Datum- und Zeitangaben formatieren
						String outboundDeparture = formatDateTime(firstSegment.get("departure").getAsJsonObject().get("at").getAsString());
						String outboundArrival = formatDateTime(firstSegment.get("arrival").getAsJsonObject().get("at").getAsString());
						String returnDeparture = formatDateTime(secondSegment.get("departure").getAsJsonObject().get("at").getAsString());
						String returnArrival = formatDateTime(secondSegment.get("arrival").getAsJsonObject().get("at").getAsString());

						// IataCode extrahieren
						String outboundDepartureIataCode = firstSegment.get("departure").getAsJsonObject().get("iataCode").getAsString();
						String outboundArrivalIataCode = firstSegment.get("arrival").getAsJsonObject().get("iataCode").getAsString();
						String returnDepartureIataCode = secondSegment.get("departure").getAsJsonObject().get("iataCode").getAsString();
						String returnArrivalIataCode = secondSegment.get("arrival").getAsJsonObject().get("iataCode").getAsString();

						// Datum extrahieren yyyy-MM-dd
						String outboundDepartureDate = formatToDate(firstSegment.get("departure").getAsJsonObject().get("at").getAsString());
						String outboundArrivalDate = formatToDate(firstSegment.get("arrival").getAsJsonObject().get("at").getAsString());
						String returnDepartureDate = formatToDate(secondSegment.get("departure").getAsJsonObject().get("at").getAsString());
						String returnArrivalDate = formatToDate(secondSegment.get("arrival").getAsJsonObject().get("at").getAsString());

						// Überprüfen, ob es sich um Direktflüge handelt
						boolean outboundDirectFlight = flightData.getAsJsonArray("itineraries").get(0).getAsJsonObject().getAsJsonArray("segments").size() == 1;
						boolean returnDirectFlight = flightData.getAsJsonArray("itineraries").get(1).getAsJsonObject().getAsJsonArray("segments").size() == 1;

						// Gesamtpreis extrahieren
						double price = flightData.getAsJsonObject("price").get("total").getAsDouble();

						// FlightData-Objekt erstellen und der Liste hinzufügen
						flights.add(new FlightData(outboundFlightNumber, outboundAirline,
								outboundAirlineName, outboundDeparture, outboundArrival,
								outboundDepartureIataCode, outboundArrivalIataCode, outboundFlightTime,
								returnFlightNumber, returnAirline, returnAirlineName, returnDeparture,
								returnArrival, returnDepartureIataCode, returnArrivalIataCode,
								returnFlightTime, outboundDirectFlight, returnDirectFlight, price,
								outboundDepartureDate, outboundArrivalDate, returnDepartureDate, returnArrivalDate, adults));

					}
				} else {
					// Falls keine Daten gefunden werden
					System.out.println("Keine Flugdaten gefunden.");
				}
			}
		}
		return flights;  // Rückgabe der Liste mit Fluginformationen
	}


	// Methode zur Formatierung der Flugdauer von "PTxxHxxM" zu "xxH xxM"
	private String formatFlightDuration(String duration) {
		// Entferne den "PT"-Präfix und ersetze "H" und "M" durch das gewünschte Format
		return duration.replace("PT", "").replace("H", "H ").replace("M", "M");
	}

	// Methode zur Formatierung der Datums- und Zeitangaben, um nur die Zeit als "HH:mm" anzuzeigen
	private String formatDateTime(String dateTime) {
		LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
		return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
	}

	// Methode zur Formatierung der Datums- und Zeitangaben, um nur die Zeit als "HH:mm" anzuzeigen
	private String formatToDate(String dateTime) {
		LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
		return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
}
