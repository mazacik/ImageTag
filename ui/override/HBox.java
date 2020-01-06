package ui.override;

import javafx.application.Platform;
import javafx.scene.Node;
import ui.decorator.Decorator;

public class HBox extends javafx.scene.layout.HBox {
	public HBox() {
		super();
		Platform.runLater(() -> Decorator.getNodeList().add(this));
	}
	public HBox(Node... children) {
		super(children);
		Platform.runLater(() -> Decorator.getNodeList().add(this));
	}
}
