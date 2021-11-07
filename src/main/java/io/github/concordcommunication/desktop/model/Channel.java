package io.github.concordcommunication.desktop.model;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.concordcommunication.desktop.client.ConcordApi;
import io.github.concordcommunication.desktop.client.dto.api.ChatResponse;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Channel {
	private final ConcordApi api;
	private final Server server;
	private final LongProperty id;
	private final StringProperty name;
	private final StringProperty description;
	private final IntegerProperty ordinality;
	private final ObservableList<Channel> children;
	private final ObservableList<Chat> chats;

	public Channel(Server server, long id, String name, String description, int ordinality) {
		this.api = server.getConcordApi();
		this.server = server;
		this.id = new SimpleLongProperty(id);
		this.name = new SimpleStringProperty(name);
		this.description = new SimpleStringProperty(description);
		this.ordinality = new SimpleIntegerProperty(ordinality);
		this.children = FXCollections.observableArrayList();
		this.chats = FXCollections.observableArrayList();
		this.api.getJson("/channels/" + id + "/chats/latest", ChatResponse[].class)
				.thenAcceptAsync(chatsArray -> {
					this.chats.clear();
					for (var c : chatsArray) {
						Chat chat = new Chat(this, c.id(), c.createdAt(), c.authorId(), c.channelId(), c.threadId(), c.content(), c.edited());
						this.chats.add(0, chat);
					}
				});
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

	public void appendChat(Chat chat) {
		if (!this.chats.contains(chat)) {
			this.chats.add(chat);
			if (this.chats.size() > 100) {
				this.chats.remove(0);
			}
		}
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
