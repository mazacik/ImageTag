package ui.main.top;

import base.CustomList;
import base.entity.Entity;
import control.reload.Reload;
import enums.Direction;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import main.Root;
import ui.custom.ClickMenu;
import ui.custom.ListMenu;
import ui.custom.TitleBar;
import ui.decorator.Decorator;
import ui.node.SeparatorNode;
import ui.node.textnode.TextNode;
import ui.node.textnode.TextNodeTemplates;
import ui.override.HBox;

public class ToolbarPane extends BorderPane {
	private TextNode nodeTarget;
	
	public ToolbarPane() {
		TitleBar titleBar = new TitleBar();
		
		TextNode nodeFile = new TextNode("File", true, true, false, true);
		ClickMenu.install(nodeFile, Direction.DOWN, MouseButton.PRIMARY,
		                  TextNodeTemplates.APPLICATION_SAVE.get(),
		                  TextNodeTemplates.APPLICATION_IMPORT.get(),
		                  TextNodeTemplates.APPLICATION_REFRESH.get(),
		                  TextNodeTemplates.APPLICATION_SETTINGS.get(),
		                  TextNodeTemplates.CACHE_RESET.get(),
		                  new SeparatorNode(),
		                  TextNodeTemplates.APPLICATION_SAVE_AND_CLOSE.get(),
		                  TextNodeTemplates.APPLICATION_EXIT.get()
		);
		
		TextNode nodeRandom = new TextNode("Random", true, true, false, true);
		nodeRandom.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Root.SELECT.setRandom();
			Reload.start();
		});
		
		nodeTarget = new TextNode("", true, true, false, true);
		ClickMenu.install(nodeTarget, Direction.DOWN, MouseButton.PRIMARY, ListMenu.Preset.ENTITY);
		
		HBox hBox = new HBox(nodeFile, nodeRandom);
		hBox.setAlignment(Pos.CENTER);
		
		titleBar.setCenter(nodeTarget);
		titleBar.setBorder(null);
		
		this.setBorder(Decorator.getBorder(0, 0, 1, 0));
		this.setLeft(hBox);
		this.setCenter(titleBar);
	}
	
	public boolean reload() {
		Entity target = Root.SELECT.getTarget();
		if (target != null) {
			if (target.hasCollection()) {
				CustomList<Entity> collection = target.getCollection();
				String collectionIndex = (collection.indexOf(target) + 1) + "/" + collection.size();
				nodeTarget.setText("[" + collectionIndex + "] " + target.getName());
			} else {
				nodeTarget.setText(target.getName());
			}
		}
		
		return true;
	}
}
