package io.github.concordcommunication.desktop.control.channel;

import io.github.concordcommunication.desktop.client.ConcordEventListener;
import io.github.concordcommunication.desktop.client.dto.websocket.ChatSent;
import io.github.concordcommunication.desktop.model.Channel;
import io.github.concordcommunication.desktop.model.Chat;
import javafx.application.Platform;

public class ChannelChatAppender implements ConcordEventListener {
	private final Channel channel;

	public ChannelChatAppender(Channel channel) {
		this.channel = channel;
	}

	@Override
	public void onChatSent(ChatSent event) {
		var c = event.chat();
		var chat = new Chat(channel, c.id(), c.createdAt(), c.authorId(), c.channelId(), c.threadId(), c.content(), c.edited());
		Platform.runLater(() -> channel.appendChat(chat));
	}
}
