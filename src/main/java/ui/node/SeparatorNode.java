package ui.node;

import javafx.scene.layout.Region;
import ui.decorator.Decorator;

public class SeparatorNode extends Region {
	public SeparatorNode() {
		super();
		this.setBorder(Decorator.getBorder(1, 1, 0, 0));
	}
}
