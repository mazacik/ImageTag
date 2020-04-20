package client.ui.main.top;

import client.ui.custom.ClickMenu;
import client.ui.custom.ListMenu;
import client.ui.custom.SeparatorNode;
import client.ui.custom.TitleBar;
import client.ui.custom.textnode.TextNode;
import client.ui.custom.textnode.TextNodeTemplates;
import client.ui.decorator.Decorator;
import client.ui.override.HBox;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import main.Root;
import server.base.CustomList;
import server.base.entity.Entity;
import server.control.reload.Reload;
import server.enums.Direction;

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
