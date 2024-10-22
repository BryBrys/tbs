package helpers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TransferAPIService {
    private static final String API_URL = "https://test.api.amadeus.com/v1/shopping/transfer-offers";
    private static final String API_KEY = "I2w5UqOxi9t8qmAACcboa4pah3HA9CoP"; 
    private static final String API_SECRET = "AjDGVWbFRMJS8ZwR"; 

    private String accessToken;

    // Authentifizierungsmethode
    private void authenticate() throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://test.api.amadeus.com/v1/security/oauth2/token");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            String body = "grant_type=client_credentials&client_id=" + API_KEY + "&client_secret=" + API_SECRET;
            post.setEntity(new StringEntity(body, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = null;
				try {
					responseBody = EntityUtils.toString(response.getEntity());
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
                Gson gson = new Gson();
                // JSON-Parsing der Antwort und Zugriff auf den Access Token
                accessToken = gson.fromJson(responseBody, JsonObject.class).get("access_token").getAsString();
            }
        }
    }

    // Methode zum Abrufen der Transferangebote
    public String getTransferOffers(TransferOfferRequest transferRequest) throws IOException {
        if (accessToken == null) {
            authenticate();
        }

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(API_URL);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + accessToken);

            // Konvertiert TransferOfferRequest zu JSON
            Gson gson = new Gson();
            String jsonBody = gson.toJson(transferRequest);
            post.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                String responseBody = null;
				try {
					responseBody = EntityUtils.toString(response.getEntity());
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
                // Hier wird die Antwort (responseBody) zurückgegeben
                return responseBody;
            }
        }
    }
}
