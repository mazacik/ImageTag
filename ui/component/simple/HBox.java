package ui.component.simple;

import ui.decorator.ColorUtil;
import javafx.application.Platform;
import javafx.scene.Node;

public class HBox extends javafx.scene.layout.HBox {
	public HBox() {
		super();
		Platform.runLater(() -> ColorUtil.getNodeList().add(this));
	}
	public HBox(Node... children) {
		super(children);
		Platform.runLater(() -> ColorUtil.getNodeList().add(this));
	}
}
