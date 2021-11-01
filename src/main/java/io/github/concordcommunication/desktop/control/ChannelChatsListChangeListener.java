package io.github.concordcommunication.desktop.control;

import io.github.concordcommunication.desktop.model.Chat;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class ChannelChatsListChangeListener implements ListChangeListener<Chat> {
	private final VBox channelMessagesVBox;
	private final Map<Chat, Node> chatNodes = new HashMap<>();

	public ChannelChatsListChangeListener(VBox channelMessagesVBox) {
		this.channelMessagesVBox = channelMessagesVBox;
	}

	@Override
	public void onChanged(Change<? extends Chat> c) {
		while (c.next()) {
			if (c.wasAdded()) {
				for (var chat : c.getAddedSubList()) {
					var label = new Label(chat.getContent());
					chatNodes.put(chat, label);
					Platform.runLater(() -> channelMessagesVBox.getChildren().add(label));
				}
			}
			if (c.wasRemoved()) {
				for (var chat : c.getRemoved()) {
					Node node = chatNodes.get(chat);
					if (node != null) {
						Platform.runLater(() -> channelMessagesVBox.getChildren().remove(node));
					}
				}
			}
		}
	}
}
