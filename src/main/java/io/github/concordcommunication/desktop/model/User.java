package io.github.concordcommunication.desktop.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.*;

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
		Profile profile = new Profile(createdAt, nickname, bio, avatarId);
		var statusNode = node.get("status");
		String onlineStatus = statusNode.get("onlineStatus").asText();
		Status status = new Status(onlineStatus);
		return new User(server, id, username, profile, status);
	}

	public static class Profile {
		private final LongProperty createdAt;
		private final StringProperty nickname;
		private final StringProperty bio;
		private final ObjectProperty<Long> avatarId;

		public Profile(long createdAt, String nickname, String bio, Long avatarId) {
			this.createdAt = new SimpleLongProperty(createdAt);
			this.nickname = new SimpleStringProperty(nickname);
			this.bio = new SimpleStringProperty(bio);
			this.avatarId = new SimpleObjectProperty<>(avatarId);
		}
	}

	public static class Status {
		private final StringProperty onlineStatus;

		public Status(String status) {
			this.onlineStatus = new SimpleStringProperty(status);
		}
	}
}
