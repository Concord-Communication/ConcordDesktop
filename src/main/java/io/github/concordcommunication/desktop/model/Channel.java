package io.github.concordcommunication.desktop.model;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.*;
import javafx.collections.ObservableList;

public class Channel {
	private LongProperty id;
	private StringProperty name;
	private StringProperty description;
	private IntegerProperty ordinality;
	private ObservableList<Channel> children;

	public Channel(long id, String name, String description, int ordinality) {
		this.id = new SimpleLongProperty(id);
		this.name = new SimpleStringProperty(name);
		this.description = new SimpleStringProperty(description);
		this.ordinality = new SimpleIntegerProperty(ordinality);
		this.children = new SimpleListProperty<>();
	}

	public static Channel fromJson(ObjectNode node) {
		long id = node.get("id").asLong();
		int ordinality = node.get("ordinality").asInt();
		String name = node.get("name").asText();
		String description = node.get("description").asText();
		ArrayNode children = node.withArray("children");
		Channel channel = new Channel(id, name, description, ordinality);
		for (var child : children) {
			if (child.isObject()) {
				channel.children.add(fromJson((ObjectNode) child));
			}
		}
		return channel;
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
