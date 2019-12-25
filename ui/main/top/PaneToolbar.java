package ui.main.top;

import base.CustomList;
import base.entity.Entity;
import control.Target;
import ui.component.clickmenu.ClickMenu;
import ui.component.simple.BoxSeparatorNode;
import ui.component.simple.HBox;
import ui.component.simple.TextNode;
import ui.component.simple.template.ButtonTemplates;
import ui.decorator.SizeUtil;
import ui.stage.TitleBar;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import enums.Direction;

import java.util.logging.Logger;

public class PaneToolbar extends TitleBar {
	private TextNode nodeTarget;
	
	public void init() {
		super.init("");
		
		TextNode nodeFile = new TextNode("File", true, true, false, true);
		TextNode nodeSave = ButtonTemplates.APPLICATION_SAVE.get();
		TextNode nodeImport = ButtonTemplates.APPLICATION_IMPORT.get();
		TextNode nodeCacheReset = ButtonTemplates.CACHE_RESET.get();
		TextNode nodeExit = ButtonTemplates.APPLICATION_EXIT.get();
		ClickMenu.install(nodeFile, Direction.DOWN, nodeSave, nodeImport, nodeCacheReset, new BoxSeparatorNode(), nodeExit);
		
		TextNode nodeRandom = ButtonTemplates.FILTER_RANDOM.get();
		HBox hBoxTools = new HBox(nodeRandom);
		hBoxTools.setAlignment(Pos.CENTER);
		
		nodeTarget = new TextNode("", true, true, false, true);
		ClickMenu.install(nodeTarget, Direction.DOWN, MouseButton.PRIMARY, ClickMenu.StaticInstance.ENTITY);
		
		HBox hBox = new HBox(nodeFile, hBoxTools);
		hBox.setAlignment(Pos.CENTER);
		
		this.setPrefHeight(SizeUtil.getPrefHeightTopMenu());
		this.setLeft(hBox);
		this.setCenter(nodeTarget);
	}
	
	public boolean reload() {
		Logger.getGlobal().info(this.toString());
		
		Entity currentTarget = Target.get();
		if (currentTarget.getCollectionID() != 0) {
			CustomList<Entity> collection = currentTarget.getCollection();
			String collectionIndex = (collection.indexOf(currentTarget) + 1) + "/" + collection.size();
			nodeTarget.setText("[" + collectionIndex + "] " + currentTarget.getName());
		} else {
			nodeTarget.setText(currentTarget.getName());
		}
		
		return true;
	}
	
	private PaneToolbar() {}
	private static class Loader {
		private static final PaneToolbar INSTANCE = new PaneToolbar();
	}
	public static PaneToolbar get() {
		return Loader.INSTANCE;
	}
}
