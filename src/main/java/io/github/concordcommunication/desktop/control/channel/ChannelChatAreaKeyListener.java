package io.github.concordcommunication.desktop.control.channel;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.concordcommunication.desktop.client.ConcordApi;
import io.github.concordcommunication.desktop.client.dto.api.ChatResponse;
import io.github.concordcommunication.desktop.model.Channel;
import io.github.concordcommunication.desktop.model.Chat;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ChannelChatAreaKeyListener implements EventHandler<KeyEvent> {
	private final Channel channel;
	private final TextArea chatTextArea;

	public ChannelChatAreaKeyListener(Channel channel, TextArea chatTextArea) {
		this.channel = channel;
		this.chatTextArea = chatTextArea;
	}

	@Override
	public void handle(KeyEvent event) {
		String text = chatTextArea.getText();
		if (!event.isShiftDown() && event.getCode().equals(KeyCode.ENTER) && !text.isBlank()) {
			String msg = text.trim();
			event.consume();
			chatTextArea.setText("");
			ObjectNode body = ConcordApi.mapper.createObjectNode().put("content", msg);
			channel.getServer().getConcordApi().postJson("/channels/" + channel.getId() + "/chats", body, ChatResponse.class)
					.thenAcceptAsync(c -> {
						var chat = new Chat(channel, c.id(), c.createdAt(), c.authorId(), c.channelId(), c.threadId(), c.content(), c.edited());
						Platform.runLater(() -> channel.appendChat(chat));
					});
		}
	}
}
