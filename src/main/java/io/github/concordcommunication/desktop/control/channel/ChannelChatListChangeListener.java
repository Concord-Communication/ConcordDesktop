package io.github.concordcommunication.desktop.control.channel;

import io.github.concordcommunication.desktop.model.ChannelChatListener;
import io.github.concordcommunication.desktop.model.Chat;
import io.github.concordcommunication.desktop.view.ChatElement;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelChatListChangeListener implements ChannelChatListener {
	private final ObservableList<Node> chatDisplayList;
	private final Map<Chat, ChatElement> chatNodes;

	public ChannelChatListChangeListener(ObservableList<Node> chatDisplayList) {
		this.chatDisplayList = chatDisplayList;
		this.chatNodes = new ConcurrentHashMap<>();
	}

	@Override
	public void chatsSet(List<Chat> chats) {
		Platform.runLater(() -> {
			chatDisplayList.clear();
			chatNodes.clear();
			chatsAppended(chats);
		});
	}

	@Override
	public void chatsAppended(List<Chat> chats) {
		Platform.runLater(() -> {
			var nodes = chats.stream().map(chat -> {
				var node = new ChatElement(chat);
				chatNodes.put(chat, node);
				return node;
			}).sorted().toList();
			chatDisplayList.addAll(nodes);
			FXCollections.sort(chatDisplayList, Comparator.comparingLong(n -> ((ChatElement) n).getChat().getCreatedAt()));
		});
	}

	@Override
	public void chatsPrepended(List<Chat> chats) {
		Platform.runLater(() -> {
			var nodes = chats.stream().map(chat -> {
				var node = new ChatElement(chat);
				chatNodes.put(chat, node);
				return node;
			}).toList();
			chatDisplayList.addAll(0, nodes);
			FXCollections.sort(chatDisplayList, Comparator.comparingLong(n -> ((ChatElement) n).getChat().getCreatedAt()));
		});
	}

	@Override
	public void chatsRemoved(List<Chat> chats) {
		Platform.runLater(() -> {
			var nodes = chats.stream()
					.map(chatNodes::remove)
					.filter(Objects::nonNull)
					.toList();
			chatDisplayList.removeAll(nodes);
		});
	}
}
