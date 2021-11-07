package io.github.concordcommunication.desktop.control;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class ServerViewController {
	@FXML
	public Label channelNameLabel;
	@FXML
	public Label channelDescriptionLabel;
	@FXML
	public TextArea chatTextArea;
	@FXML
	public VBox messagesVBox;
	@FXML
	public ScrollPane chatScrollPane;
}
