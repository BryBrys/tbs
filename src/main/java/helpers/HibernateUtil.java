package helpers;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import classEntities.Benutzer;
import classEntities.Buchung;
import classEntities.Flug;
import classEntities.Hotel;
import classEntities.Transfer;
import classEntities.Zahlung;

public class HibernateUtil {
    // Variable für die Hibernate SessionFactory
    private static SessionFactory sessionFactory;

    // Statischer Initialisierungsblock zum Erstellen der SessionFactory
    static {
        try {
            // SessionFactory wird basierend auf den Einstellungen aus der hibernate.cfg.xml erstellt
            sessionFactory = new Configuration()
            		.configure("hibernate.cfg.xml") // Läd die Hibernate-Konfigurationsdatei
            		.addAnnotatedClass(Benutzer.class) // Fügt die Entitätsklasse "Benutzer" hinzu
            		.addAnnotatedClass(Buchung.class)  // Fügt die Entitätsklasse "Buchung" hinzu
            		.addAnnotatedClass(Hotel.class)    // Fügt die Entitätsklasse "Hotel" hinzu
            		.addAnnotatedClass(Flug.class)     // Fügt die Entitätsklasse "Flug" hinzu
            		.addAnnotatedClass(Transfer.class) // Fügt die Entitätsklasse "Transfer" hinzu
            		.addAnnotatedClass(Zahlung.class)  // Fügt die Entitätsklasse "Zahlung" hinzu
            		.buildSessionFactory();            // Erstellt die SessionFactory
        } catch (Throwable ex) {
            // Fehlerbehandlung, falls die Initialisierung fehlschlägt
            throw new ExceptionInInitializerError(ex);
        }
    }

    // Methode zum Abrufen der SessionFactory
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    // Methode zum Schließen der SessionFactory (z.B. beim Beenden der Anwendung)
    public static void shutdown() {
        getSessionFactory().close();
    }
}
