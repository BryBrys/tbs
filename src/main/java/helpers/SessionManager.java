package helpers;

import org.hibernate.Session;
import org.hibernate.Transaction;

import classEntities.Benutzer;
import classEntities.Buchung;
import classEntities.Transfer;

public class SessionManager {

    private static SessionManager instance;

    // Variable, die den eingeloggenen Benutzer speichert
    private Benutzer loggedInUser;

    // Privater Konstruktor, um das Singleton-Pattern zu erzwingen
    // Verhindert, dass von außen neue Instanzen der Klasse erstellt werden
    private SessionManager() {}

    // Methode zum Abrufen der Instanz (erstellt sie, falls sie noch nicht existiert)
    // Implementierung des Singleton-Patterns
    public static SessionManager getInstance() {
        if (instance == null) {
            // Falls noch keine Instanz existiert, wird sie erstellt
            instance = new SessionManager();
        }
        return instance;  // Rückgabe der Instanz
    }

    // Methode zum Abrufen des eingeloggenen Benutzers
    public Benutzer getLoggedInUser() {
        return loggedInUser;
    }

    // Methode zum Setzen des Benutzers nach erfolgreicher Anmeldung
    public void setLoggedInUser(Benutzer benutzer) {
        this.loggedInUser = benutzer;  // Speichert den aktuell eingeloggten Benutzer
    }

    // Methode zum Abmelden des Benutzers, setzt den Benutzer auf null
    public void logout() {
        loggedInUser = null;  // Löscht den aktuellen Benutzer aus der Session
    }

    // Methode zum Aktualisieren des aktuell eingeloggten Benutzers
    public void updateUser(Benutzer updatedUser) {
        if (loggedInUser != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    // Initialize the user's bookings (buchungen) to prevent lazy loading errors
                    Benutzer persistentUser = session.get(Benutzer.class, updatedUser.getBenutzerId());
                    if (persistentUser != null && persistentUser.getBuchungen() != null) {
                        persistentUser.getBuchungen().size();  // Force Hibernate to load the collection
                    }
                    
                    // Now merge the updated user
                    session.merge(updatedUser);
                    transaction.commit();  // Commit the transaction
                    
                    loggedInUser = updatedUser;  // Update the session's user instance
                    System.out.println("User successfully updated.");
                } catch (Exception e) {
                    if (transaction != null) transaction.rollback();
                    e.printStackTrace();
                    System.out.println("Error updating user.");
                }
            }
        } else {
            System.out.println("No user is logged in.");
        }
    }

//    public void updateUser(Benutzer updatedUser) {
//        if (loggedInUser != null) {
//            // Benutzer aktualisieren, falls ein Benutzer eingeloggt ist
//            try {
//                // Datenbank aktualisieren
//                saveToDatabase(updatedUser);
//                // Aktualisierten Benutzer in der Session speichern
//                loggedInUser = updatedUser;
//                System.out.println("Benutzer erfolgreich aktualisiert.");
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Fehler beim Aktualisieren des Benutzers.");
//            }
//        } else {
//            System.out.println("Kein Benutzer ist eingeloggt.");
//        }
//    }

    // Methode zum Löschen des aktuell eingeloggten Benutzers
    public void deleteUser() {
        if (loggedInUser != null) {
            try {
                // Benutzer aus der Datenbank löschen
                deleteFromDatabase(loggedInUser);
                // Abmelden nach dem Löschen
                logout();
                System.out.println("Benutzer erfolgreich gelöscht.");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Fehler beim Löschen des Benutzers.");
            }
        } else {
            System.out.println("Kein Benutzer ist eingeloggt.");
        }
    }

    // Methode zum Speichern der Benutzerdaten in der Datenbank
    private void saveToDatabase(Benutzer user) {
        // Datenbank-Logik zum Aktualisieren der Benutzerdaten (hier mit Hibernate)
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        
        try {
            session.merge(user);  // Benutzer aktualisieren
            transaction.commit();  // Transaktion abschließen
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();  // Rollback im Fehlerfall
            }
            throw e;  // Fehler weitergeben
        } finally {
            session.close();  // Sitzung schließen
        }
    }

    // Methode zum Löschen des Benutzers aus der Datenbank
    private void deleteFromDatabase(Benutzer user) {
        // Datenbank-Logik zum Löschen der Benutzerdaten (hier mit Hibernate)
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        
        try {
            session.remove(user);  // Benutzer löschen
            transaction.commit();  // Transaktion abschließen
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();  // Rollback im Fehlerfall
            }
            throw e;  // Fehler weitergeben
        } finally {
            session.close();  // Sitzung schließen
        }
    }

    // Methode zum Löschen einer Buchung
    public void deleteBooking(Buchung buchung) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        
        try {
            session.remove(buchung);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); 
            }
            e.printStackTrace();
            throw e; 
        } finally {
            session.close();
        }
    }

    // Methode zum Speichern einer Transferbuchung
    public void saveTransferBooking(Transfer transfer) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.merge(transfer);  // Transferbuchung speichern
            tx.commit();  // Transaktion abschließen
        } catch (Exception e) {
            if (tx != null) tx.rollback();  // Rollback der Transaktion, wenn ein Fehler auftritt
            e.printStackTrace();
        } finally {
            session.close();  // Sitzung schließen
        }
    }
}
