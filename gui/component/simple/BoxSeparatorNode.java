package gui.component.simple;

import javafx.scene.layout.Region;
import tools.NodeUtil;

public class BoxSeparatorNode extends Region {
	public BoxSeparatorNode() {
		super();
		this.setBorder(NodeUtil.getBorder(1, 1, 0, 0));
	}
}
