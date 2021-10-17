module io.github.concordcommunication.desktop {
	requires javafx.controls;
	requires javafx.fxml;

	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;
	requires org.kordamp.bootstrapfx.core;

	opens io.github.concordcommunication.desktop to javafx.fxml;
	exports io.github.concordcommunication.desktop;
}