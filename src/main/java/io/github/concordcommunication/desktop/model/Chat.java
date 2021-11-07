package io.github.concordcommunication.desktop.model;

import javafx.beans.property.*;

public class Chat implements Comparable<Chat> {
	private final Channel channel;
	private final LongProperty id;
	private final LongProperty createdAt;
	private final LongProperty authorId;
	private final LongProperty channelId;
	private final ObjectProperty<Long> threadId;
	private final StringProperty content;
	private final BooleanProperty edited;
	// TODO: Add reaction data.
	public Chat(Channel channel, long id, long createdAt, long authorId, long channelId, Long threadId, String content, boolean edited) {
		this.channel = channel;
		this.id = new SimpleLongProperty(id);
		this.createdAt = new SimpleLongProperty(createdAt);
		this.authorId = new SimpleLongProperty(authorId);
		this.channelId = new SimpleLongProperty(channelId);
		this.threadId = new SimpleObjectProperty<>(threadId);
		this.content = new SimpleStringProperty(content);
		this.edited = new SimpleBooleanProperty(edited);
	}

	public Channel getChannel() {
		return channel;
	}

	public long getId() {
		return id.get();
	}

	public LongProperty idProperty() {
		return id;
	}

	public long getCreatedAt() {
		return createdAt.get();
	}

	public LongProperty createdAtProperty() {
		return createdAt;
	}

	public long getAuthorId() {
		return authorId.get();
	}

	public LongProperty authorIdProperty() {
		return authorId;
	}

	public long getChannelId() {
		return channelId.get();
	}

	public LongProperty channelIdProperty() {
		return channelId;
	}

	public Long getThreadId() {
		return threadId.get();
	}

	public ObjectProperty<Long> threadIdProperty() {
		return threadId;
	}

	public String getContent() {
		return content.get();
	}

	public StringProperty contentProperty() {
		return content;
	}

	public boolean isEdited() {
		return edited.get();
	}

	public BooleanProperty editedProperty() {
		return edited;
	}

	@Override
	public int compareTo(Chat o) {
		return Long.compare(this.getCreatedAt(), o.getCreatedAt());
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Chat other && this.getId() == other.getId();
	}

	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());
	}
}
