module io.github.concordcommunication.desktop {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.net.http;

	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;
	requires org.kordamp.bootstrapfx.core;
	requires easybind;
	requires Java.WebSocket;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.databind;

	opens io.github.concordcommunication.desktop to javafx.fxml;
	exports io.github.concordcommunication.desktop;
	exports io.github.concordcommunication.desktop.control;
	exports io.github.concordcommunication.desktop.model;
	opens io.github.concordcommunication.desktop.control to javafx.fxml;
	opens io.github.concordcommunication.desktop.client.dto.websocket to com.fasterxml.jackson.databind;
	opens io.github.concordcommunication.desktop.client.dto.api to com.fasterxml.jackson.databind;
	opens io.github.concordcommunication.desktop.client to com.fasterxml.jackson.databind;
}