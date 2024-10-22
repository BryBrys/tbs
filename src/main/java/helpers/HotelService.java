package helpers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

public class HotelService {
	private static final String API_KEY = "I2w5UqOxi9t8qmAACcboa4pah3HA9CoP"; // Amadeus API key
	private static final String API_SECRET = "AjDGVWbFRMJS8ZwR"; // Amadeus API secret
	private static final String AUTH_URL = "https://test.api.amadeus.com/v1/security/oauth2/token"; // URL zur Authentifizierung
	private static final String HOTEL_LIST_SEARCH_URL = "https://test.api.amadeus.com/v1/reference-data/locations/hotels/by-city"; // URL zur Hotelsuche
	private static final String HOTEL_ID_SEARCH_URL = "https://test.api.amadeus.com/v3/shopping/hotel-offers"; // URL zur Hotelsuche nach ID

	private String accessToken; // Token für die Authentifizierung

	// Methode zur Authentifizierung und zum Abrufen des Access Tokens
	private void authenticate() throws Exception {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(AUTH_URL);
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			String body = "grant_type=client_credentials&client_id=" + API_KEY + "&client_secret=" + API_SECRET;
			post.setEntity(new StringEntity(body));

			// Senden der POST-Anfrage zur Authentifizierung
			try (CloseableHttpResponse response = client.execute(post)) {
				String responseBody = EntityUtils.toString(response.getEntity());
				JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
				accessToken = jsonObject.get("access_token").getAsString(); // Speichern des Access Tokens
			}
		}
	}

	// Methode, um Hotels zu erhalten
	public List<HotelData> getHotels(String cityCode, String amenities, String checkInDate, String checkOutDate, String adults, String rooms) throws Exception {
		// Überprüfen, ob das Access Token vorhanden ist, und gegebenenfalls authentifizieren
		if (accessToken == null) {
			authenticate();
		}

		List<HotelData> hotels = new ArrayList<>(); // Liste zur Speicherung der gefundenen Hotels
		String url;

		try (CloseableHttpClient client = HttpClients.createDefault()) {
			// Aufbau der Anfrage-URL
			url = HOTEL_LIST_SEARCH_URL + "?cityCode=" + cityCode + "&radius=5" + "&radiusUnit=KM" + "&hotelSource=ALL";

			// Hinzufügen von Annehmlichkeiten zur URL, falls vorhanden
			if (!amenities.isEmpty()) {
				url += "&amenities=" + amenities;
			}

			System.out.println("API Call: " + url); // Log

			HttpGet get = new HttpGet(url);
			get.setHeader("Authorization", "Bearer " + accessToken); // Setzen des Authorization-Headers

			// Ausführen der GET-Anfrage zur Hotelsuche
			try (CloseableHttpResponse response = client.execute(get)) {
				String responseBody = EntityUtils.toString(response.getEntity());
				System.out.println("API Response: " + responseBody); // Debugging-Ausgabe

				JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);

				// Überprüfen, ob die Antwort Daten enthält
				if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
					JsonArray hotelOffers = jsonObject.getAsJsonArray("data"); // Liste der Hotelangebote

					List<String> hotelIds = new ArrayList<>(); // Liste zur Speicherung der Hotel-IDs

					// Durchlaufen der Hotelangebote und Speichern der Daten
					for (JsonElement hotelOfferElement : hotelOffers) {
						JsonObject hotelData = hotelOfferElement.getAsJsonObject();

						String hotelName = hotelData.get("name").getAsString(); // Hotelname
						String hotelId = hotelData.get("hotelId").getAsString(); // Hotel-ID
						String iataCode = hotelData.get("iataCode").getAsString(); // IATA-Code

						hotelIds.add(hotelId); // Hotel-ID zur Liste hinzufügen

						List<String> amenitiesList = new ArrayList<>(); // Liste zur Speicherung der Annehmlichkeiten
						if (hotelData.has("amenities") && hotelData.get("amenities").isJsonArray()) {
							JsonArray amenitiesArray = hotelData.getAsJsonArray("amenities");
							for (JsonElement amenity : amenitiesArray) {
								amenitiesList.add(amenity.getAsString()); // Annehmlichkeiten zur Liste hinzufügen
							}
						}

						String[] amenitiesArr = amenitiesList.toArray(new String[0]); // Umwandlung in ein Array
						HotelData hotel = new HotelData(iataCode, amenitiesArr, hotelName, hotelId); // Erstellen eines HotelData-Objekts
						hotels.add(hotel); // Hotel zur Liste hinzufügen
						System.out.println("Hotel added: " + hotel.getHotelName()); // Log
					}

					// Abrufen von Hotelangeboten für die gefundenen Hotels
					List<HotelOfferData> hotelOffersData = getHotelOffers(hotelIds, checkInDate, checkOutDate, adults, rooms);
					attachOffersToHotels(hotels, hotelOffersData); // Anfügen der Angebote an die Hotels
				}
			}
		}

		return hotels; // Rückgabe der Liste der Hotels
	}

	private static final int MAX_HOTEL_IDS_PER_REQUEST = 20; // Maximale Anzahl von Hotel-IDs pro Anfrage

	// Hotel-IDs in Batches aufteilen
	private List<List<String>> batchHotelIds(List<String> hotelIds) {
		List<List<String>> batches = new ArrayList<>();

		// Aufteilen der Hotel-IDs in kleinere Batches
		for (int i = 0; i < hotelIds.size(); i += MAX_HOTEL_IDS_PER_REQUEST) {
			int end = Math.min(hotelIds.size(), i + MAX_HOTEL_IDS_PER_REQUEST);
			batches.add(hotelIds.subList(i, end)); // Hinzufügen des Batches zur Liste
		}

		return batches; // Rückgabe der Batches
	}

	// Hotelangebote abrufen
	private List<HotelOfferData> getHotelOffers(List<String> hotelIds, String checkInDate, String checkOutDate, String adults, String rooms) throws Exception {
		// Überprüfen, ob das Access Token vorhanden ist, und gegebenenfalls authentifizieren
		if (accessToken == null) {
			authenticate();
		}

		List<HotelOfferData> hotelOffers = new ArrayList<>(); // Liste zur Speicherung der Hotelangebote
		List<List<String>> batches = batchHotelIds(hotelIds); // Aufteilen der Hotel-IDs in Batches

		// Durchlaufen der Batches und Abrufen der Angebote
		for (List<String> batch : batches) {
			try (CloseableHttpClient client = HttpClients.createDefault()) {
				// Aufbau der Anfrage-URL
				String url = HOTEL_ID_SEARCH_URL + "?hotelIds=" + String.join(",", batch) + "&adults=" + adults
						+ "&checkInDate=" + checkInDate + "&checkOutDate=" + checkOutDate + "&roomQuantity=" + rooms
						+ "&paymentPolicy=NONE" + "&bestRateOnly=true";

				System.out.println("API Call (HotelOffer): " + url); // Log

				HttpGet get = new HttpGet(url);
				get.setHeader("Authorization", "Bearer " + accessToken); // Setzen des Authorization-Headers

				// Ausführen der GET-Anfrage zur Hotelangebotssuche
				try (CloseableHttpResponse response = client.execute(get)) {
					String responseBody = EntityUtils.toString(response.getEntity());
					System.out.println("API Response (HotelOffer): " + responseBody); // Debugging-Ausgabe

					JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);

					// Überprüfen, ob die Antwort Daten enthält
					if (jsonObject.has("data") && jsonObject.get("data").isJsonArray()) {
						JsonArray offersArray = jsonObject.getAsJsonArray("data"); // Liste der Angebote

						// Durchlaufen der Angebote und Speichern der Daten
						for (JsonElement offerElement : offersArray) {
							JsonObject offerData = offerElement.getAsJsonObject();

							String hotelId = offerData.get("hotel").getAsJsonObject().get("hotelId").getAsString(); // Hotel-ID
							String checkIn = formatToDate(offerData.get("offers").getAsJsonArray().get(0).getAsJsonObject().get("checkInDate").getAsString()); // Check-In-Datum
							String checkOut = formatToDate(offerData.get("offers").getAsJsonArray().get(0).getAsJsonObject().get("checkOutDate").getAsString()); // Check-Out-Datum
							double price = offerData.get("offers").getAsJsonArray().get(0).getAsJsonObject()
									.get("price").getAsJsonObject().get("total").getAsDouble(); // Preis des Angebots

							System.out.println("Price for hotel " + hotelId + ": " + price); // Log

							// Zimmerdaten aus dem Angebot extrahieren
							JsonObject roomData = offerData.get("offers").getAsJsonArray().get(0).getAsJsonObject()
									.get("room").getAsJsonObject();
							int beds = roomData.has("typeEstimated") && roomData.get("typeEstimated").getAsJsonObject().has("beds")
									? roomData.get("typeEstimated").getAsJsonObject().get("beds").getAsInt() : 0; // Anzahl der Betten
							String bedType = roomData.has("typeEstimated") && roomData.get("typeEstimated").getAsJsonObject().has("bedType")
									? roomData.get("typeEstimated").getAsJsonObject().get("bedType").getAsString() : "Not Specified"; // Art des Bettes
							String description = roomData.has("description") && roomData.get("description").getAsJsonObject().has("text")
									? roomData.get("description").getAsJsonObject().get("text").getAsString() : "No description available"; // Beschreibung des Zimmers

							int adultsInt = Integer.parseInt(adults); // Anzahl der Erwachsenen
							int roomsInt = Integer.parseInt(rooms); // Anzahl der Zimmer

							// Hotelangebot zu Liste hinzufügen
							hotelOffers.add(new HotelOfferData(hotelId, checkIn, checkOut, price, beds, bedType, description, adultsInt, roomsInt));
						}
					}
				}
			}
		}

		return hotelOffers; // Rückgabe der Liste der Hotelangebote
	}

	// Methode zum Anfügen der Angebote an die Hotels
	private void attachOffersToHotels(List<HotelData> hotels, List<HotelOfferData> hotelOffers) {
		for (HotelData hotel : hotels) {
			for (HotelOfferData offer : hotelOffers) {
				// Wenn die Hotel-ID mit dem Angebot übereinstimmt, das Angebot hinzufügen
				if (hotel.getHotelCode().equals(offer.getHotelId())) {
					hotel.setOffer(offer);
					hotel.setCheckInDate(offer.getCheckInDate());
					hotel.setCheckOutDate(offer.getCheckOutDate());

					System.out.println("Offer attached to hotel: " + hotel.getHotelName() + " with price: " + offer.getPrice()); // Log 
					break;
				}
			}
		}
	}

	// Methode, um das Datumsformat "yyyy-MM-dd" korrekt zu formatieren
	private String formatToDate(String dateTime) {
		// Das Datums-String in ein LocalDate umwandeln und formatieren
		return LocalDate.parse(dateTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
}

