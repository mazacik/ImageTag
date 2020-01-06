package ui.node;

import javafx.scene.layout.Region;
import ui.decorator.Decorator;

public class NodeBoxSeparator extends Region {
	public NodeBoxSeparator() {
		super();
		this.setBorder(Decorator.getBorder(1, 1, 0, 0));
	}
}
