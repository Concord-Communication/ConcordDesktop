package io.github.concordcommunication.desktop.client.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.concordcommunication.desktop.client.ConcordEventListener;
import io.github.concordcommunication.desktop.client.dto.websocket.ChatSent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.github.concordcommunication.desktop.client.ConcordClient.mapper;

public class ConcordWebsocketClient extends WebSocketClient {
	private final List<ConcordEventListener> eventListeners = new CopyOnWriteArrayList<>();

	public ConcordWebsocketClient(URI serverUri) {
		super(serverUri);
	}

	public void addListener(ConcordEventListener listener) {
		this.eventListeners.add(listener);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		eventListeners.forEach(ConcordEventListener::onConnect);
	}

	@Override
	public void onMessage(String message) {
		try {
			var node = mapper.readValue(message, ObjectNode.class);
			String type = node.get("type").asText();
			System.out.println("Got message: " + type);
			switch (type) {
				case "chat_sent" -> {
					var msg = mapper.treeToValue(node, ChatSent.class);
					eventListeners.forEach(l -> l.onChatSent(msg));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		eventListeners.forEach(ConcordEventListener::onDisconnect);
	}

	@Override
	public void onError(Exception ex) {

	}
}
