package application.gui.component.simple;

import application.tools.NodeUtil;
import javafx.scene.layout.Region;

public class BoxSeparatorNode extends Region {
	public BoxSeparatorNode() {
		super();
		this.setBorder(NodeUtil.getBorder(1, 1, 0, 0));
	}
}
