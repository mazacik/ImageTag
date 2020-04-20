package client.ui.override;

import client.ui.decorator.Decorator;
import javafx.application.Platform;
import javafx.scene.Node;

public class HBox extends javafx.scene.layout.HBox {
	public HBox() {
		super();
		Platform.runLater(() -> Decorator.getNodeList().addImpl(this));
	}
	public HBox(Node... children) {
		super(children);
		Platform.runLater(() -> Decorator.getNodeList().addImpl(this));
	}
}
