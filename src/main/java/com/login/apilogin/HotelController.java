package com.login.apilogin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;

import classEntities.Benutzer;
import helpers.HotelData;
import helpers.HotelOfferData;
import helpers.HotelService;
import helpers.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class HotelController {

	private Benutzer benutzer;

	@FXML
	private ComboBox<String> cbx_adults;
	@FXML
	private ComboBox<String> cbx_rooms;
	@FXML
	private ListView<HotelData> hotel_auswahl_list;
	@FXML
	private Button btn_cancel;
	@FXML
	private Label error_lbl;
	@FXML
	private TextField txt_city;
	@FXML
	private DatePicker date_checkin;
	@FXML
	private DatePicker date_checkout;
	@FXML
	private Button btn_flugsuche;
	@FXML
	private Button btn_transfersuche;
	@FXML
	private Button btn_buchungen;
	@FXML
	private Button btn_konto;
	@FXML
	private Button btn_abmelden;
	@FXML
	private CheckBox cbx_testdaten;

	@FXML
	private CheckComboBox<String> checkComboBox;

	private Map<String, String> cityToIataMap = new HashMap<>(); // Maps city-country to IATA code
	private ObservableList<String> citySuggestions = FXCollections.observableArrayList();
	private Popup popup = new Popup();
	private ListView<String> suggestionList = new ListView<>();

	// Methode zum Wechseln des Fensters zu "Login"
	@FXML
	private void btnLoginSwitch() throws IOException {
		// Benutzer wird ausgeloggt
		SessionManager.getInstance().logout();
		// Zurück zur Login Fenster
		App.setRoot("login");
	}
	// Methode zum Wechseln des Fensters zu "Flug"
	@FXML
	private void btnFlugSwitch() throws IOException {
		// Zurück zur Flug Fenster
		App.setRoot("flug");
	}
	// Methode zum Wechseln des Fensters zu "Transfer"
	@FXML
	private void btnTransferSwitch() throws IOException {
		App.setRoot("transfer");
	}
	// Methode zum Öffnen des Kontos-Fensters
	@FXML
	private void btnKontoSwitch() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/login/apilogin/konto.fxml"));
		Parent root = fxmlLoader.load();
		Stage stage = new Stage();
		stage.setTitle("Konto");
		stage.setScene(new Scene(root));
		stage.show();
	}
	// Methode zum Öffnen des Buchungen-Fensters
	@FXML
	private void btnBuchungenSwitch() throws IOException {
		// Lade die Datei buchungen.fxml
		FXMLLoader loader = new FXMLLoader(getClass().getResource("buchungen.fxml"));
		Parent root = loader.load();

		// Hole den Controller und übergebe die aktuelle Scene
		BuchungenController buchungenController = loader.getController();
		buchungenController.setMainStage((Stage) btn_buchungen.getScene().getWindow());  // Übergebe bei Bedarf die aktuelle Bühne

		// Öffne eine neues zur Anzeige der Buchungen des Benutzers
		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.setTitle("Ihre Buchungen");
		stage.show();
	}

	public void setBenutzer(Benutzer benutzer) {
		this.benutzer = benutzer;
	}
	// Methode, die bei Initialisierung des Controllers aufgerufen wird
	public void initialize() throws IOException {
		loadCsvData();
		setupHotelList();

		try {
			hotel_list.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		} catch (NullPointerException e) {
			System.out.println("Error: CSS file not found.");
			e.printStackTrace();
		}

		setupAdultsComboBox();
		setupRoomsComboBox();
		initCCB();
		setupAutoComplete(txt_city);

		// Listener für das Check-in-Datum, um das Check-out-Datum automatisch zu setzen
		date_checkin.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				LocalDate checkoutDate = date_checkout.getValue();
				if (checkoutDate == null || !checkoutDate.isAfter(newValue)) {
					date_checkout.setValue(newValue.plusDays(1)); // Sicher dass CheckoutDatum 1 Tag nach Checkindatum ist
				}
			}
		});
		
		// Listener für checkinDatum hinzufügen, um checkoutDatum zu aktualisieren
				date_checkin.valueProperty().addListener((observable, oldValue, newValue) -> {
					if (newValue != null) {
						// Überprüfen, ob der gewählte Hinflugdatum in der Vergangenheit liegt
						if (newValue.isBefore(LocalDate.now())) {
							// Setzen Sie das CheckinDatum auf das heutige Datum, wenn das ausgewählte Datum in der Vergangenheit liegt
							date_checkin.setValue(LocalDate.now());

							// Zeigt eine Warnung an, dass das CheckIn nicht in der Vergangenheit liegen darf
							Alert alert = new Alert(Alert.AlertType.WARNING);
							alert.setTitle("Ungültiges Datum");
							alert.setHeaderText(null);
							alert.setContentText("Das CheckIn-Datum kann nicht in der Vergangenheit liegen.");
							alert.showAndWait();
							return;
						}

						// Aktuelles Datum von date_rueckflug abrufen, falls gesetzt
						LocalDate checkoutDate = date_checkout.getValue();

						if (checkoutDate != null) {
							// Sicherstellen, dass der CheckOut mindestens 1 Tag nach dem CheckIn liegt
							if (!checkoutDate.isAfter(newValue)) {
								date_checkout.setValue(newValue.plusDays(1));  // CheckOut auf 1 Tag nach dem CheckIn setzen
							}
						} else {
							// Falls noch kein CheckOut-Datum gewählt wurde, auf 1 Tag nach dem CheckIn setzen
							date_checkout.setValue(newValue.plusDays(1));
						}
					}
				});
	}
	// Methode dass die CheckComboBox mit Annehmlichkeiten/Zubehör
	public void initCCB() {
		checkComboBox.getItems().addAll(
				"Swimming pool", "SPA", "Fitness center", "Air conditioning", "Restaurant",
				"Parking", "Pets allowed", "Business center", "WIFI", "Tennis", "Golf",
				"Kitchen", "Animal watching", "Beach", "Casino", "Sauna", "Massage",
				"Bar or Lounge", "Mini bar", "Wi-Fi in room", "Room service"
				);
	}
	// CSV-Datei mit Städte-Daten laden
	private void loadCsvData() throws IOException {
		String csvFilePath = "src/main/resources/airport_data.csv";
		BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
		String line;

		br.readLine();

		while ((line = br.readLine()) != null) {
			String[] data = line.split(",");
			if (data.length > 2) {
				String country = data[0].trim();
				String city = data[1].trim();
				String iataCode = data[2].trim();

				String cityCountry = city + ", " + country;
				citySuggestions.add(cityCountry);
				cityToIataMap.put(cityCountry, iataCode);
			}
		}
		br.close();
	}
	// Methode des AutoComplete
	private void setupAutoComplete(TextField textField) {
		textField.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			String input = textField.getText().toLowerCase();
			if (input.isEmpty()) {
				popup.hide(); // versteck PopUp wenn kein Input
				return;
			}

			List<String> filteredSuggestions = citySuggestions.stream()
					.filter(cityCountry -> cityCountry.toLowerCase().startsWith(input))
					.limit(10) // nur 10 anzeigen, zwecks leistung
					.collect(Collectors.toList());

			if (filteredSuggestions.isEmpty()) {
				popup.hide();
				return;
			}

			// Aktualisiert die ListView nach gefilterten Vorschlägen
			suggestionList.setItems(FXCollections.observableArrayList(filteredSuggestions));

			// Zeigt PopUp wenn es noch nicht angezeigt wird
			if (!popup.isShowing()) {
				// Überprüft ob PopUp bereits besteht
				if (!popup.getContent().contains(suggestionList)) {
					popup.getContent().add(suggestionList);
				}
				
				// Position des TextFeldes am Bildschirm bestimmen
				Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
				popup.show(textField.getScene().getWindow(), bounds.getMinX(), bounds.getMaxY());
			}

			// Übernimmt den Vorschlag, wenn Benutzer dieses klickt
			suggestionList.setOnMouseClicked(mouseEvent -> {
				String selectedItem = suggestionList.getSelectionModel().getSelectedItem();
				if (selectedItem != null) {
					textField.setText(selectedItem);
					popup.hide();
				}
			});
		});

		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				popup.hide(); // Versteckt PopUp wenn Fokus velroren geht
			}
		});
	}


	static class HotelCell extends ListCell<HotelData> {
		// Label-Eigenschaften setzen
		private void setLabelProperties(Label... labels) {
			for (Label label : labels) {
				label.setStyle("-fx-text-fill: white; -fx-font-family: 'Arial'; -fx-font-size: 16px;");
			}
		}

		// Methode zum Füllen der custom ListView
		@Override
		protected void updateItem(HotelData hotel, boolean empty) {
			super.updateItem(hotel, empty);
			// Inhalt löschen, wenn die Zelle leer ist
			if (empty || hotel == null) {
				setText(null);
				setGraphic(null);
			} else {
				// Erstellen eines Containers für den Inhalt
				HBox hBox = new HBox(10);
				hBox.setPrefWidth(950);
				hBox.setBackground(new Background(new BackgroundFill(Color.web("#3ab7b7"), new CornerRadii(15), Insets.EMPTY)));
				hBox.setPadding(new Insets(10, 20, 10, 20));
				hBox.setStyle("-fx-alignment: center;");

				// Erstellen einer VBox für das Hotelbild
				VBox firstVBox = new VBox(10);
				firstVBox.setStyle("-fx-alignment: center;");
				ImageView hotelPicture;

				// Verwenden eines eindeutigen zufälligen Bildes für jedes Hotel basierend auf dessen Daten
				int randomNumber = Math.abs(hotel.getHotelName().hashCode()) % 17 + 1;
				String imagePath = String.format("/img/hotels/hotel_%d.jpg", randomNumber);

				try {
					hotelPicture = new ImageView(getClass().getResource(imagePath).toExternalForm());
				} catch (NullPointerException e) {
					System.out.println("Hotelbild nicht gefunden für " + hotel.getHotelName());
					hotelPicture = new ImageView(); // Fallback, falls das Bild nicht gefunden wird
				}

				hotelPicture.setFitHeight(150); // Gewünschte Höhe festlegen
				hotelPicture.setPreserveRatio(true); // Seitenverhältnis beibehalten
				firstVBox.getChildren().addAll(hotelPicture);

				VBox secondVBox = new VBox(5);
				secondVBox.setStyle("-fx-alignment: center;");
				Label hotelName = new Label(hotel.getHotelName());
				hotelName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

				String amenitiesText = String.join(", ", hotel.getAmenities());
				Label hotelAmenities = new Label("Ausstattung: " + amenitiesText);
				String descriptionText = (hotel.getOffer() != null && hotel.getOffer().getDescription() != null) 
						? hotel.getOffer().getDescription() : "Keine Beschreibung vorhanden";
				Label hotelDescription = new Label("Beschreibung: " + descriptionText);

				if(amenitiesText.isEmpty()) {
					secondVBox.getChildren().addAll(hotelName, hotelDescription);
				} else {
					secondVBox.getChildren().addAll(hotelName, hotelAmenities, hotelDescription);
				}

				// VBox für den Auswahl-Button
				VBox priceBox = new VBox(10);
				priceBox.setStyle("-fx-alignment: center;");
				Label hotelPrice = new Label(String.format("%.2f €", hotel.getOffer().getPrice()));
				hotelPrice.setFont(new Font("Arial", 30));
				hotelPrice.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
				hotelPrice.getStyleClass().add("price-label");

				Button selectButton = new Button("Auswählen");
				selectButton.setPrefHeight(20.0);
				selectButton.setPrefWidth(150.0);
				selectButton.setFont(new Font("Arial", 18));
				selectButton.setStyle("-fx-background-color: #fff; -fx-border-color: white; -fx-border-width: 1; " +
						"-fx-border-style: solid; -fx-background-radius: 10; -fx-border-radius: 10; -fx-text-fill: #3ab7b7; -fx-font-weight: bold;");
				selectButton.getStyleClass().add("button-custom");

				// Aktion für den "Auswählen"-Button festlegen
				selectButton.setOnAction(event -> {
					HotelDetailsWindow auswahlWindow = new HotelDetailsWindow();
					auswahlWindow.openHotelDetailsWindow(hotel);
				});

				priceBox.getChildren().addAll(hotelPrice, selectButton);

				// Erstellen vertikaler weißer Linien
				Region line1 = new Region();
				line1.setPrefWidth(2);
				line1.setStyle("-fx-background-color: white;");

				Region line2 = new Region();
				line2.setPrefWidth(2);
				line2.setStyle("-fx-background-color: white;");

				// Fügen Sie alle VBox-Container und Linien zur HBox hinzu
				hBox.getChildren().addAll(firstVBox, line1, secondVBox, line2, priceBox);

				// Sicherstellen, dass jede VBox die richtige Menge an Platz einnimmt
				firstVBox.setPrefWidth(300); // Feste Breite für die Bild-VBox
				secondVBox.setPrefWidth(400);
				priceBox.setPrefWidth(190);

				// Setze HGrow für jede VBox auf ALWAYS, um die gleichmäßige Verteilung zu gewährleisten
				HBox.setHgrow(firstVBox, Priority.ALWAYS);
				HBox.setHgrow(secondVBox, Priority.ALWAYS);
				HBox.setHgrow(priceBox, Priority.ALWAYS);

				// Setzen der HBox als Inhalt der ListCell
				setGraphic(hBox);

				// Layout: Eigenschaften auf die Labels anwenden
				setLabelProperties(hotelName, hotelPrice, hotelAmenities, hotelDescription, hotelAmenities);
			}
		}

	}


	@FXML
	private ListView<HotelData> hotel_list;

	// Methode für die custom CellFactory
	private void setupHotelList() {
		hotel_list.setCellFactory(param -> new HotelCell());
	}

	// Methode die vorgefertigte Datensätze lädt
	private void TestloadHotelData() {
	    // Beispiel-Hotels erstellen
	    HotelData hotel1 = new HotelData("MUC", new String[] {"WLAN", "Fitnessstudio"}, "Hilton Munich City", "HMC123");
	    hotel1.setCheckInDate("2024-10-01");
	    hotel1.setCheckOutDate("2024-10-05");
	    hotel1.setOffer(new HotelOfferData("HMC123", "2024-10-01", "2024-10-05", 150.00, 2, "Doppelbett", "Komfortables Zimmer", 2, 1));

	    HotelData hotel2 = new HotelData("FRA", new String[] {"Parkplatz", "Frühstück"}, "Frankfurt Marriott Hotel", "FMH456");
	    hotel2.setCheckInDate("2024-11-01");
	    hotel2.setCheckOutDate("2024-11-05");
	    hotel2.setOffer(new HotelOfferData("FMH456", "2024-11-01", "2024-11-05", 200.00, 3, "Einzelbett", "Luxuriöses Zimmer", 1, 1));

	    // Liste mit den Test-Hotels füllen
	    List<HotelData> hotels = new ArrayList<>();
	    hotels.add(hotel1);
	    hotels.add(hotel2);

	    ObservableList<HotelData> hotelObservableList = FXCollections.observableArrayList(hotels);
	    hotel_list.setItems(hotelObservableList);
	}


	// Methode zum Laden der Hotelangebote
	private void loadHotelData() {
		try {
			// Stadt-Eingabe vom Benutzer abrufen
			String city = txt_city.getText().trim();

			// Check-in und Check-out Daten vom Benutzer abrufen
			String checkInDate = date_checkin.getValue().toString();
			String checkOutDate = date_checkout.getValue().toString();

			String adults = cbx_adults.getValue().toString().split(" ")[0];
			String rooms = cbx_rooms.getValue().toString().split(" ")[0];

			// IATA-Code für die Stadt abrufen
			String cityCode = cityToIataMap.getOrDefault(city, "");

			// Verarbeiten des ausgewählten Zubehörs: in Großbuchstaben umwandeln, Leerzeichen durch Unterstriche ersetzen und mit Kommas trennen
			String amenities = checkComboBox.getCheckModel().getCheckedItems().stream()
					.map(amenity -> amenity.toUpperCase().replace(" ", "_"))
					.collect(Collectors.joining(","));

			System.out.println("Ausgewählte Annehmlichkeiten: " + amenities);

			// Stadtcode validieren
			if (cityCode.isEmpty()) {
				System.out.println("Ungültige Stadt");
				error_lbl.setText("Ungültige Stadt");
				return; // Methode verlassen, wenn der Stadtcode nicht gefunden wurde
			}

			// Methode des Hotel-Service aufrufen mit allen Parametern
			HotelService hotelService = new HotelService();
			List<HotelData> hotels = hotelService.getHotels(cityCode, amenities, checkInDate, checkOutDate, adults, rooms);

			// Hotels herausfiltern, die keine gültigen Angebote haben
			List<HotelData> validHotels = hotels.stream()
					.filter(hotel -> hotel.getOffer() != null && hotel.getOffer().getPrice() > 0)
					.collect(Collectors.toList());

			// ListView mit den gefilterten Hotels aktualisieren
			ObservableList<HotelData> hotelObservableList = FXCollections.observableArrayList(validHotels);
			hotel_list.setItems(hotelObservableList);

			// Fehlermeldung anzeigen, falls keine gültigen Hotels gefunden wurden
			if (validHotels.isEmpty()) {
				error_lbl.setText("Keine gültigen Hotelangebote gefunden.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			error_lbl.setText("Fehler beim Laden der Hotelangebote.");
		}
	}

	// Methode um TestDaten oder API Daten zu laden
	@FXML
	private void handleSucheStarten() {
		String cityName = txt_city.getText().trim();

		if (!cityName.isEmpty()) {
			error_lbl.setText(null); // Fehlermeldungen löschen

			if (cbx_testdaten.isSelected()) {
				// Testdaten für Hotels laden
				TestloadHotelData();
				error_lbl.setText("Test Daten geladen");
			} else {
				// API-Aufruf zum Abrufen echter Daten
				error_lbl.setAccessibleHelp("API Daten geladen");
				loadHotelData();
			}
		} else {
			error_lbl.setText("Bitte alle Felder ausfüllen!");
		}
	}

	// Füge Auswahlmöglichkeiten für die Anzahl der Erwachsenen hinzu
	private void setupAdultsComboBox() {
		cbx_adults.getItems().addAll(
				"1 Erwachsener",
				"2 Erwachsene",
				"3 Erwachsene",
				"4 Erwachsene",
				"5 Erwachsene"
				);
		cbx_adults.getSelectionModel().selectFirst();
	}
	// Füge Auswahlmöglichkeiten für die Anzahl der Zimmer hinzu
	private void setupRoomsComboBox() {
		cbx_rooms.getItems().addAll(
				"1 Zimmer",
				"2 Zimmer",
				"3 Zimmer",
				"4 Zimmer",
				"5 Zimmer",
				"6 Zimmer",
				"7 Zimmer",
				"8 Zimmer",
				"9 Zimmer"
				);
		cbx_rooms.getSelectionModel().selectFirst();
	}
}
