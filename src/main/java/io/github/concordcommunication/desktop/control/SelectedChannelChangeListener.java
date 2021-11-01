package io.github.concordcommunication.desktop.control;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.concordcommunication.desktop.client.ConcordApi;
import io.github.concordcommunication.desktop.client.ConcordEventListener;
import io.github.concordcommunication.desktop.client.dto.api.ChatResponse;
import io.github.concordcommunication.desktop.client.dto.websocket.ChatSent;
import io.github.concordcommunication.desktop.model.Channel;
import io.github.concordcommunication.desktop.model.Chat;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public class SelectedChannelChangeListener implements ChangeListener<TreeItem<Channel>> {
	private final AnchorPane centerChannelViewPane;
	private final Map<Channel, Node> channelViewsMap;

	public SelectedChannelChangeListener(AnchorPane centerChannelViewPane) {
		this.centerChannelViewPane = centerChannelViewPane;
		this.channelViewsMap = new HashMap<>();
	}

	@Override
	public void changed(ObservableValue<? extends TreeItem<Channel>> observable, TreeItem<Channel> oldValue, TreeItem<Channel> newValue) {
		if (newValue == null) {
			Platform.runLater(() -> centerChannelViewPane.getChildren().clear());
		} else {
			var channel = newValue.getValue();
			Node channelView = this.channelViewsMap.computeIfAbsent(channel, this::createServerChannelView);
			Platform.runLater(() -> centerChannelViewPane.getChildren().setAll(channelView));
		}
	}

	private Node createServerChannelView(Channel channel) {
		FXMLLoader serverViewLoader = new FXMLLoader(getClass().getResource("/io/github/concordcommunication/desktop/server-view.fxml"));
		Node serverView;
		try {
			serverView = serverViewLoader.load();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		ServerViewController serverViewController = serverViewLoader.getController();
		serverViewController.channelNameLabel.textProperty().bind(channel.nameProperty());
		serverViewController.channelDescriptionLabel.textProperty().bind(channel.descriptionProperty());
		channel.getChats().addListener(new ChannelChatsListChangeListener(serverViewController.messagesVBox));
		channel.getServer().getConcordApi().addListener(new ConcordEventListener() {
			@Override
			public void onChatSent(ChatSent event) {
				var c = event.chat();
				channel.getChats().add(new Chat(c.id(), c.createdAt(), c.authorId(), c.channelId(), c.threadId(), c.content(), c.edited()));
			}
		});
		serverViewController.chatTextArea.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
			String text = serverViewController.chatTextArea.getText();
			if (!keyEvent.isShiftDown() && keyEvent.getCode().equals(KeyCode.ENTER) && !text.isBlank()) {
				String msg = text.trim();
				keyEvent.consume();
				System.out.println("Sending message: \"" + msg + "\"");
				serverViewController.chatTextArea.setText("");
				ObjectNode body = ConcordApi.mapper.createObjectNode().put("content", msg);
				channel.getServer().getConcordApi().postJson("/channels/" + channel.getId() + "/chats", body, ChatResponse.class)
						.thenAcceptAsync(c -> {
							channel.getChats().add(new Chat(c.id(), c.createdAt(), c.authorId(), c.channelId(), c.threadId(), c.content(), c.edited()));
						});
			}
		});
		AnchorPane.setTopAnchor(serverView, 0.0);
		AnchorPane.setBottomAnchor(serverView, 0.0);
		AnchorPane.setLeftAnchor(serverView, 0.0);
		AnchorPane.setRightAnchor(serverView, 0.0);
		return serverView;
	}
}
