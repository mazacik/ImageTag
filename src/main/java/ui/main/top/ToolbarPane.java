package ui.main.top;

import base.CustomList;
import base.entity.Entity;
import control.Select;
import control.reload.Notifier;
import control.reload.Reload;
import enums.Direction;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import ui.custom.ClickMenu;
import ui.custom.TitleBar;
import ui.decorator.Decorator;
import ui.main.gallery.GalleryPane;
import ui.main.stage.MainStage;
import ui.node.NodeTemplates;
import ui.node.SeparatorNode;
import ui.node.TextNode;
import ui.override.HBox;

public class ToolbarPane extends BorderPane {
	public static final double PREF_HEIGHT = 30;
	
	private TextNode nodeTarget;
	
	public boolean reload() {
		Entity currentTarget = Select.getTarget();
		if (currentTarget.getCollectionID() != 0) {
			CustomList<Entity> collection = currentTarget.getCollection();
			String collectionIndex = (collection.indexOf(currentTarget) + 1) + "/" + collection.size();
			nodeTarget.setText("[" + collectionIndex + "] " + currentTarget.getName());
		} else {
			nodeTarget.setText(currentTarget.getName());
		}
		
		return true;
	}
	
	private ToolbarPane() {
		TitleBar titleBar = new TitleBar(MainStage.getInstance());
		
		TextNode nodeFile = new TextNode("File", true, true, false, true);
		TextNode nodeSave = NodeTemplates.APPLICATION_SAVE.get();
		TextNode nodeImport = NodeTemplates.APPLICATION_IMPORT.get();
		TextNode nodeSettings = NodeTemplates.APPLICATION_SETTINGS.get();
		TextNode nodeCacheReset = NodeTemplates.CACHE_RESET.get();
		TextNode nodeExit = NodeTemplates.APPLICATION_EXIT.get();
		ClickMenu.install(nodeFile, Direction.DOWN, MouseButton.PRIMARY, nodeSave, nodeImport, nodeSettings, nodeCacheReset, new SeparatorNode(), nodeExit);
		
		TextNode nodeRandom = new TextNode("Random", true, true, false, true);
		nodeRandom.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			Reload.notify(Notifier.FILTER_NEEDS_REFRESH);
			Entity randomEntity = GalleryPane.getTileEntities().getRandom();
			Select.getEntities().set(randomEntity);
			Select.setTarget(randomEntity);
			Reload.start();
		});
		
		nodeTarget = new TextNode("", true, true, false, true);
		ClickMenu.install(nodeTarget, Direction.DOWN, MouseButton.PRIMARY
				, NodeTemplates.FILE.get()
				, NodeTemplates.SELECTION.get()
		);
		
		HBox hBox = new HBox(nodeFile, nodeRandom);
		hBox.setAlignment(Pos.CENTER);
		
		titleBar.setCenter(nodeTarget);
		titleBar.setBorder(null);
		
		this.setBorder(Decorator.getBorder(0, 0, 1, 0));
		this.setPrefHeight(PREF_HEIGHT);
		this.setLeft(hBox);
		this.setCenter(titleBar);
	}
	private static class Loader {
		private static final ToolbarPane INSTANCE = new ToolbarPane();
	}
	public static ToolbarPane getInstance() {
		return Loader.INSTANCE;
	}
}
