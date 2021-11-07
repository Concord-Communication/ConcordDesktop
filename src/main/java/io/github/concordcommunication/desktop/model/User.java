package io.github.concordcommunication.desktop.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.concordcommunication.desktop.client.ConcordApi;
import javafx.beans.property.*;
import javafx.scene.image.Image;

public class User {
	private final Server server;
	private final LongProperty id;
	private final StringProperty username;
	private final ObjectProperty<Profile> profile;
	private final ObjectProperty<Status> status;

	public User(Server server, long id, String username, Profile profile, Status status) {
		this.server = server;
		this.id = new SimpleLongProperty(id);
		this.username = new SimpleStringProperty(username);
		this.profile = new SimpleObjectProperty<>(profile);
		this.status = new SimpleObjectProperty<>(status);
	}

	public static User fromJson(Server server, ObjectNode node) {
		long id = node.get("id").asLong();
		String username = node.get("username").asText();
		var profileNode = node.get("profile");
		long createdAt = profileNode.get("createdAt").asLong();
		String nickname = profileNode.get("nickname").asText();
		String bio = profileNode.get("bio").isNull() ? null : profileNode.get("bio").asText();
		Long avatarId = profileNode.get("avatarId").isNull() ? null : profileNode.get("avatarId").asLong();
		Profile profile = new Profile(server.getConcordApi(), createdAt, nickname, bio, avatarId);
		var statusNode = node.get("status");
		String onlineStatus = statusNode.get("onlineStatus").asText();
		Status status = new Status(onlineStatus);
		return new User(server, id, username, profile, status);
	}

	public long getId() {
		return id.get();
	}

	public LongProperty idProperty() {
		return id;
	}

	public String getUsername() {
		return username.get();
	}

	public StringProperty usernameProperty() {
		return username;
	}

	public Profile getProfile() {
		return profile.get();
	}

	public ObjectProperty<Profile> profileProperty() {
		return profile;
	}

	public Status getStatus() {
		return status.get();
	}

	public ObjectProperty<Status> statusProperty() {
		return status;
	}

	public static class Profile {
		private final ConcordApi api;
		private final LongProperty createdAt;
		private final StringProperty nickname;
		private final StringProperty bio;
		private final ObjectProperty<Long> avatarId;
		private final ObjectProperty<Image> avatarImage;

		public Profile(ConcordApi api, long createdAt, String nickname, String bio, Long avatarId) {
			this.api = api;
			this.createdAt = new SimpleLongProperty(createdAt);
			this.nickname = new SimpleStringProperty(nickname);
			this.bio = new SimpleStringProperty(bio);
			this.avatarId = new SimpleObjectProperty<>(avatarId);
			this.avatarImage = new SimpleObjectProperty<>(null);
			if (this.avatarId.get() != null) {
				api.getImage(this.avatarId.get()).thenAcceptAsync(this.avatarImage::setValue);
			}
		}

		public long getCreatedAt() {
			return createdAt.get();
		}

		public LongProperty createdAtProperty() {
			return createdAt;
		}

		public String getNickname() {
			return nickname.get();
		}

		public StringProperty nicknameProperty() {
			return nickname;
		}

		public String getBio() {
			return bio.get();
		}

		public StringProperty bioProperty() {
			return bio;
		}

		public Long getAvatarId() {
			return avatarId.get();
		}

		public ObjectProperty<Long> avatarIdProperty() {
			return avatarId;
		}

		public Image getAvatarImage() {
			return avatarImage.get();
		}

		public ObjectProperty<Image> avatarImageProperty() {
			return avatarImage;
		}
	}

	public static class Status {
		private final StringProperty onlineStatus;

		public Status(String status) {
			this.onlineStatus = new SimpleStringProperty(status);
		}

		public String getOnlineStatus() {
			return onlineStatus.get();
		}

		public StringProperty onlineStatusProperty() {
			return onlineStatus;
		}
	}
}
