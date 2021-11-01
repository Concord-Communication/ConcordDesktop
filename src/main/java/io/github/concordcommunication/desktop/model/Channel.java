package io.github.concordcommunication.desktop.model;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Channel {
	private final Server server;
	private LongProperty id;
	private StringProperty name;
	private StringProperty description;
	private IntegerProperty ordinality;
	private ObservableList<Channel> children;
	private ObservableList<Chat> chats;

	public Channel(Server server, long id, String name, String description, int ordinality) {
		this.server = server;
		this.id = new SimpleLongProperty(id);
		this.name = new SimpleStringProperty(name);
		this.description = new SimpleStringProperty(description);
		this.ordinality = new SimpleIntegerProperty(ordinality);
		this.children = FXCollections.observableArrayList();
		this.chats = FXCollections.observableArrayList();
	}

	public static Channel fromJson(Server server, ObjectNode node) {
		long id = node.get("id").asLong();
		int ordinality = node.get("ordinality").asInt();
		String name = node.get("name").asText();
		String description = node.get("description").asText();
		ArrayNode children = node.withArray("children");
		Channel channel = new Channel(server, id, name, description, ordinality);
		for (var child : children) {
			if (child.isObject()) {
				channel.children.add(fromJson(server, (ObjectNode) child));
			}
		}
		return channel;
	}

	public Server getServer() {
		return server;
	}

	public long getId() {
		return id.get();
	}

	public LongProperty idProperty() {
		return id;
	}

	public String getName() {
		return name.get();
	}

	public StringProperty nameProperty() {
		return name;
	}

	public String getDescription() {
		return description.get();
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public int getOrdinality() {
		return ordinality.get();
	}

	public IntegerProperty ordinalityProperty() {
		return ordinality;
	}

	public ObservableList<Channel> getChildren() {
		return children;
	}

	public ObservableList<Chat> getChats() {
		return chats;
	}

	@Override
	public String toString() {
		return this.name.get();
	}
}
