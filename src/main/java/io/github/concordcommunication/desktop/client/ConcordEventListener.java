package io.github.concordcommunication.desktop.client;

import io.github.concordcommunication.desktop.client.dto.websocket.ChatReaction;
import io.github.concordcommunication.desktop.client.dto.websocket.ChatSent;
import io.github.concordcommunication.desktop.client.dto.websocket.ChatTyping;

/**
 * Main interface for listening to events that are sent from the Concord web
 * server. Only implement the methods you need to.
 */
public interface ConcordEventListener {
	default void onConnect() {}
	default void onDisconnect() {}

	// Chat messages
	default void onChatTyping(ChatTyping event) {}
	default void onChatReaction(ChatReaction event) {}
	default void onChatSent(ChatSent event) {}
}
