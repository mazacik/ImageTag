package frontend.node.override;

import frontend.decorator.DecoratorUtil;
import javafx.scene.Node;

public class VBox extends javafx.scene.layout.VBox {
	public VBox() {
		super();
		DecoratorUtil.getNodeList().add(this);
	}
	public VBox(Node... children) {
		super(children);
		DecoratorUtil.getNodeList().add(this);
	}
}
