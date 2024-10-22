package com.login.apilogin;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.ParseException;
import org.apache.poi.sl.draw.geom.ClosePathCommandIf;

import com.browniebytes.javafx.control.DateTimePicker;

import classEntities.Benutzer;
import classEntities.Buchung;
import classEntities.Transfer;
import helpers.SessionManager;
import helpers.TransferData;
import helpers.TransferOfferRequest;
import helpers.TransferService;
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

public class TransferController {

	private Benutzer benutzer;

	// Methode zum Setzen des eingeloggten Benutzers
	public void setBenutzer(Benutzer benutzer) {
		this.benutzer = benutzer;
	}

	@FXML
	private TextField txt_startOrt;
	@FXML
	private TextField txt_streetName;
	@FXML
	private TextField txt_streetNumber;
	@FXML
	private TextField txt_cityName;
	@FXML
	private TextField txt_plz;
	@FXML
	private TextField txt_country;
	@FXML
	private DateTimePicker startDateTime;
	//	@FXML
	//	private DateTimePicker endDateTime;
	@FXML
	private ComboBox<String> cbx_passengers;
	@FXML
	private Button btn_hotelsuche;
	@FXML
	private Button btn_buchungen;
	@FXML
	private Button btn_konto;
	@FXML
	private Button btn_abmelden;
	@FXML
	private Label error_lbl;
	@FXML
	private CheckBox cbx_testdaten;

