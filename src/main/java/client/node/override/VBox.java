package client.node.override;

import client.decorator.DecoratorUtil;
import javafx.application.Platform;
import javafx.scene.Node;

public class VBox extends javafx.scene.layout.VBox {
	public VBox() {
		super();
		Platform.runLater(() -> DecoratorUtil.getNodeList().addImpl(this));
	}
	public VBox(Node... children) {
		super(children);
		Platform.runLater(() -> DecoratorUtil.getNodeList().addImpl(this));
	}
}
