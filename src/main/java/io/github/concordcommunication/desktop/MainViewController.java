package io.github.concordcommunication.desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {
	@FXML
	public Label serverNameLabel;
	@FXML
	public VBox serverChannelsBox;
	@FXML
	public TextArea chatInputArea;
	@FXML
	public VBox chatBox;

	@FXML
	public void openConnectToServerDialog(ActionEvent actionEvent) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("connect-to-server.fxml"));
		Parent parent = loader.load();
		ConnectToServerDialogController controller = loader.getController();
		controller.onComplete(serverConnectData -> {
			System.out.println(serverConnectData);
			// TODO: Try to log into server, set access token, and connect to websocket, and update all state.
		});
		Scene scene = new Scene(parent);
		Stage stage = new Stage();
		stage.setTitle("Connect to a Server");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		stage.showAndWait();
	}
}
