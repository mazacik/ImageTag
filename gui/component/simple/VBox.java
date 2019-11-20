package gui.component.simple;

import gui.decorator.ColorUtil;
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
