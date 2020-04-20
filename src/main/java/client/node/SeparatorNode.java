package client.node;

import client.decorator.DecoratorUtil;
import javafx.scene.layout.Region;

public class SeparatorNode extends Region {
	public SeparatorNode() {
		super();
		this.setBorder(DecoratorUtil.getBorder(1, 1, 0, 0));
	}
}
