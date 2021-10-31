package io.github.concordcommunication.desktop.control;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class ServerViewController {
	@FXML
	public Label channelNameLabel;
	@FXML
	public Label channelDescriptionLabel;
	@FXML
	public VBox channelMessagesVBox;
	@FXML
	public TextArea chatTextArea;


	public void onChatKeyPressed(KeyEvent keyEvent) {
		String text = chatTextArea.getText();
		if (!keyEvent.isShiftDown() && keyEvent.getCode().equals(KeyCode.ENTER) && !text.isBlank()) {
			String msg = text.trim();
			System.out.println("Sending message: \"" + msg + "\"");
			chatTextArea.setText("");
		}
	}
}
