package gui.component;

import gui.component.clickmenu.ClickMenu;
import gui.component.simple.TextNode;
import gui.decorator.ColorPreset;
import gui.decorator.ColorUtil;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.InstanceCollector;
import tools.enums.Direction;

public class ColorPresetComboBox extends TextNode {
	private static final String TEXT_BASE = "Color Preset: ";
	
	public ColorPresetComboBox() {
		super(TEXT_BASE + ColorPreset.values()[InstanceCollector.settings.getColorPreset()].getDisplayName(), true, false, true, true);
		
		int size = ColorPreset.values().length;
		TextNode[] nodes = new TextNode[size];
		
		for (int i = 0; i < size; i++) {
			ColorPreset colorPreset = ColorPreset.values()[i];
			String colorPresetName = colorPreset.getDisplayName();
			
			nodes[i] = new TextNode(colorPresetName, true, false, true, true);
			nodes[i].addMouseEvent(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY, () -> {
				ColorUtil.setColorPreset(colorPreset);
				this.setText(TEXT_BASE + colorPresetName);
				ClickMenu.hideAll();
			});
		}
		
		ClickMenu.install(this, Direction.RIGHT, nodes);
	}
}
