package io.github.concordcommunication.desktop.view;

import io.github.concordcommunication.desktop.model.Chat;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.fxmisc.easybind.EasyBind;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ChatElement extends VBox {
	public ChatElement(Chat chat) {
		setFillWidth(true);
		setPrefWidth(300);
		String createdAt = Instant.ofEpochMilli(chat.getCreatedAt()).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		var authorLabel = new Label();
		var b = EasyBind.map(chat.authorIdProperty(), authorId -> chat.getChannel().getServer().getUsers().get(authorId).getProfile().getNickname());
		authorLabel.textProperty().bind(b);
		getChildren().add(new FlowPane(
				authorLabel,
				new Separator(Orientation.VERTICAL),
				new Label(createdAt)
		));
		Text text = new Text();
		text.textProperty().bind(chat.contentProperty());
		TextFlow textFlow = new TextFlow(text);
		getChildren().add(textFlow);
	}
}
