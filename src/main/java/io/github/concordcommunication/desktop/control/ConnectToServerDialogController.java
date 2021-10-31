package io.github.concordcommunication.desktop.control;

import io.github.concordcommunication.desktop.ServerConnectData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class ConnectToServerDialogController {
	private Consumer<ServerConnectData> dataConsumer;

	@FXML
	public TextField serverAddressField;
	@FXML
	public TextField usernameField;
	@FXML
	public PasswordField passwordField;

	@FXML
	public void initialize() {
	}

	public void onComplete(Consumer<ServerConnectData> dataConsumer) {
		this.dataConsumer = dataConsumer;
	}

	public void cancel(ActionEvent actionEvent) {
		Node node = (Node) actionEvent.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		stage.close();
	}

	public void connect(ActionEvent actionEvent) {
		Node node = (Node) actionEvent.getSource();
		Stage stage = (Stage) node.getScene().getWindow();
		stage.close();
		this.dataConsumer.accept(new ServerConnectData(serverAddressField.getText().trim(), usernameField.getText().trim(), passwordField.getText()));
	}
}
