package client.ui.override;

import client.ui.decorator.Decorator;
import javafx.application.Platform;
import javafx.scene.Node;

public class VBox extends javafx.scene.layout.VBox {
	public VBox() {
		super();
		Platform.runLater(() -> Decorator.getNodeList().addImpl(this));
	}
	public VBox(Node... children) {
		super(children);
		Platform.runLater(() -> Decorator.getNodeList().addImpl(this));
	}
}
