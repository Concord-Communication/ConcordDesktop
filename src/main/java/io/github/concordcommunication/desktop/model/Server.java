package io.github.concordcommunication.desktop.model;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.concordcommunication.desktop.client.ConcordApi;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public class Server {
	private final ConcordApi concordApi;
	private final StringProperty name;
	private final StringProperty description;
	private final ObjectProperty<Image> icon;
	private final ObservableList<Channel> channels;

	public Server(ConcordApi concordApi, String name, String description, Long iconId) {
		this.concordApi = concordApi;
		this.name = new SimpleStringProperty(name);
		this.description = new SimpleStringProperty(description);
		this.icon = new SimpleObjectProperty<>(null);
		this.channels = FXCollections.observableArrayList();
		if (iconId != null) {
			concordApi.getImage(iconId).thenAcceptAsync(this.icon::setValue);
		}
		concordApi.getJson("/channels", ArrayNode.class)
				.thenAcceptAsync(channelsArray -> {
					this.channels.clear();
					for (var channelJson : channelsArray) {
						this.channels.add(Channel.fromJson(this, (ObjectNode) channelJson));
					}
				});
	}

	public ConcordApi getConcordApi() {
		return concordApi;
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

	public Image getIcon() {
		return icon.get();
	}

	public ObjectProperty<Image> iconProperty() {
		return icon;
	}

	public ObservableList<Channel> getChannels() {
		return channels;
	}
}
