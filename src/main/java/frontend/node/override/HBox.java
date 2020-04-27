package frontend.node.override;

import frontend.decorator.DecoratorUtil;
import javafx.application.Platform;
import javafx.scene.Node;

public class HBox extends javafx.scene.layout.HBox {
	public HBox() {
		super();
		Platform.runLater(() -> DecoratorUtil.getNodeList().add(this));
	}
	public HBox(Node... children) {
		super(children);
		Platform.runLater(() -> DecoratorUtil.getNodeList().add(this));
	}
}
