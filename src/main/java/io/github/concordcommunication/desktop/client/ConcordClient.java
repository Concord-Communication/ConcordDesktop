package io.github.concordcommunication.desktop.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ConcordClient {
	public static final ObjectMapper mapper = new ObjectMapper();

	private final HttpClient httpClient = HttpClient.newHttpClient();
	private WebSocketClient webSocketClient;
	private String address;
	private String username;
	private String password;

	private String token;

	public ConcordClient(String address, String username, String password) {
		var tokenRequest = HttpRequest.newBuilder(URI.create("http://" + address + "/api/tokens"))
				.POST(jsonPublisher(mapper.createObjectNode()
						.put("username", username)
						.put("password", password)))
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.build();
		requestJson(tokenRequest, ObjectNode.class)
				.thenAcceptAsync(node -> {
					this.token = node.get("token").asText();
					System.out.println("Got token: " + this.token);
					this.webSocketClient = new WebSocketClient(URI.create("ws://" + address + "/client?token=" + this.token)) {
						@Override
						public void onOpen(ServerHandshake serverHandshake) {
							System.out.println("Websocket opened to " + getURI());
						}

						@Override
						public void onMessage(String s) {
							System.out.println("Received message: " + s);
						}

						@Override
						public void onClose(int i, String s, boolean b) {
							System.out.println("Websocket closed.");
						}

						@Override
						public void onError(Exception e) {
							e.printStackTrace();
						}
					};
					this.webSocketClient.connect();
				})
				.join();
	}

	public static HttpRequest.BodyPublisher jsonPublisher(Object obj) {
		try {
			byte[] bytes = mapper.writeValueAsBytes(obj);
			return HttpRequest.BodyPublishers.ofByteArray(bytes);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
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
}
