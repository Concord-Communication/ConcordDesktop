package io.github.concordcommunication.desktop.view;

import io.github.concordcommunication.desktop.model.Chat;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.fxmisc.easybind.EasyBind;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * The element that is used to render a chat in a channel.
 */
public class ChatElement extends VBox implements Comparable<ChatElement> {
	private final Chat chat;
	private final Label authorLabel;
	private final Label timestampLabel;

	public ChatElement(Chat chat) {
		this.chat = chat;
		setFillWidth(true);
		setPrefWidth(200);
		this.authorLabel = new Label();
		this.timestampLabel = new Label();
		var b = EasyBind.map(chat.authorIdProperty(), authorId -> chat.getChannel().getServer().getUsers().get(authorId).getProfile().getNickname());
		authorLabel.textProperty().bind(b);
		String createdAt = Instant.ofEpochMilli(chat.getCreatedAt()).atZone(ZoneId.systemDefault())
				.format(DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm"));
		this.timestampLabel.setText(createdAt);
		var headerPane = new FlowPane(
				authorLabel,
				new Separator(Orientation.VERTICAL),
				timestampLabel,
				new Separator(Orientation.VERTICAL),
				new Label(Long.toString(chat.getId()))
		);
		headerPane.getStyleClass().add("chat-header");
		getChildren().add(headerPane);
		Label text = new Label();
		text.getStyleClass().add("chat-body");
		text.setWrapText(true);
		text.textProperty().bind(chat.contentProperty());
		getChildren().add(text);

		getStyleClass().add("chat");
	}

	public Chat getChat() {
		return chat;
	}

	@Override
	public int compareTo(ChatElement o) {
		return this.chat.compareTo(o.chat);
	}
}
