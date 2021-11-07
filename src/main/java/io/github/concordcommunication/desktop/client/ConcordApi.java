package io.github.concordcommunication.desktop.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.github.concordcommunication.desktop.client.dto.ConcordWebsocketClient;
import io.github.concordcommunication.desktop.client.dto.api.ServerData;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ConcordApi {
	public static final ObjectMapper mapper = new ObjectMapper();
	static {
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	private final HttpClient httpClient;
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
	private ConcordWebsocketClient webSocketClient;

	private String address;
	private String username;
	private String password;

	private volatile String token;
	private String baseApiUrl;

	public ConcordApi(String address, String username, String password) {
		this.httpClient = HttpClient.newBuilder().executor(this.executorService).build();
		this.address = address;
		this.username = username;
		this.password = password;
		this.baseApiUrl = "http://" + address + "/api";
	}

	/**
	 * Obtains a token with this client's credentials, and connects to the
	 * server's websocket.
	 * @return A completable future that resolves once a connection is established.
	 */
	public CompletableFuture<Void> connect() {
		return postAnonJson("/tokens", mapper.createObjectNode()
						.put("username", this.username)
						.put("password", this.password),
				JsonNode.class
		).thenAcceptAsync(node -> {
			this.token = node.get("token").asText();
			this.webSocketClient = new ConcordWebsocketClient(URI.create("ws://" + address + "/client?token=" + this.token));
			this.webSocketClient.connect();
		}).thenRunAsync(() -> {
			getJson("/users", ArrayNode.class)
					.thenAcceptAsync(usersArray -> {

					});
		});
	}

	public CompletableFuture<Void> disconnect() {
		CompletableFuture<Void> future = new CompletableFuture<>();
		this.executorService.submit(() -> {
			this.token = null;
			if (this.webSocketClient != null && this.webSocketClient.isOpen()) {
				try {
					this.webSocketClient.closeBlocking();
				} catch (InterruptedException e) {
					future.completeExceptionally(e);
				}
			}
			future.complete(null);
		});
		this.executorService.shutdown();
		return future;
	}

	public void addListener(ConcordEventListener listener) {
		this.webSocketClient.addListener(listener);
	}

	public <T> CompletableFuture<T> getJson(String url, Class<T> type) {
		var request = HttpRequest.newBuilder(URI.create(this.baseApiUrl + url))
				.GET()
				.header("Authorization", "Bearer " + this.token)
				.header("Accept", "application/json")
				.timeout(Duration.ofSeconds(3))
				.build();
		return sendJsonRequest(request, type);
	}

	public <T> CompletableFuture<T> postAnonJson(String url, Object body, Class<T> responseType) {
		var request = HttpRequest.newBuilder(URI.create(this.baseApiUrl + url))
				.POST(jsonPublisher(body))
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.build();
		return sendJsonRequest(request, responseType);
	}

	public <T> CompletableFuture<T> postJson(String url, Object body, Class<T> responseType) {
		var request = HttpRequest.newBuilder(URI.create(this.baseApiUrl + url))
				.POST(jsonPublisher(body))
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.header("Authorization", "Bearer " + this.token)
				.build();
		return sendJsonRequest(request, responseType);
	}

	private <T> CompletableFuture<T> sendJsonRequest(HttpRequest request, Class<T> type) {
		return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
				.thenApply(response -> {
					try {
						return mapper.readValue(response.body(), type);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				});
	}

	public CompletableFuture<Image> getImage(long id) {
		var request = HttpRequest.newBuilder(URI.create(this.baseApiUrl + "/images/" + id))
				.GET()
				.header("Authorization", "Bearer " + this.token)
				.header("Accept", "*/*")
				.build();
		return this.httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
				.thenApplyAsync(resp -> new Image(resp.body()));
	}

	public CompletableFuture<Server> getServer() {
		return getJson("/server", ServerData.class)
				.thenApply(data -> new Server(this, data.name(), data.description(), data.iconId(), data.defaultChannelId()));
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
