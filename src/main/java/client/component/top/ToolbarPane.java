package client.component.top;

import client.decorator.DecoratorUtil;
import client.node.SeparatorNode;
import client.node.TitleBar;
import client.node.menu.ClickMenu;
import client.node.menu.ListMenu;
import client.node.override.HBox;
import client.node.textnode.TextNode;
import client.node.textnode.TextNodeTemplates;
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
		
		this.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
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
