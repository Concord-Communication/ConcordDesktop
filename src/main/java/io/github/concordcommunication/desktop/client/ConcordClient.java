package io.github.concordcommunication.desktop.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.concordcommunication.desktop.client.dto.ConcordWebsocketClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ConcordClient {
	public static final ObjectMapper mapper = new ObjectMapper();

	private final HttpClient httpClient = HttpClient.newHttpClient();
	private ConcordWebsocketClient webSocketClient;
	private String address;
	private String username;
	private String password;

	private String token;
	private String baseApiUrl;

	public ConcordClient(String address, String username, String password) {
		this.address = address;
		this.username = username;
		this.password = password;
		this.baseApiUrl = "http://" + address + "/api";
		post("/api/tokens", mapper.createObjectNode()
				.put("username", this.username)
				.put("password", this.password),
				JsonNode.class
		).thenAcceptAsync(node -> {
			this.token = node.get("token").asText();
			this.webSocketClient = new ConcordWebsocketClient(URI.create("ws://" + address + "/client?token=" + this.token));
			this.webSocketClient.connect();
		}).join();
	}

	public void addListener(ConcordEventListener listener) {
		this.webSocketClient.addListener(listener);
	}

	public <T> CompletableFuture<T> get(String url, Class<T> type) {
		var request = HttpRequest.newBuilder(URI.create(url))
				.GET()
				.header("Authorization", "Bearer " + this.token)
				.header("Accept", "application/json")
				.timeout(Duration.ofSeconds(3))
				.build();
		return requestJson(request, type);
	}

	public <T> CompletableFuture<T> post(String url, Object body, Class<T> responseType) {
		var request = HttpRequest.newBuilder(URI.create(this.baseApiUrl + url))
				.POST(jsonPublisher(body))
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.build();
		return requestJson(request, responseType);
	}

	public <T> CompletableFuture<T> requestJson(HttpRequest request, Class<T> type) {
		return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
				.thenApply(response -> {
					try {
						return mapper.readValue(response.body(), type);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
	}

	public static HttpRequest.BodyPublisher jsonPublisher(Object obj) {
		try {
			byte[] bytes = mapper.writeValueAsBytes(obj);
			return HttpRequest.BodyPublishers.ofByteArray(bytes);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
