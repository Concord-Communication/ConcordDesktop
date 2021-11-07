package io.github.concordcommunication.desktop.client;

import javafx.scene.image.Image;

public class Server {
	private final ConcordApi api;

	private String name;
	private String description;
	private Long iconId;
	private Long defaultChannelId;

	public Server(ConcordApi api, String name, String description, Long iconId, Long defaultChannelId) {
		this.api = api;
		this.name = name;
		this.description = description;
		this.iconId = iconId;
		this.defaultChannelId = defaultChannelId;
	}

	public ConcordApi getApi() {
		return api;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Long getIconId() {
		return iconId;
	}

	public Long getDefaultChannelId() {
		return defaultChannelId;
	}
}
