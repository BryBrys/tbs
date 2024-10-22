package com.login.apilogin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import classEntities.Benutzer;
import helpers.FlightData;
import helpers.FlightService;
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

public class FlugController {

	private Benutzer benutzer;

	// Methode zum Setzen des eingeloggten Benutzers
	public void setBenutzer(Benutzer benutzer) {
		this.benutzer = benutzer;
	}

	@FXML
	private TextField txt_von, txt_nach;
	@FXML
	private DatePicker date_hinflug, date_rueckflug;
	@FXML
	private Button btn_hotelsuche, btn_transfersuche, btn_buchungen, btn_konto, btn_abmelden, btn_passwort_vergessen;
	@FXML
	private Label flug_error_lbl;
	@FXML
	private CheckBox cbx_testdaten;
	@FXML
	private ComboBox<String> cbx_anzahl;

	// Methode zum Wechseln des Fensters zu "Login"
	@FXML
	private void btnLoginSwitch() throws IOException {
		// Benutzer wird ausgeloggt
		SessionManager.getInstance().logout();
		// Zurück zur Login Fenster
		App.setRoot("login");
	}
	// Methode zum Wechseln des Fensters zu "Hotel"
	@FXML
	private void btnHotelSwitch() throws IOException {
		// Wechsel zu Hotelsuche
		App.setRoot("hotel");
	}
	// Methode zum Wechseln des Fensters zu "Transfer"
	@FXML
	private void btnTransferSwitch() throws IOException {
		// Wechsel zu Transfersuche
		App.setRoot("transfer");
	}
	// Methode zum Öffnen des Kontos-Fensters
	@FXML
	private void btnKontoSwitch() throws IOException {
		// Lade konto.fxml
		FXMLLoader loader = new FXMLLoader(getClass().getResource("konto.fxml"));
		Parent root = loader.load();

		// Hole den Controller von konto.fxml und übergebe die Referenz zur flug.fxml-Bühne
		KontoController kontoController = loader.getController();
		kontoController.setMainStage((Stage) btn_konto.getScene().getWindow());

		// Öffne konto.fxml in einem neuen Fenster
		Stage stage = new Stage();
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
		buchungenController.setMainStage((Stage) btn_buchungen.getScene().getWindow());

		// Öffne eine neues zur Anzeige der Buchungen des Benutzers
		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.setTitle("Ihre Buchungen");
		stage.show();
	}

	private ObservableList<String> citySuggestions = FXCollections.observableArrayList();

	// Popup für AutoCompletion
	private Popup popup = new Popup();
	private ListView<String> suggestionList = new ListView<>();

