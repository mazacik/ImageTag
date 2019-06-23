package user_interface.nodes.node;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import user_interface.nodes.NodeUtil;
import user_interface.style.ColorUtil;

public class ColorModeNode extends SwitchNode {
	public ColorModeNode() {
		super("Default", "Dark", 140);
		
		node1.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				if (ColorUtil.isNightMode()) {
					ColorUtil.setNightMode(false);
					node1.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
					node2.setBorder(null);
				}
			}
		});
		node2.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				if (!ColorUtil.isNightMode()) {
					ColorUtil.setNightMode(true);
					node1.setBorder(null);
					node2.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
				}
			}
		});
		
		if (!ColorUtil.isNightMode()) {
			node1.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
			node2.setBorder(null);
		} else {
			node1.setBorder(null);
			node2.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		}
	}
}
