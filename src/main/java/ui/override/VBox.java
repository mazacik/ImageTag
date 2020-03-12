package ui.override;

import javafx.application.Platform;
import javafx.scene.Node;
import ui.decorator.Decorator;

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
