package io.github.concordcommunication.desktop.client;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Self {
	private final ConcordApi api;

	public Self(ConcordApi api) {
		this.api = api;
	}

	public CompletableFuture<Void> updatePassword(String newPassword) {
		return this.api.postJson("/self/password", Map.of("newPassword", newPassword), Void.TYPE);
	}
}
