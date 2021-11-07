package io.github.concordcommunication.desktop.control;

import io.github.concordcommunication.desktop.control.channel.ChannelChatAppender;
import io.github.concordcommunication.desktop.control.channel.ChannelChatAreaKeyListener;
import io.github.concordcommunication.desktop.control.channel.ChannelChatListChangeListener;
import io.github.concordcommunication.desktop.model.Channel;
import io.github.concordcommunication.desktop.view.ChatElement;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
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
		var scrollPane = serverViewController.chatScrollPane;

		serverViewController.channelNameLabel.textProperty().bind(channel.nameProperty());
		serverViewController.channelDescriptionLabel.textProperty().bind(channel.descriptionProperty());
		channel.getChats().addListener(new ChannelChatListChangeListener(serverViewController.messagesVBox.getChildren(), scrollPane));
		// Weird trick to clamp scrollpane to bottom when user scrolls to bottom.
		InvalidationListener heightListener = observable -> scrollPane.setVvalue(1.0);
		scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue.doubleValue() < 1.0 && newValue.doubleValue() == 1.0) {
				serverViewController.messagesVBox.heightProperty().addListener(heightListener);
			} else if (oldValue.doubleValue() == 1.0 && newValue.doubleValue() < 1.0) {
				serverViewController.messagesVBox.heightProperty().removeListener(heightListener);
			}
		});
		channel.getServer().getConcordApi().addListener(new ChannelChatAppender(channel));
		serverViewController.chatTextArea.addEventHandler(KeyEvent.KEY_PRESSED, new ChannelChatAreaKeyListener(channel, serverViewController.chatTextArea));
		// init with current chats
		Platform.runLater(() -> {
			var nodes = channel.getChats().stream().map(ChatElement::new).toList();
			serverViewController.messagesVBox.getChildren().addAll(nodes);
		});

		AnchorPane.setTopAnchor(serverView, 0.0);
		AnchorPane.setBottomAnchor(serverView, 0.0);
		AnchorPane.setLeftAnchor(serverView, 0.0);
		AnchorPane.setRightAnchor(serverView, 0.0);
		return serverView;
	}


}
