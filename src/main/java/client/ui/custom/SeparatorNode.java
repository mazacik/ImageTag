package client.ui.custom;

import client.ui.decorator.Decorator;
import javafx.scene.layout.Region;

public class SeparatorNode extends Region {
	public SeparatorNode() {
		super();
		this.setBorder(Decorator.getBorder(1, 1, 0, 0));
	}
}
