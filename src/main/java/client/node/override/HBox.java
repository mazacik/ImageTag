package client.node.override;

import client.decorator.DecoratorUtil;
import javafx.application.Platform;
import javafx.scene.Node;

public class HBox extends javafx.scene.layout.HBox {
	public HBox() {
		super();
		Platform.runLater(() -> DecoratorUtil.getNodeList().addImpl(this));
	}
	public HBox(Node... children) {
		super(children);
		Platform.runLater(() -> DecoratorUtil.getNodeList().addImpl(this));
	}
}
