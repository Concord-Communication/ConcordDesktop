package io.github.concordcommunication.desktop.client.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.concordcommunication.desktop.client.ConcordEventListener;
import io.github.concordcommunication.desktop.client.dto.websocket.ChatSent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static io.github.concordcommunication.desktop.client.ConcordApi.mapper;

public class ConcordWebsocketClient extends WebSocketClient {
	private final List<WeakReference<ConcordEventListener>> eventListeners = new CopyOnWriteArrayList<>();

	public ConcordWebsocketClient(URI serverUri) {
		super(serverUri);
	}

	public void addListener(ConcordEventListener listener) {
		this.eventListeners.add(new WeakReference<>(listener));
	}

	private void forEach(Consumer<ConcordEventListener> c) {
		this.eventListeners.forEach(ref -> {
			var listener = ref.get();
			if (listener != null) c.accept(listener);
		});
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		forEach(ConcordEventListener::onConnect);
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
					forEach(l -> l.onChatSent(msg));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		forEach(ConcordEventListener::onDisconnect);
	}

	@Override
	public void onError(Exception ex) {

	}
}
