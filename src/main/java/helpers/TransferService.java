package helpers;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.hc.core5.http.ParseException;

public class TransferService {

	// Amadeus API & Positionstack credentials
	private static final String AMADEUS_API_URL = "https://test.api.amadeus.com/v1/shopping/transfer-offers";
	private static final String GEOCODE_API_URL = "http://api.positionstack.com/v1/forward";
	private static final String GEOCODE_API_KEY = "f19bc36a9907d39a8dbff435a23492fc";
	private static final String AMADEUS_API_KEY = "I2w5UqOxi9t8qmAACcboa4pah3HA9CoP";
	private static final String AMADEUS_API_SECRET = "AjDGVWbFRMJS8ZwR";
	private static final String AMADEUS_AUTH_URL = "https://test.api.amadeus.com/v1/security/oauth2/token";

	private String accessToken;

	// Konstruktor holt das Zugriffstoken
	public TransferService() throws IOException, InterruptedException {
		this.accessToken = authenticateAmadeus();
	}

	// Methode zur Authentifizierung und Erhalt des Access Tokens
	private String authenticateAmadeus() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String body = "grant_type=client_credentials&client_id=" + AMADEUS_API_KEY + "&client_secret=" + AMADEUS_API_SECRET;

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(AMADEUS_AUTH_URL))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() == 200) {
			Gson gson = new Gson();
			JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
			return jsonResponse.get("access_token").getAsString();
		} else {
			throw new IOException("Failed to authenticate with Amadeus API: " + response.body());
		}
	}

	// Transferangebote abrufen
	public List<TransferData> getTransferOffers(String pickUpLocation, String dropOffAddress, String pickUpDate, 
			String dropOffDate, int passengers, 
			String streetName, String streetNumber, String cityName, String plz, String country)
					throws IOException, ParseException, InterruptedException {


		// Konvertiere die Abgabeadresse in Geokoordinaten
		double[] geoCoordinates = getGeoCoordinatesFromAddress(dropOffAddress);
		if (geoCoordinates == null) {
			throw new ParseException("Unable to get valid geo-coordinates from the address");
		}

		// Erstelle request body für Amadeus API
		Map<String, Object> transferRequest = new HashMap<>();
		transferRequest.put("startLocationCode", pickUpLocation);
		transferRequest.put("startDateTime", pickUpDate);
		transferRequest.put("endGeoCode", String.format(Locale.US, "%.6f,%.6f", geoCoordinates[0], geoCoordinates[1]));
		transferRequest.put("endAddressLine", streetName + " " + streetNumber);
		transferRequest.put("endCityName", cityName);
		transferRequest.put("endZipCode", plz);
		transferRequest.put("endCountryCode", country);
		transferRequest.put("passengers", passengers);
		transferRequest.put("transferType", "PRIVATE");

		System.out.println("Amadeus HashMap: " + transferRequest); // Log

		// Sende die Anfrage an die Amadeus API und bearbeite die Antwort
		String responseBody = getTransferOffers(transferRequest);
		System.out.println("API Amadeus ResponseBody: " + responseBody);  // Log

		return parseTransferOffers(responseBody);
	}

	// Konvertiere eine Adresse in Geokoordinaten mit der PositionStack API
	private double[] getGeoCoordinatesFromAddress(String address) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
		String url = GEOCODE_API_URL + "?access_key=" + GEOCODE_API_KEY + "&query=" + encodedAddress;

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.GET()
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		System.out.println("PositionstackRequest: " + response); // Log

		if (response.statusCode() == 200) {
			Gson gson = new Gson();
			JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
			JsonArray dataArray = jsonResponse.getAsJsonArray("data");

			System.out.println("PositionstackResponse" + jsonResponse); // Log

			if (dataArray.size() > 0) {
				JsonObject location = dataArray.get(0).getAsJsonObject();
				double latitude = location.get("latitude").getAsDouble();
				double longitude = location.get("longitude").getAsDouble();
				return new double[]{latitude, longitude};

			} else {
				System.out.println("Für die Adresse wurden keine gültigen Koordinaten gefunden: " + address);
				return null;
			}
		} else {
			System.out.println("Geokoordinaten konnten nicht abgerufen werden: " + response.body());
			return null;
		}
	}

	// Rufe die Amadeus API für Transferangebote auf
	private String getTransferOffers(Map<String, Object> transferRequest) throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		Gson gson = new Gson();
		String requestBody = gson.toJson(transferRequest);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(AMADEUS_API_URL))
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + accessToken)
				.POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
				.build();

		System.out.println("API Request Amadeus: " + requestBody); // Log

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		System.out.println("API Response Amadeus: " + response.body());  // Log

		if (response.statusCode() == 200) {
			return response.body();
		} else {
			throw new IOException("Failed to retrieve transfer offers: " + response.body());
		}
	}

	private List<TransferData> parseTransferOffers(String responseBody) {
		List<TransferData> transferOffers = new ArrayList<>();

		// Überprüfen, ob der Antwortinhalt null oder leer ist
		if (responseBody == null || responseBody.isEmpty()) {
			System.out.println("Response body is null or empty");
			return transferOffers;
		}

		Gson gson = new Gson();
		JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);

		// Überprüfen, ob 'data' im Antwort-JSON vorhanden ist und nicht null ist
		if (responseJson.has("data") && responseJson.getAsJsonArray("data") != null) {
			JsonArray offersArray = responseJson.getAsJsonArray("data");

			// Überprüfen, ob das Array der Angebote nicht leer ist
			if (offersArray.size() > 0) {
				for (int i = 0; i < offersArray.size(); i++) {
					JsonObject offer = offersArray.get(i).getAsJsonObject();
					TransferData transferData = new TransferData();

					// Sicheres Abrufen der vorhandenen Daten
					if (offer.has("id") && !offer.get("id").isJsonNull()) {
						transferData.setId(offer.get("id").getAsString());
					}

					if (offer.has("vehicle") && offer.getAsJsonObject("vehicle").has("category")) {
						transferData.setCarType(offer.getAsJsonObject("vehicle").get("category").getAsString());
					}

					if (offer.has("vehicle") && offer.getAsJsonObject("vehicle").has("description")) {
						transferData.setCarType(offer.getAsJsonObject("vehicle").get("description").getAsString());
					}

					if (offer.has("serviceProvider") && offer.getAsJsonObject("serviceProvider").has("name")) {
						transferData.setProvider(offer.getAsJsonObject("serviceProvider").get("name").getAsString());
					}

					if (offer.has("converted") && offer.getAsJsonObject("converted").has("monetaryAmount")) {
						transferData.setPrice(offer.getAsJsonObject("converted").get("monetaryAmount").getAsDouble());
					}

					if (offer.has("converted") && offer.getAsJsonObject("converted").has("currencyCode")) {
						transferData.setCurrency(offer.getAsJsonObject("converted").get("currencyCode").getAsString());
					}

					if (offer.has("start") && offer.getAsJsonObject("start").has("locationCode")) {
						transferData.setPickUpLocation(offer.getAsJsonObject("start").get("locationCode").getAsString());
					}
					if (offer.has("end") && offer.getAsJsonObject("end").has("address") &&
							offer.getAsJsonObject("end").getAsJsonObject("address").has("cityName")) {
						transferData.setEndCityName(offer.getAsJsonObject("end").getAsJsonObject("address").get("cityName").getAsString());
					}

					if (offer.has("end") && offer.getAsJsonObject("end").has("address") &&
							offer.getAsJsonObject("end").getAsJsonObject("address").has("line")) {
						transferData.setEndAddressLine(offer.getAsJsonObject("end").getAsJsonObject("address").get("line").getAsString());
					}

					if (offer.has("end") && offer.getAsJsonObject("end").has("address") &&
							offer.getAsJsonObject("end").getAsJsonObject("address").has("zip")) {
						transferData.setEndZipCode(offer.getAsJsonObject("end").getAsJsonObject("address").get("zip").getAsString());
					}

					if (offer.has("start") && offer.getAsJsonObject("start").has("dateTime")) {
						transferData.setPickUpDateTime(offer.getAsJsonObject("start").get("dateTime").getAsString());
					}
					if (offer.has("end") && offer.getAsJsonObject("end").has("dateTime")) {
						transferData.setDropOffDateTime(offer.getAsJsonObject("end").get("dateTime").getAsString());
					}

					if (offer.has("vehicle") && offer.getAsJsonObject("vehicle").has("seats")) {
						JsonArray seatsArray = offer.getAsJsonObject("vehicle").getAsJsonArray("seats");
						if (seatsArray != null && seatsArray.size() > 0 && seatsArray.get(0).getAsJsonObject().has("count")) {
							transferData.setPassengers(seatsArray.get(0).getAsJsonObject().get("count").getAsInt());
						}
					}

					// Sicheres Abrufen und Setzen der Gepäckanzahl
					if (offer.has("vehicle") && offer.getAsJsonObject("vehicle").has("baggages")) {
						JsonArray baggageArray = offer.getAsJsonObject("vehicle").getAsJsonArray("baggages");
						if (baggageArray != null && baggageArray.size() > 0 && baggageArray.get(0).getAsJsonObject().has("count")) {
							transferData.setBaggage(baggageArray.get(0).getAsJsonObject().get("count").getAsInt());
						}
					}

					transferOffers.add(transferData);
				}
			} else {
				System.out.println("No offers found in the response");
			}
		} else {
			System.out.println("Response does not contain 'data' field");
		}

		return transferOffers;
	}
	public List<TransferData> mapTransferOfferRequestsToTransferData(List<TransferOfferRequest> transferOfferRequests) {
		List<TransferData> transferDataList = new ArrayList<>();

		// Iteriere über die Liste von TransferOfferRequest-Objekten
		for (TransferOfferRequest request : transferOfferRequests) {
			// Erstelle ein neues TransferData-Objekt für jede Anfrage
			TransferData transferData = new TransferData();

			// Mappe Felder von TransferOfferRequest zu TransferData
			transferData.setCarType(request.getTransferType()); 
			transferData.setPickUpLocation(request.getStartLocationCode());
			transferData.setDropOffLocation(request.getEndCityName());
			transferData.setPassengers(request.getPassengers());
			transferData.setPickUpDateTime(request.getStartDateTime()); 
			transferData.setDropOffLocation(request.getEndLocationCode()); 
			transferData.setPrice(request.getPreis());
			transferData.setCurrency(request.getCurrency());
			transferData.setEndAddressLine(request.getEndAddressLine());

			transferDataList.add(transferData);
		}

		return transferDataList;
	}

}
