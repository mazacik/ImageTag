package ui.node;

import javafx.scene.layout.Region;
import ui.decorator.Decorator;

public class BoxSeparator extends Region {
	public BoxSeparator() {
		super();
		this.setBorder(Decorator.getBorder(1, 1, 0, 0));
	}
}
