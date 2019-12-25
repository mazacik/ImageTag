package ui.component.simple;

import ui.decorator.ColorUtil;
import javafx.application.Platform;
import javafx.scene.Node;

public class VBox extends javafx.scene.layout.VBox {
	public VBox() {
		super();
		Platform.runLater(() -> ColorUtil.getNodeList().add(this));
	}
	public VBox(Node... children) {
		super(children);
		Platform.runLater(() -> ColorUtil.getNodeList().add(this));
	}
}
