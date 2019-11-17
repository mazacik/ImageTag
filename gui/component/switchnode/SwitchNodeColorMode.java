package gui.component.switchnode;

import gui.decorator.ColorPreset;
import gui.decorator.ColorUtil;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import tools.NodeUtil;

//todo rework as a dropdown menu
public class SwitchNodeColorMode extends SwitchNode {
	public SwitchNodeColorMode() {
		node1.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			if (ColorUtil.getColorPreset().equals(ColorPreset.PRESET_DARK)) {
				ColorUtil.setColorPreset(ColorPreset.PRESET_BRIGHT);
				node1.setBackground(ColorUtil.getBackgroundSecondary());
				node1.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
				node1.setGraphic(new ImageView(getClass().getResource("/icon-sun-1.png").toExternalForm()));
				node2.setGraphic(new ImageView(getClass().getResource("/icon-moon-1.png").toExternalForm()));
				node2.setBorder(null);
			}
		});
		node2.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			if (ColorUtil.getColorPreset().equals(ColorPreset.PRESET_BRIGHT)) {
				ColorUtil.setColorPreset(ColorPreset.PRESET_DARK);
				node1.setBorder(null);
				node1.setGraphic(new ImageView(getClass().getResource("/icon-sun-2.png").toExternalForm()));
				node2.setGraphic(new ImageView(getClass().getResource("/icon-moon-2.png").toExternalForm()));
				node2.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
				node2.setBackground(ColorUtil.getBackgroundSecondary());
			}
		});
		
		if (ColorUtil.getColorPreset().equals(ColorPreset.PRESET_BRIGHT)) {
			node1.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
			node2.setBorder(null);
			
			node1.setGraphic(new ImageView(getClass().getResource("/icon-sun-1.png").toExternalForm()));
			node2.setGraphic(new ImageView(getClass().getResource("/icon-moon-1.png").toExternalForm()));
		} else {
			node1.setBorder(null);
			node2.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
			
			node1.setGraphic(new ImageView(getClass().getResource("/icon-sun-2.png").toExternalForm()));
			node2.setGraphic(new ImageView(getClass().getResource("/icon-moon-2.png").toExternalForm()));
		}
	}
}
