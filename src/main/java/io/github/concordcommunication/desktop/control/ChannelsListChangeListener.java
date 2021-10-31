package io.github.concordcommunication.desktop.control;

import io.github.concordcommunication.desktop.model.Channel;
import io.github.concordcommunication.desktop.view.ChannelTreeItem;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TreeItem;

public class ChannelsListChangeListener implements ListChangeListener<Channel> {
	private final TreeItem<Channel> root;

	public ChannelsListChangeListener(TreeItem<Channel> root) {
		this.root = root;
	}

	@Override
	public void onChanged(Change<? extends Channel> c) {
		while (c.next()) {
			if (c.wasAdded()) {
				for (var channel : c.getAddedSubList()) {
					ChannelTreeItem item = new ChannelTreeItem();
					item.setValue(channel);
					channel.getChildren().addListener(new ChannelsListChangeListener(item));
					Platform.runLater(() -> root.getChildren().add(item));
				}
			}
			if (c.wasRemoved()) {
				for (var channel : c.getRemoved()) {
					int idx = root.getChildren().indexOf(channel);
					if (idx != -1) {
						var item = root.getChildren().get(idx);
						Platform.runLater(() -> root.getChildren().remove(item));
					}
				}
			}
		}
	}
}
