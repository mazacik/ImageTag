package application.gui.component.switchnode;

import application.gui.decorator.ColorUtil;
import application.tools.NodeUtil;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class SwitchNodeColorMode extends SwitchNode {
	public SwitchNodeColorMode() {
		node1.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			if (ColorUtil.isNightMode()) {
				ColorUtil.setNightMode(false);
				node1.setBackground(ColorUtil.getBackgroundAlt());
				node1.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
				node1.setGraphic(new ImageView("application/gui/resource/icon-sun-1.png"));
				node2.setGraphic(new ImageView("application/gui/resource/icon-moon-1.png"));
				node2.setBorder(null);
			}
		});
		node2.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			if (!ColorUtil.isNightMode()) {
				ColorUtil.setNightMode(true);
				node1.setBorder(null);
				node1.setGraphic(new ImageView("application/gui/resource/icon-sun-2.png"));
				node2.setGraphic(new ImageView("application/gui/resource/icon-moon-2.png"));
				node2.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
				node2.setBackground(ColorUtil.getBackgroundAlt());
			}
		});
		
		if (!ColorUtil.isNightMode()) {
			node1.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
			node2.setBorder(null);
			
			node1.setGraphic(new ImageView("application/gui/resource/icon-sun-1.png"));
			node2.setGraphic(new ImageView("application/gui/resource/icon-moon-1.png"));
		} else {
			node1.setBorder(null);
			node2.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
			
			node1.setGraphic(new ImageView("application/gui/resource/icon-sun-2.png"));
			node2.setGraphic(new ImageView("application/gui/resource/icon-moon-2.png"));
		}
	}
}