	// Methode, die bei Initialisierung des Controllers aufgerufen wird
	public void initialize() throws IOException {
		// CSV-Daten laden
		loadCsvData();
		// Setup der ListView
		setupFlightList();
		// Scrollbar Farbe ändern
		try {
			flug_list.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		} catch (NullPointerException e) {
			System.out.println("Error: CSS file not found.");
			e.printStackTrace();
		}

		// AutoComplete-Funktion für die Textfelder "Von" und "Nach" einrichten
		setupAutoComplete(txt_von);
		setupAutoComplete(txt_nach);

		// Listener für date_hinflug hinzufügen, um date_rueckflug zu aktualisieren
		date_hinflug.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				// Überprüfen, ob der gewählte Hinflugdatum in der Vergangenheit liegt
				if (newValue.isBefore(LocalDate.now())) {
					// Setzen Sie das Hinflugdatum auf das heutige Datum, wenn das ausgewählte Datum in der Vergangenheit liegt
					date_hinflug.setValue(LocalDate.now());

					// Zeigt eine Warnung an, dass das Hinflugdatum nicht in der Vergangenheit liegen darf
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("Ungültiges Datum");
					alert.setHeaderText(null);
					alert.setContentText("Das Hinflugdatum kann nicht in der Vergangenheit liegen.");
					alert.showAndWait();
					return;
				}

				// Aktuelles Datum von date_rueckflug abrufen, falls gesetzt
				LocalDate rueckflugDate = date_rueckflug.getValue();

				if (rueckflugDate != null) {
					// Sicherstellen, dass der Rückflug mindestens 1 Tag nach dem Hinflug liegt
					if (!rueckflugDate.isAfter(newValue)) {
						date_rueckflug.setValue(newValue.plusDays(1));  // Rückflug auf 1 Tag nach dem Hinflug setzen
					}
				} else {
					// Falls noch kein Rückflug-Datum gewählt wurde, auf 1 Tag nach dem Hinflug setzen
					date_rueckflug.setValue(newValue.plusDays(1));
				}
			}
		});

		setupComboBox();
	}

	private Map<String, String> cityToIataMap = new HashMap<>(); // Maps city-country to IATA code

	// Methode zum Laden der CSV File 
	private void loadCsvData() throws IOException {
		String csvFilePath = "src/main/resources/airport_data.csv";
		BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
		String line;
		List<String> cityCountryList = new ArrayList<>();

		br.readLine();

		while ((line = br.readLine()) != null) {
			String[] data = line.split(",");
			if (data.length > 2) { // Kontrolle dass genug Spalten vorhanden sind
				String country = data[0].trim();
				String city = data[1].trim();
				String iataCode = data[2].trim();

				String cityCountry = city + ", " + country;
				cityCountryList.add(cityCountry);
				cityToIataMap.put(cityCountry, iataCode);
			}
		}
		br.close();

		// Füllt Liste mit Städte-Länder Vorschlägen
		citySuggestions.addAll(cityCountryList.stream().distinct().collect(Collectors.toList()));
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
					.limit(10)  // nur 10 anzeigen, zwecks leistung
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

				// Setzt PopUp direkt unter dem TextFeld
				popup.show(textField.getScene().getWindow(),
						bounds.getMinX(),
						bounds.getMaxY());
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

	// ANZEIGE DATEN
	static class FlightCell extends ListCell<FlightData> {
		// Label-Eigenschaften setzen
		private void setLabelProperties(Label... labels) {
			for (Label label : labels) {
				label.setStyle("-fx-text-fill: white; -fx-font-family: 'Arial'; -fx-font-size: 16px;");
			}
		}
		
		// Methode zum Füllen der custom ListView
		@Override
		protected void updateItem(FlightData flight, boolean empty) {
			super.updateItem(flight, empty);
			// Inhalt löschen, wenn die Zelle leer ist
			if (empty || flight == null) {
				setText(null);
				setGraphic(null);
			} else {
				// Erstellen einer HBox-Container für den Inhalt
				HBox hBox = new HBox(10);
				hBox.setPrefWidth(950);
				hBox.setBackground(new Background(new BackgroundFill(Color.web("#3ab7b7"), new CornerRadii(15), Insets.EMPTY)));
				hBox.setPadding(new Insets(10, 20, 10, 20));
				hBox.setStyle("-fx-alignment: center;"); // Zentrieren des Inhaltes innerhalb der HBox

				// VBox für Details des Airline
				VBox firstVBox = new VBox(10);
				firstVBox.setStyle("-fx-alignment: center;");

				// Erstellen der VBox-Elemente für die Airline und Flugnummer
				VBox outboundVBox = new VBox(5); // VBox für die Airline und Flugnummer
				outboundVBox.setStyle("-fx-alignment: center;");
				Label outboundAirline = new Label(flight.getOutboundAirlineName());
				Label outboundFlightNumberLabel = new Label(flight.getOutboundFlightNumber());
				outboundVBox.getChildren().addAll(outboundAirline, outboundFlightNumberLabel); // Hinzufügen der Labels zur VBox

				VBox returnVBox = new VBox(5); // VBox für die Rückfluginformationen
				returnVBox.setStyle("-fx-alignment: center;");
				Label returnAirline = new Label(flight.getRetirnAirlineName());
				Label returnFlightNumberLabel = new Label(flight.getReturnFlightNumber());
				returnVBox.getChildren().addAll(returnAirline, returnFlightNumberLabel); // Hinzufügen der Labels zur VBox

				firstVBox.getChildren().addAll(outboundVBox, returnVBox);

				// VBox für Ablug- & Zielort / Uhrzeit / Flugdauer
				VBox secondVBox = new VBox(5);
				secondVBox.setStyle("-fx-alignment: center;");

				// Erstellen der Labels für die Flugzeiten
				Label outboundTimeLabel = new Label(flight.getOutboundDepartureTime() + " " + flight.getOutboundDepartureIataCode());
				Label outboundFlightTimeLabel = new Label(flight.getOutboundFlightTime());
				Label returnTimeLabel = new Label(flight.getReturnArrivalTime() + " " + flight.getReturnArrivalIataCode());
				Label returnFlightTimeLabel = new Label(flight.getReturnFlightTime());

				// Laden der Pfeilbilder
				ImageView arrowRightImage = new ImageView(getClass().getResource("/img/arrow_right.png").toExternalForm());
				arrowRightImage.setFitHeight(10); // Setzen der Bildhöhe
				arrowRightImage.setPreserveRatio(true); // Verhältnis beibehalten
				ImageView arrowLeftImage = new ImageView(getClass().getResource("/img/arrow_left.png").toExternalForm());
				arrowLeftImage.setFitHeight(10); // Setzen der Bildhöhe
				arrowLeftImage.setPreserveRatio(true); // Verhältnis beibehalten

				Label outboundArrivalLabel = new Label(flight.getOutboundArrivalTime() + " " + flight.getOutboundArrivalIataCode());
				Label returnArrivalLabel = new Label(flight.getReturnDepartureTime() + " " + flight.getReturnDepartureIataCode());

				// Erstellen der HBoxen für die Flugdetails, damit wir Text und Bilder kombinieren können
				HBox outboundHBox = new HBox(5);
				outboundHBox.setStyle("-fx-alignment: center;");
				outboundHBox.getChildren().addAll(outboundTimeLabel, arrowRightImage, outboundArrivalLabel);

				HBox returnHBox = new HBox(5);
				returnHBox.setStyle("-fx-alignment: center;");
				returnHBox.getChildren().addAll(returnTimeLabel, arrowLeftImage, returnArrivalLabel);

				// Hinzufügen der HBoxen und Labels zur VBox
				secondVBox.getChildren().addAll(outboundHBox, outboundFlightTimeLabel, returnHBox, returnFlightTimeLabel);

				// VBox für Preis und Auswahl-Button
				VBox priceBox = new VBox(10);
				priceBox.setStyle("-fx-alignment: center;");
				Label priceLabel = new Label(String.format("%.2f €", flight.getPrice()));
				priceLabel.setFont(new Font("Arial", 30));
				priceLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
				priceLabel.getStyleClass().add("price-label");

				Button selectButton = new Button("Auswählen");
				selectButton.setPrefHeight(20.0);
				selectButton.setPrefWidth(150.0);
				selectButton.setFont(new Font("Arial", 18));
				selectButton.setStyle("-fx-background-color: #fff; -fx-border-color: white; -fx-border-width: 1; " +
						"-fx-border-style: solid; -fx-background-radius: 10; -fx-border-radius: 10; -fx-text-fill: #3ab7b7; -fx-font-weight: bold;");
				selectButton.getStyleClass().add("button-custom");

				// Aktion für Button "Auswählen" festlegen
				selectButton.setOnAction(event -> {
					FlugDetailsWindow detailsWindow = new FlugDetailsWindow();
					detailsWindow.openFlightDetailsWindow(flight);
				});

				priceBox.getChildren().addAll(priceLabel, selectButton);

				// Erstellen der vertikalen weißen Linien
				Region line1 = new Region();
				line1.setPrefWidth(2);
				line1.setStyle("-fx-background-color: white;");

				Region line2 = new Region();
				line2.setPrefWidth(2);
				line2.setStyle("-fx-background-color: white;");

				// Hinzufügen aller Boxen und Linien zur HBox
				hBox.getChildren().addAll(firstVBox, line1, secondVBox, line2, priceBox);

				// Sicherstellen, dass jede VBox denselben Platz in der HBox einnimmt
				firstVBox.setPrefWidth(300); // Feste Breite für die VBoxen
				secondVBox.setPrefWidth(400);
				priceBox.setPrefWidth(190);

				// Setze HGrow für jede VBox auf ALWAYS, um die gleichmäßige Verteilung zu gewährleisten
				HBox.setHgrow(firstVBox, Priority.ALWAYS);
				HBox.setHgrow(secondVBox, Priority.ALWAYS);
				HBox.setHgrow(priceBox, Priority.ALWAYS);

				// Setzen der HBox als den Inhalt der ListCell
				setGraphic(hBox);

				// Layout: Eigenschaften auf die Labels anwenden
				setLabelProperties(outboundAirline, outboundFlightNumberLabel, outboundFlightTimeLabel,
						outboundTimeLabel, returnAirline, returnTimeLabel, returnFlightNumberLabel,
						returnFlightTimeLabel, outboundArrivalLabel, returnArrivalLabel);
			}
		}

	}

	@FXML
	private ListView<FlightData> flug_list;

	// Methode für die custom CellFactory
	private void setupFlightList() {
		flug_list.setCellFactory(param -> new FlightCell());
	}

	// Methode die vorgefertigte Datensätze lädt
	private void TestloadFlightData() {
		List<FlightData> flights = new ArrayList<>();

		String outboundFlightNumber = "OS 123"; 
		String outboundAirline = "OS"; 
		String outboundAirlineName = "Austrian Airlines";
		String outboundDepartureTime = "15:22";
		String outboundArrivalTime = "18:10"; 
		String outboundDepartureIataCode = "VIE"; 
		String outboundArrivalIataCode="BER";
		String outboundFlightTime="2H15M"; 
		String returnFlightNumber="OS 456"; 
		String returnAirline="OS";
		String retirnAirlineName="Austrian Airlines";
		String returnDepartureTime="08:30";
		String returnArrivalTime="10:15"; 
		String returnDepartureIataCode="BER"; 
		String returnArrivalIataCode="VIE";
		String returnFlightTime="1H55M";
		String outboundDepartureDate="2024-11-23";
		String outboundArrivalDate="2024-11-23";
		String returnDepartureDate="2024-11-25";
		String returnArrivalDate="2024-11-25";
		boolean outboundDirectFlight=true;
		boolean returnDirectFlight=true;
		String adults="1";

		for (int i = 0; i < 6; i++) {
			flights.add(new FlightData(outboundFlightNumber+i, outboundAirline+i, 
					outboundAirlineName+i, outboundDepartureTime, outboundArrivalTime, 
					outboundDepartureIataCode+i, outboundArrivalIataCode+i, outboundFlightTime+i, 
					returnFlightNumber+i, returnAirline+i, retirnAirlineName+i, returnDepartureTime, 
					returnArrivalTime, returnDepartureIataCode+i, returnArrivalIataCode+i, 
					returnFlightTime, outboundDirectFlight, returnDirectFlight, 111.52+(+i*15),
					outboundDepartureDate, outboundArrivalDate, returnDepartureDate, returnArrivalDate, adults));
		}
		ObservableList<FlightData> flightObservableList = FXCollections.observableArrayList(flights);
		flug_list.setItems(flightObservableList);
	}

	// Methode zum Laden der Flugangebote
	private void loadFlightData() {
		try {
			// Benutzer-Eingaben für Abflug- und Zielort abrufen
			String originCityCountry = txt_von.getText().trim();
			String destinationCityCountry = txt_nach.getText().trim();

			// IATA-Codes aus der Map abrufen
			String originIataCode = cityToIataMap.getOrDefault(originCityCountry, "");
			String destinationIataCode = cityToIataMap.getOrDefault(destinationCityCountry, "");

			String adults = cbx_anzahl.getValue().toString().split(" ")[0];

			// Überprüfen, ob beide IATA-Codes gefunden wurden
			if (originIataCode.isEmpty() || destinationIataCode.isEmpty()) {
				System.out.println("Ungültiger Abflug- oder Zielort.");
				flug_error_lbl.setText("Ungültiger Abflug- oder Zielort.");
				return; // Methode verlassen, wenn die IATA-Codes nicht gefunden wurden
			}

			// Andere Flugdaten abrufen
			String departureDate = date_hinflug.getValue().toString();
			String returnDate = date_rueckflug.getValue().toString();
			String children = "0"; // Platzhalter für die Anzahl der Kinder

			// Flugdaten über den FlightService abrufen
			FlightService flightService = new FlightService();
			List<FlightData> flights = flightService.getFlights(originIataCode, destinationIataCode, departureDate, returnDate, adults, children);

			// Die ListView mit den abgerufenen Flugdaten aktualisieren
			ObservableList<FlightData> flightObservableList = FXCollections.observableArrayList(flights);
			flug_list.setItems(flightObservableList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Methode um TestDaten oder API Daten zu laden
	@FXML
	private void handleSucheStarten() {
		// Überprüfen, ob alle erforderlichen Felder ausgefüllt sind
		if (txt_von.getText() != null && txt_nach.getText() != null
				&& date_hinflug.getValue() != null && date_rueckflug.getValue() != null) {
			// Fehlerlabel zurücksetzen
			flug_error_lbl.setText(null);

			// Überprüfen, ob die Testdaten-Checkbox ausgewählt ist
			if (cbx_testdaten.isSelected()) {
				// Testdaten laden
				TestloadFlightData();
				flug_error_lbl.setText("Test Daten geladen");
			}
			else {
				// API-Daten laden
				flug_error_lbl.setAccessibleHelp("API Daten geladen");
				loadFlightData();
			}

		} else {
			// Fehlermeldung anzeigen, wenn nicht alle Felder ausgefüllt sind
			flug_error_lbl.setText("Bitte alle Felder ausfüllen!");
		}
	}

	// Füge Auswahlmöglichkeiten für die Anzahl der Erwachsenen hinzu
	private void setupComboBox() {
		cbx_anzahl.getItems().addAll(
				"1 Erwachsener",
				"2 Erwachsene",
				"3 Erwachsene",
				"4 Erwachsene",
				"5 Erwachsene"
				);

		// Standardmäßig die erste Auswahloption wählen
		cbx_anzahl.getSelectionModel().selectFirst();
	}

}
