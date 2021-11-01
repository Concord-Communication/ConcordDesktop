package io.github.concordcommunication.desktop.control;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.concordcommunication.desktop.ServerConnectData;
import io.github.concordcommunication.desktop.client.ConcordApi;
import io.github.concordcommunication.desktop.model.ClientModel;
import io.github.concordcommunication.desktop.model.Server;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {
	@FXML
	public Accordion serversAccordion;
	@FXML
	public BorderPane mainBorderPane;
	@FXML
	public AnchorPane centerViewPane;

	public void initialize() {
		ClientModel.INSTANCE.getServers().addListener(new ServersListChangeListener(serversAccordion, centerViewPane));
		var serverConnectData = new ServerConnectData("localhost:8080", "admin", "kiSnfO4U5GUvdxDYWT7omb7eA2zHyuja7kS7BJgX");
		var api = new ConcordApi(serverConnectData.address(), serverConnectData.username(), serverConnectData.password());
		var connectionFuture = api.connect();
		connectionFuture.thenComposeAsync(unused -> api.getJson("/server", ObjectNode.class))
				.thenAccept(data -> {
					String name = data.get("name").asText();
					String description = data.get("description").asText();
					Long iconId = data.get("iconId").asLong();
					var server = new Server(api, name, description, iconId);
					ClientModel.INSTANCE.addServer(server);
				});
	}

	@FXML
	public void openConnectToServerDialog(ActionEvent actionEvent) throws IOException {
		FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource("/io/github/concordcommunication/desktop/connect-to-server.fxml"));
		Parent parent = dialogLoader.load();
		ConnectToServerDialogController controller = dialogLoader.getController();
		controller.onComplete(serverConnectData -> {
			var api = new ConcordApi(serverConnectData.address(), serverConnectData.username(), serverConnectData.password());
			var connectionFuture = api.connect();
			connectionFuture.thenComposeAsync(unused -> api.getJson("/server", ObjectNode.class))
					.thenAccept(data -> {
						String name = data.get("name").asText();
						String description = data.get("description").asText();
						Long iconId = data.get("iconId").asLong();
						var server = new Server(api, name, description, iconId);
						ClientModel.INSTANCE.addServer(server);
					});
			});
		Scene scene = new Scene(parent);
		Stage stage = new Stage();
		stage.setTitle("Connect to a Server");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setScene(scene);
		stage.showAndWait();
	}
}
