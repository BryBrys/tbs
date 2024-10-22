package classEntities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "kunde")
public class Benutzer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "benutzerId")
    private int benutzerId;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "address")
    private String address;

    @Column(name = "plz")
    private String plz;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "benutzer", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Buchung> buchungen;

    public List<Buchung> getBuchungen() {
        if (buchungen == null) {
            buchungen = new ArrayList<>();
        }
        return buchungen;
    }

    public void setBuchungen(List<Buchung> buchungen) {
        this.buchungen = buchungen;
    }

    public Benutzer() {
    }

    public Benutzer(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public int getBenutzerId() {
        return benutzerId;
    }

    public void setBenutzerId(int benutzerId) {
        this.benutzerId = benutzerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Benutzer [benutzerId=" + benutzerId + ", firstName=" + firstName + ", lastName=" + lastName + ", email="
                + email + ", password=" + password + "]";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
