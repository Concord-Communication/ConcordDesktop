package io.github.concordcommunication.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ConcordDesktop extends Application {
	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(ConcordDesktop.class.getResource("main-view.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
		stage.setTitle("Concord Desktop");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}