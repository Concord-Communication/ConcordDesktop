module io.github.concordcommunication.desktop {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.net.http;

	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;
	requires org.kordamp.bootstrapfx.core;
	requires Java.WebSocket;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.databind;

	opens io.github.concordcommunication.desktop to javafx.fxml;
	exports io.github.concordcommunication.desktop;
}