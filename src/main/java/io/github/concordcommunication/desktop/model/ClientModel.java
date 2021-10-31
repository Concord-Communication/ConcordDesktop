package io.github.concordcommunication.desktop.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClientModel {
	public static ClientModel INSTANCE = new ClientModel();

	ObservableList<Server> servers;

	public ClientModel() {
		this.servers = FXCollections.observableArrayList();
	}

	public ObservableList<Server> getServers() {
		return servers;
	}

	public void addServer(Server server) {
		this.servers.add(server);
	}
}
