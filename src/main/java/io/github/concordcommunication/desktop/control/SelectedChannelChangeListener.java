package io.github.concordcommunication.desktop.control;

import io.github.concordcommunication.desktop.control.channel.ChannelChatAppender;
import io.github.concordcommunication.desktop.control.channel.ChannelChatAreaKeyListener;
import io.github.concordcommunication.desktop.control.channel.ChannelChatListChangeListener;
import io.github.concordcommunication.desktop.model.Channel;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
		var messagesBox = serverViewController.messagesVBox;

		serverViewController.channelNameLabel.textProperty().bind(channel.nameProperty());
		serverViewController.channelDescriptionLabel.textProperty().bind(channel.descriptionProperty());
		channel.addChatListener(new ChannelChatListChangeListener(messagesBox.getChildren()));
		channel.fetchLatest();

		// Weird trick to clamp scrollpane to bottom when user scrolls to bottom.
		AtomicBoolean scrollClamp = new AtomicBoolean(true);
		AtomicReference<Double> lastHeight = new AtomicReference<>(messagesBox.getHeight());
		InvalidationListener heightListener = observable -> {
			if (scrollClamp.get()) scrollPane.setVvalue(1.0);
		};
		scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
				if (oldValue.doubleValue() < 1.0 && newValue.doubleValue() == 1.0) {
					scrollClamp.set(true);
					lastHeight.set(messagesBox.getHeight());
				} else if (oldValue.doubleValue() == 1.0 && newValue.doubleValue() < 1.0 &&
						lastHeight.get().equals(messagesBox.getHeight())) {
					scrollClamp.set(false);
				} else if (oldValue.doubleValue() > 0 && newValue.doubleValue() == 0) {
					// load more chats.
					channel.fetchBackward();
				}
		});
		messagesBox.heightProperty().addListener(heightListener);

		channel.getServer().getConcordApi().addListener(new ChannelChatAppender(channel));
		serverViewController.chatTextArea.addEventHandler(KeyEvent.KEY_PRESSED, new ChannelChatAreaKeyListener(channel, serverViewController.chatTextArea));

		AnchorPane.setTopAnchor(serverView, 0.0);
		AnchorPane.setBottomAnchor(serverView, 0.0);
		AnchorPane.setLeftAnchor(serverView, 0.0);
		AnchorPane.setRightAnchor(serverView, 0.0);
		return serverView;
	}


}
