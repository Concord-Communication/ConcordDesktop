package io.github.concordcommunication.desktop.control;

import io.github.concordcommunication.desktop.model.Channel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SelectedChannelChangeListener implements ChangeListener<TreeItem<Channel>> {
	private final AnchorPane centerChannelViewPane;

	public SelectedChannelChangeListener(AnchorPane centerChannelViewPane) {
		this.centerChannelViewPane = centerChannelViewPane;
	}

	@Override
	public void changed(ObservableValue<? extends TreeItem<Channel>> observable, TreeItem<Channel> oldValue, TreeItem<Channel> newValue) {
		if (newValue == null) {
			Platform.runLater(() -> centerChannelViewPane.getChildren().clear());
		} else {
			Platform.runLater(() -> {
				var channel = newValue.getValue();
				FXMLLoader serverViewLoader = new FXMLLoader(getClass().getResource("/io/github/concordcommunication/desktop/server-view.fxml"));
				try {
					Node serverView = serverViewLoader.load();
					ServerViewController serverViewController = serverViewLoader.getController();
					serverViewController.channelNameLabel.textProperty().bind(channel.nameProperty());
					serverViewController.channelDescriptionLabel.textProperty().bind(channel.descriptionProperty());
					centerChannelViewPane.getChildren().clear();
					AnchorPane.setTopAnchor(serverView, 0.0);
					AnchorPane.setBottomAnchor(serverView, 0.0);
					AnchorPane.setLeftAnchor(serverView, 0.0);
					AnchorPane.setRightAnchor(serverView, 0.0);
					centerChannelViewPane.getChildren().add(serverView);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

	}
}
