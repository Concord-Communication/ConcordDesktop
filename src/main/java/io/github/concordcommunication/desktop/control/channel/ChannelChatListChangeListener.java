package io.github.concordcommunication.desktop.control.channel;

import io.github.concordcommunication.desktop.model.Chat;
import io.github.concordcommunication.desktop.view.ChatElement;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

import java.util.HashMap;
import java.util.Map;

public class ChannelChatListChangeListener implements ListChangeListener<Chat> {
	private final ObservableList<Node> chatDisplayList;
	private final Map<Chat, Node> chatNodes;
	private final ScrollPane chatScrollPane;

	public ChannelChatListChangeListener(ObservableList<Node> chatDisplayList, ScrollPane chatScrollPane) {
		this.chatDisplayList = chatDisplayList;
		this.chatScrollPane = chatScrollPane;
		this.chatNodes = new HashMap<>();
	}

	@Override
	public void onChanged(Change<? extends Chat> c) {
		while (c.next()) {
			if (c.wasAdded()) {
				for (var chat : c.getAddedSubList()) {
					var node = new ChatElement(chat);
					this.chatNodes.put(chat, node);
					this.chatDisplayList.add(node);
				}
			}
			if (c.wasRemoved()) {
				for (var chat : c.getRemoved()) {
					var node = this.chatNodes.remove(chat);
					if (node != null) this.chatDisplayList.remove(node);
				}
			}
		}
	}
}
