package io.github.concordcommunication.desktop.model;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.concordcommunication.desktop.client.ConcordApi;
import io.github.concordcommunication.desktop.client.dto.api.ChatResponse;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class Channel {
	private final ConcordApi api;
	private final Server server;
	private final LongProperty id;
	private final StringProperty name;
	private final StringProperty description;
	private final IntegerProperty ordinality;
	private final ObservableList<Channel> children;
	private final List<Chat> chats;
	private final BooleanProperty atEnd;
	private final BooleanProperty atStart;

	private final Set<ChannelChatListener> chatListeners;

	public Channel(Server server, long id, String name, String description, int ordinality) {
		this.api = server.getConcordApi();
		this.server = server;
		this.id = new SimpleLongProperty(id);
		this.name = new SimpleStringProperty(name);
		this.description = new SimpleStringProperty(description);
		this.ordinality = new SimpleIntegerProperty(ordinality);
		this.children = FXCollections.observableArrayList();
		this.chats = new LinkedList<>();
		this.atEnd = new SimpleBooleanProperty(true);
		this.atStart = new SimpleBooleanProperty(false);
		this.chatListeners = new HashSet<>();
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

	public void addChatListener(ChannelChatListener listener) {
		this.chatListeners.add(listener);
	}

	public void fetchLatest() {
		this.api.getJson("/channels/" + this.getId() + "/chats/latest", ChatResponse[].class)
				.thenAcceptAsync(chatsArray -> {
					var newChats = Arrays.stream(chatsArray)
							.map(c -> new Chat(this, c.id(), c.createdAt(), c.authorId(), c.channelId(), c.threadId(), c.content(), c.edited()))
							.toList();
					this.chats.clear();
					this.chats.addAll(newChats);
					this.chatListeners.forEach(l -> l.chatsSet(newChats));
				});
	}

	public void appendChat(Chat chat) {
		if (!this.chats.contains(chat)) {
			this.chats.add(chat);
			this.chatListeners.forEach(l -> l.chatsAppended(List.of(chat)));
		}
	}

	public void fetchBackward() {
		String url = "/chats?channelId=" + this.getId();
		if (!this.chats.isEmpty()) {
			url += "&before=" + this.chats.get(0).getCreatedAt();
		}
		System.out.println("Fetching from " + url);
		this.api.getJson(url, ChatResponse[].class)
				.thenAcceptAsync(chatsArray -> {
					var newChats = Arrays.stream(chatsArray)
							.map(c -> new Chat(this, c.id(), c.createdAt(), c.authorId(), c.channelId(), c.threadId(), c.content(), c.edited()))
							.filter(c -> !this.chats.contains(c))
							.toList();
					this.chats.addAll(0, newChats);
					this.chatListeners.forEach(l -> l.chatsPrepended(newChats));
					System.out.println("New chat count: " + this.chats.size());
				});
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

	@Override
	public String toString() {
		return this.name.get();
	}
}
