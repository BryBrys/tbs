package helpers;

import java.math.BigDecimal;
import java.util.Locale;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;


public class BraintreeService {
    
    // Konstanten für die Braintree-Sandbox-Anmeldeinformationen
    private static final String MERCHANT_ID = "z33j72258dctd7q6";  // Händler-ID
    private static final String PUBLIC_KEY = "9zxtzf9nf7zx7mhb";    // Öffentlicher Schlüssel
    private static final String PRIVATE_KEY = "5058308f2d67ecb4090a58bf99cc684f";  // Privater Schlüssel

    // Das BraintreeGateway-Objekt ermöglicht die Kommunikation mit der Braintree-API
    private BraintreeGateway gateway;

    // Konstruktor der Klasse, der das BraintreeGateway initialisiert und mit der Sandbox-Umgebung verbindet
    public BraintreeService() {
        this.gateway = new BraintreeGateway(
            Environment.SANDBOX,  // Die Umgebung wird auf "SANDBOX" gesetzt, das für Testzwecke verwendet wird
            MERCHANT_ID,          // Händler-ID
            PUBLIC_KEY,           // Öffentlicher Schlüssel
            PRIVATE_KEY           // Privater Schlüssel
        );
    }

    public boolean processPayment(String cardNumber, String expiryMonth, String expiryYear, String cvv, double amount) {
        // Erstellen einer Transaktionsanfrage mit den übergebenen Parametern
        TransactionRequest request = new TransactionRequest()
            // Der Zahlungsbetrag wird formatiert (immer mit 2 Dezimalstellen) und in ein BigDecimal umgewandelt.
            // Die Formatierung erfolgt nach dem US-Standard (z.B. "123.45").
            .amount(new BigDecimal(String.format(Locale.US, "%.2f", amount)))
            .creditCard()                // Kreditkartendetails werden hinzugefügt
                .number(cardNumber)      // Kreditkartennummer setzen
                .expirationMonth(expiryMonth)  // Ablaufmonat setzen
                .expirationYear(expiryYear)    // Ablaufjahr setzen
                .cvv(cvv)               // CVV-Sicherheitscode setzen
                .done();                // Abschluss der Kreditkartenkonfiguration

        // Durchführung der Transaktion über das BraintreeGateway
        Result<Transaction> result = gateway.transaction().sale(request);

        // Überprüfung, ob die Transaktion erfolgreich war
        if (result.isSuccess()) {
            // Wenn die Transaktion erfolgreich ist, wird die Transaktions-ID ausgegeben
            Transaction transaction = result.getTarget();
            System.out.println("Payment successful! Transaction ID: " + transaction.getId());
            return true;  // Erfolgreiche Zahlung
        } else if (result.getTransaction() != null) {
            // Wenn die Transaktion fehlschlägt, wird eine Fehlermeldung mit einer Antwort von der Bank ausgegeben
            Transaction transaction = result.getTransaction();
            System.out.println("Payment failed: " + transaction.getProcessorResponseText());
            return false;  // Zahlung fehlgeschlagen
        } else {
            // Wenn keine spezifische Transaktion erfolgt ist, wird eine allgemeine Fehlermeldung ausgegeben
            System.out.println("Payment failed: " + result.getMessage());
            return false;  // Zahlung fehlgeschlagen
        }
    }
}