	// Methode zum Wechseln des Fensters zu "Login"
	@FXML
	private void btnLoginSwitch() throws IOException {
		SessionManager.getInstance().logout();
		App.setRoot("login");
	}
	// Methode zum Wechseln des Fensters zu "Hotel"
	@FXML
	private void btnHotelSwitch() throws IOException {
		App.setRoot("hotel");
	}
	// Methode zum Wechseln des Fensters zu "Flug"
	@FXML
	private void btnFlugSwitch() throws IOException {
		App.setRoot("flug");
	}
	// Methode zum Öffnen des Kontos-Fensters
	@FXML
	private void btnKontoSwitch() throws IOException {
		// Lade konto.fxml
		FXMLLoader loader = new FXMLLoader(getClass().getResource("konto.fxml"));
		Parent root = loader.load();

		// Hole den Controller von konto.fxml und die zB. flug.fxml Stage-Referenz übergeben
		KontoController kontoController = loader.getController();
		kontoController.setMainStage((Stage) btn_konto.getScene().getWindow());

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
	// Methode zum Verarbeiten der Transferbuchung

	// Popup für AutoCompletion
	private Popup popup = new Popup();
	private ListView<String> suggestionList = new ListView<>();
	@FXML
	private void handleTransferBooking(TransferData selectedTransferData) {
		Transfer transfer = new Transfer();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
		LocalDateTime pickUpDateTime = LocalDateTime.parse(selectedTransferData.getPickUpDateTime(), formatter);

		// in LocalDate umwandeln
		LocalDate pickUpDate = pickUpDateTime.toLocalDate();
		LocalDate returnDate = pickUpDate; // unnötiger Datensatz

		// für Log
		System.out.println("HandleMethod PickUpDate: " + pickUpDate);
		System.out.println("HandleMethod ReturnDate: " + returnDate);

		transfer.setMietDatum(pickUpDate);
		transfer.setPreis((float)selectedTransferData.getPrice());

		String originCityCountry = txt_startOrt.getText().trim();
		String iataCode = cityToIataMap.getOrDefault(originCityCountry, "");
		if (iataCode.isEmpty()) {
			showAlert("Fehler", "StartOrt konnte nicht als IATA-Code gefunden werden.", Alert.AlertType.ERROR);
			return;
		}
		transfer.setStartOrt(iataCode);
		transfer.setZielOrt(selectedTransferData.getDropOffLocation());

		// Mit der aktuellen Buchung des Benutzers verknüpfen
		Buchung currentBuchung = (Buchung) SessionManager.getInstance().getLoggedInUser().getBuchungen();
		transfer.setBuchung(currentBuchung);

		// In der TransferData speichern
		SessionManager.getInstance().saveTransferBooking(transfer);
	}

	private TransferService transferService;

	public TransferController() throws IOException, InterruptedException {
		transferService = new TransferService();
	}

	// Methode, die bei Initialisierung des Controllers aufgerufen wird
	@FXML
	public void initialize() throws IOException {
		// CSV-Daten laden
		loadCsvData();
		loadCountryCapitalCsvData();
		// Scrollbar Farbe ändern
		try {
			transfer_list.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
		} catch (NullPointerException e) {
			System.out.println("Fehler: CSS File nicht gefunden");
			e.printStackTrace();
		}
		setupAutoComplete(txt_startOrt);
		setupCityAutoComplete(txt_cityName);
		setupComboBox(); // Setzt die Auswahl für die Passagieranzahl
	}

	private Map<String, String> cityToIataMap = new HashMap<>();

	// Lädt Daten von CSV File airportdata
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

	private Map<String, String[]> cityToCountryMap = new HashMap<>();
	// Lädt Daten von CSV File Country,Short,Capital
	private void loadCountryCapitalCsvData() throws IOException {
		String csvFilePath = "src/main/resources/CountryCodeCapital.csv";
		BufferedReader br = new BufferedReader(new FileReader(csvFilePath));
		String line;

		br.readLine();

		while ((line = br.readLine()) != null) {
			String[] data = line.split(",");
			if (data.length == 3) { 
				String country = data[0].trim(); 
				String countryShort = data[1].trim().toUpperCase(); 
				String capital = data[2].trim();

				cityToCountryMap.put(capital, new String[]{country, countryShort});
			}
		}
		br.close();
	}

	// Methode für AutoComplete "startOrt"
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

	// Methode für Autocomplete cityName & country
	private void setupCityAutoComplete(TextField textField) {
		Popup popup = new Popup();
		ListView<String> suggestionList = new ListView<>();

		textField.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			String input = textField.getText().toLowerCase();
			if (input.isEmpty()) {
				popup.hide(); 
				return;
			}

			List<String> filteredSuggestions = cityToCountryMap.keySet().stream()
					.filter(city -> city.toLowerCase().startsWith(input))
					.limit(10)
					.collect(Collectors.toList());

			if (filteredSuggestions.isEmpty()) {
				popup.hide();
				return;
			}

			suggestionList.setItems(FXCollections.observableArrayList(filteredSuggestions));

			if (!popup.isShowing()) {
				if (!popup.getContent().contains(suggestionList)) {
					popup.getContent().add(suggestionList);
				}

				Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
				popup.show(textField.getScene().getWindow(), bounds.getMinX(), bounds.getMaxY());
			}

			suggestionList.setOnMouseClicked(mouseEvent -> {
				String selectedCity = suggestionList.getSelectionModel().getSelectedItem();
				if (selectedCity != null) {
					textField.setText(selectedCity);
					String[] countryData = cityToCountryMap.get(selectedCity);

					txt_country.setText(countryData[0]);
					txt_country.setUserData(countryData[1].toUpperCase());

					popup.hide();
				}
			});
		});

		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				popup.hide();
			}
		});
	}

	// ANZEIGE DATEN
	static class TransferCell extends ListCell<TransferData> {
		private void setLabelProperties(Label... labels) {
			for (Label label : labels) {
				label.setStyle("-fx-text-fill: white; -fx-font-family: 'Arial'; -fx-font-size: 16px;");
			}
		}

		// Methode zum Füllen der custom ListView
		@Override
		protected void updateItem(TransferData transfer, boolean empty) {
			super.updateItem(transfer, empty);

			// Inhalt leeren, wenn die Zelle leer ist
			if (empty || transfer == null) {
				setText(null);
				setGraphic(null);
			} else {
				// Container für den Inhalt erstellen
				HBox hBox = new HBox(10);
				hBox.setPrefWidth(950);
				hBox.setBackground(new Background(new BackgroundFill(Color.web("#3ab7b7"), new CornerRadii(15), Insets.EMPTY)));
				hBox.setPadding(new Insets(10, 20, 10, 20));
				hBox.setStyle("-fx-alignment: center;");

				// VBox für das Bild erstellen
				VBox firstVBox = new VBox(10);
				firstVBox.setStyle("-fx-alignment: center;");
				ImageView hotelPicture;

				// Verwenden eines zufälligen Bildes für jedes Auto 
				int randomNumber = Math.abs(transfer.getCarType().hashCode()) % 28 + 1;
				String imagePath = String.format("/img/cars/car_%d.jpg", randomNumber);

				try {
					hotelPicture = new ImageView(getClass().getResource(imagePath).toExternalForm());
				} catch (NullPointerException e) {
					System.out.println("Image not found for " + transfer.getCarType());
					hotelPicture = new ImageView(); // Fallback, falls das Bild nicht gefunden wird
				}

				hotelPicture.setFitHeight(150); 
				hotelPicture.setPreserveRatio(true);

				firstVBox.getChildren().addAll(hotelPicture);

				// VBox für Autodetails
				VBox secondVBox = new VBox(5);
				secondVBox.setStyle("-fx-alignment: center;");

				Label carType = new Label(transfer.getCarType());
				Label description = new Label(transfer.getDescription());

				secondVBox.getChildren().addAll(carType, description);

				// VBox für den Auswahlbutton
				VBox thirdVbox = new VBox(10);
				thirdVbox.setStyle("-fx-alignment: center;");
				Label priceLabel = new Label(String.format("%.2f €", transfer.getPrice()));
				priceLabel.setFont(new Font("Arial", 30));
				priceLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
				priceLabel.getStyleClass().add("price-label");

				Button selectButton = new Button("Auswählen");
				selectButton.setPrefHeight(20.0);
				selectButton.setPrefWidth(150.0);
				selectButton.setFont(new Font("Arial", 18));
				selectButton.setStyle("-fx-background-color: #fff; -fx-border-color: white; -fx-border-width: 1; "
						+ "-fx-border-style: solid; -fx-background-radius: 10; -fx-border-radius: 10; -fx-text-fill: #3ab7b7; -fx-font-weight: bold;");
				selectButton.getStyleClass().add("button-custom");

				selectButton.setOnAction(event -> {
					TransferDetailsWindow auswahlWindow = new TransferDetailsWindow();
					auswahlWindow.openTransferDetailsWindow(transfer);
				});

				thirdVbox.getChildren().addAll(priceLabel, selectButton);

				// Erstellen der vertikalen weißen Linien
				Region line1 = new Region();
				line1.setPrefWidth(2);
				line1.setStyle("-fx-background-color: white;");
				Region line2 = new Region();
				line2.setPrefWidth(2);
				line2.setStyle("-fx-background-color: white;");

				hBox.getChildren().addAll(firstVBox, line1, secondVBox, line2, thirdVbox);

				// Sicherstellen, dass jede VBox denselben Platz in der HBox einnimmt
				firstVBox.setPrefWidth(300);// Feste Breite für die VBoxen
				secondVBox.setPrefWidth(400);
				thirdVbox.setPrefWidth(190);

				// Setze HGrow für jede VBox auf ALWAYS, um die gleichmäßige Verteilung zu gewährleisten
				HBox.setHgrow(firstVBox, Priority.ALWAYS);
				HBox.setHgrow(secondVBox, Priority.ALWAYS);
				HBox.setHgrow(thirdVbox, Priority.ALWAYS);

				// Setzen der HBox als den Inhalt der ListCell
				setGraphic(hBox);

				// Layout: Eigenschaften auf die Labels anwenden
				setLabelProperties(carType, description);
			}
		}
	}
	@FXML
	private ListView<TransferData> transfer_list;

	// Methode zum Laden der Transferangebote
	private void loadTransferOffers(String pickUpLocation, String dropOffLocation, String pickUpDate,
			String dropOffDate, int passengers, String streetName, String streetNumber, String cityName, String plz,
			String country) throws ParseException, InterruptedException {
		List<TransferData> transferOffers;
		try {
			transferOffers = transferService.getTransferOffers(pickUpLocation, dropOffLocation, pickUpDate,
					dropOffDate, passengers, streetName, streetNumber, cityName, plz, country);

			// Füllen der ListView mit den abgerufenen Transferangeboten
			ObservableList<TransferData> transferObservableList = FXCollections.observableArrayList(transferOffers);
			transfer_list.setItems(transferObservableList);

			transfer_list.setCellFactory(param -> new TransferCell());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Methode um TestDaten oder API Daten zu laden
	@FXML
	private void handleSucheStarten() {
		// Überprüfen, ob alle erforderlichen Felder ausgefüllt und nicht leer sind
		if (!isInputValid()) {
			// Fehlermeldung anzeigen, wenn nicht alle Felder ausgefüllt oder leer sind
			error_lbl.setText("Bitte alle Felder ausfüllen!");
			return;
		}

		// Zeit vom DateTimePicker abrufen
		Date startDateTimeValue = startDateTime.getTime();

		// Aktuelles Datum abrufen
		Date nowTime = new Date(); // Equivalent to LocalDateTime.now() as a Date

		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		// Datum mithilfe des Formatters in einen String umwandeln (optional for logging or further use)
		String starDate = dateFormatter.format(startDateTimeValue);
		String endDate = starDate;

		// Überprüfen, ob das Startdatum größer als das aktuelle Datum ist
		if (startDateTimeValue.before(nowTime)) {
			// Fehlermeldung anzeigen, wenn das Startdatum in der Vergangenheit liegt
			error_lbl.setText("Das Startdatum muss in der Zukunft liegen!");
			return;
		}

		// Fehlerlabel zurücksetzen
		error_lbl.setText(null);

		// Überprüfen, ob die Testdaten-Checkbox ausgewählt ist
		if (cbx_testdaten.isSelected()) {
			// Testdaten laden
			TestLoadTransferData();
			error_lbl.setText("Test Daten geladen");
		} else {
			// API-Daten laden
			error_lbl.setText("API Daten geladen");
			loadTransferData();
		}
	}



	// Methode zur Überprüfung, ob Textfelder leer sind
	private boolean isInputValid() {
		// Überprüfen, ob alle Eingaben in den Textfeldern ausgefüllt und nicht leer sind
		if (txt_startOrt.getText() == null || txt_startOrt.getText().isEmpty()) {
			error_lbl.setText("Bitte Startort eingeben!");
			return false;
		}
		if (txt_streetName.getText() == null || txt_streetName.getText().isEmpty()) {
			error_lbl.setText("Bitte Straßennamen eingeben!");
			return false;
		}
		if (txt_streetNumber.getText() == null || txt_streetNumber.getText().isEmpty()) {
			error_lbl.setText("Bitte Hausnummer eingeben!");
			return false;
		}
		if (txt_cityName.getText() == null || txt_cityName.getText().isEmpty()) {
			error_lbl.setText("Bitte Stadtname eingeben!");
			return false;
		}
		if (txt_plz.getText() == null || txt_plz.getText().isEmpty()) {
			error_lbl.setText("Bitte Postleitzahl eingeben!");
			return false;
		}
		if (txt_country.getText() == null || txt_country.getText().isEmpty()) {
			error_lbl.setText("Bitte Land eingeben!");
			return false;
		}
		return true;
	}

	// Methode zum Laden von Testdaten
	private void TestLoadTransferData() {
		List<TransferData> mockTransferDataList = new ArrayList<>();

		TransferData mockData1 = new TransferData(
				"Sedan",
				"Provider A",
				100.00,
				"EUR",
				"Vienna, Austria",
				"Munich, Germany",
				"2023-10-01T10:00:00",
				"2023-10-01T12:00:00",
				2,
				2,
				"Comfortable sedan with WiFi"
				);
		mockData1.setEndAddressLine("123 Main St");
		mockData1.setEndZipCode("80331");
		mockData1.setEndCityName("Munich");

		TransferData mockData2 = new TransferData(
				"SUV", 
				"Provider B", 
				150.00, 
				"EUR", 
				"Berlin, Germany", 
				"Paris, France", 
				"2023-10-02T08:00:00", 
				"2023-10-02T14:00:00", 
				4, 
				3, 
				"Spacious SUV with extra luggage space"
				);
		mockData2.setEndAddressLine("456 Elm St");
		mockData2.setEndZipCode("75008");
		mockData2.setEndCityName("Paris");

		TransferData mockData3 = new TransferData(
				"Luxury", 
				"Provider C", 
				300.00, 
				"EUR", 
				"London, UK", 
				"Edinburgh, UK", 
				"2023-10-03T09:00:00", 
				"2023-10-03T13:00:00", 
				2, 
				1, 
				"Luxury car with a professional chauffeur"
				);
		mockData3.setEndAddressLine("789 King St");
		mockData3.setEndZipCode("EH1 1BB");
		mockData3.setEndCityName("Edinburgh");

		mockTransferDataList.add(mockData1);
		mockTransferDataList.add(mockData2);
		mockTransferDataList.add(mockData3);

		ObservableList<TransferData> transferObservableList = FXCollections.observableArrayList(mockTransferDataList);
		transfer_list.setItems(transferObservableList);
		transfer_list.setCellFactory(param -> new TransferCell());

		System.out.println("Testdaten geladen");
	}

	// Methode zum Laden der Transferangebote
	private void loadTransferData() {
		// Daten aus den UI-Eingaben sammeln
		String startOrt = txt_startOrt.getText();
		String streetName = txt_streetName.getText();
		String streetNumber = txt_streetNumber.getText();
		String cityName = txt_cityName.getText();
		String plz = txt_plz.getText();
		String country = txt_country.getText();
		String countryShortCode = (String) txt_country.getUserData();

		if (countryShortCode == null || countryShortCode.isEmpty()) {
			showAlert("Fehler", "Land konnte nicht als Kurzcode gefunden werden.", Alert.AlertType.ERROR);
			return;
		}

		// Vollständige Adresse für den Rückgabeort zusammenstellen
		String dropOffLocation = streetName + " " + streetNumber + ", " + cityName + ", " + plz + ", " + country;

		// Zeit vom DateTimePicker abrufen
		Date pickUpDate = startDateTime.getTime();
		Date dropOffDate = pickUpDate;

		// Rohdaten der getTime()-Ausgabe ausgeben // LOG
		System.out.println("Rohdaten Abholdatum: " + pickUpDate);

		// Definieren des benötigten Formats (yyyy-MM-dd'T'HH:mm:ss)
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		// Datum mithilfe des Formatters in einen String umwandeln
		String pickUpDateString = dateFormatter.format(pickUpDate);
		String dropOffDateString = dateFormatter.format(dropOffDate);

		// LOG
		System.out.println("Formatiertes Abholdatum: " + pickUpDateString);

		// Anzahl der Passagiere abrufen
		int passengers = cbx_passengers.getSelectionModel().getSelectedIndex() + 1;

		// Eingaben validieren
		if (startOrt.isEmpty() || dropOffLocation.isEmpty() || pickUpDate == null || dropOffDate == null) {
			showAlert("Fehler", "Bitte füllen Sie alle Felder aus", Alert.AlertType.ERROR);
			return;
		}

		// Wandelt startOrt zu IATAcode
		String iataCode = cityToIataMap.getOrDefault(startOrt, null);

		if (iataCode == null || iataCode.isEmpty()) {
			showAlert("Fehler", "StartOrt konnte nicht als IATA-Code gefunden werden.", Alert.AlertType.ERROR);
			return;
		}

		try {
			List<TransferData> transferDataList = transferService.getTransferOffers(
					iataCode,
					dropOffLocation,
					pickUpDateString,
					dropOffDateString,
					passengers,
					txt_streetName.getText(),
					txt_streetNumber.getText(),
					cityName,
					txt_plz.getText(),
					countryShortCode
					);

			List<TransferOfferRequest> transferOfferRequests = new ArrayList<>();
			for (TransferData transferData : transferDataList) {
				TransferOfferRequest offerRequest = new TransferOfferRequest(transferData);
				transferOfferRequests.add(offerRequest);
			}

			ObservableList<TransferData> transferObservableList = FXCollections.observableArrayList(transferDataList);
			transfer_list.setItems(transferObservableList);
			transfer_list.setCellFactory(param -> new TransferCell());

		} catch (Exception e) {
			e.printStackTrace();
			showAlert("Fehler", "Ein Fehler ist aufgetreten: " + e.getMessage(), Alert.AlertType.ERROR);
		}
	}

	// Methode des Datums-Umwandler
	private LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
		return Instant.ofEpochMilli(dateToConvert.getTime())
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}

	// Methode zum Füllen der Combox Passagiere
	@FXML
	private void setupComboBox() {
		cbx_passengers.getItems().addAll(
				"1 Passagier",
				"2 Passagiere",
				"3 Passagiere",
				"4 Passagiere"
				);
		cbx_passengers.getSelectionModel().selectFirst(); // Setzen der ersten Eingabe als Bsp
	}

	// Methode der AlertMeldung für fehlende Felder
	private void showAlert(String title, String message, Alert.AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

}
