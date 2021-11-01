package io.github.concordcommunication.desktop.control;

import io.github.concordcommunication.desktop.model.Channel;
import io.github.concordcommunication.desktop.model.Server;
import io.github.concordcommunication.desktop.view.ChannelTreeItem;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class ServersListChangeListener implements ListChangeListener<Server> {
	private final Accordion serversAccordion;
	private final AnchorPane centerChannelViewPane;
	private final Map<Server, TitledPane> serverPanesMap;

	public ServersListChangeListener(Accordion serversAccordion, AnchorPane centerChannelViewPane) {
		this.serversAccordion = serversAccordion;
		this.centerChannelViewPane = centerChannelViewPane;
		this.serverPanesMap = new HashMap<>();
	}

	@Override
	public void onChanged(Change<? extends Server> c) {
		while (c.next()) {
			if (c.wasAdded()) {
				for (var server : c.getAddedSubList()) {
					var pane = buildServerPane(server);
					serverPanesMap.put(server, pane);
					Platform.runLater(() -> serversAccordion.getPanes().add(pane));
				}
			}
			if (c.wasRemoved()) {
				for (var server : c.getRemoved()) {
					var pane = serverPanesMap.get(server);
					if (pane != null) {
						Platform.runLater(() -> serversAccordion.getPanes().remove(pane));
					}
				}
			}
		}
	}

	private TitledPane buildServerPane(Server server) {
		TreeView<Channel> channelsView = new TreeView<>(new ChannelTreeItem());
		channelsView.setShowRoot(false);
		channelsView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		channelsView.getSelectionModel().selectedItemProperty().addListener(new SelectedChannelChangeListener(centerChannelViewPane));
		server.getChannels().addListener(new ChannelsListChangeListener(channelsView.getRoot()));

		VBox serverVBox = new VBox();
		ImageView serverIconView = new ImageView();
		serverIconView.setFitHeight(30.0);
		serverIconView.setFitWidth(30.0);
		serverIconView.setSmooth(true);
		serverIconView.setPreserveRatio(true);
		serverIconView.imageProperty().bind(server.iconProperty());
		serverVBox.getChildren().addAll(channelsView);

		var serverChannelsPane = new TitledPane("Server Name", serverVBox);
		serverChannelsPane.setAlignment(Pos.TOP_LEFT);
		serverChannelsPane.setAnimated(false);
		serverChannelsPane.textProperty().bind(server.nameProperty());
		serverChannelsPane.graphicProperty().setValue(serverIconView);
		return serverChannelsPane;
	}
}
