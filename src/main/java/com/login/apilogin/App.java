package com.login.apilogin;

import java.io.IOException;

import classEntities.Benutzer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

	private static Stage stage;

	// Die Startmethode wird automatisch beim Start der JavaFX-Anwendung aufgerufen!!
	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		primaryStage.setTitle("Travel Booking System");
		setRoot("login");
		primaryStage.show();
		System.out.println("test");
	}

	public static void setRoot(String fxml) throws IOException {
		Scene scene = new Scene(loadFXML(fxml));
		stage.setScene(scene);
	}

	public static void setRoot(String fxml, Benutzer benutzer) throws IOException {
		FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		Parent root = loader.load();

		FlugController controller = loader.getController();
		controller.setBenutzer(benutzer);

		Scene scene = new Scene(root);
		stage.setScene(scene);
	}

	private static Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	// Die Main-Methode startet die JavaFX-Anwendung
	public static void main(String[] args) {
		launch();
	}
}
