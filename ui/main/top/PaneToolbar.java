package ui.main.top;

import base.CustomList;
import base.entity.Entity;
import control.Select;
import enums.Direction;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import ui.NodeUtil;
import ui.component.clickmenu.ClickMenu;
import ui.component.simple.BoxSeparatorNode;
import ui.component.simple.HBox;
import ui.component.simple.TextNode;
import ui.component.simple.template.ButtonTemplates;
import ui.decorator.SizeUtil;
import ui.stage.StageManager;
import ui.stage.TitleBar;

import java.util.logging.Logger;

public class PaneToolbar extends BorderPane {
	private TitleBar titleBar;
	private TextNode nodeTarget;
	
	public void init() {
		titleBar = new TitleBar(StageManager.getStageMain());
		
		TextNode nodeFile = new TextNode("File", true, true, false, true);
		TextNode nodeSave = ButtonTemplates.APPLICATION_SAVE.get();
		TextNode nodeImport = ButtonTemplates.APPLICATION_IMPORT.get();
		TextNode nodeCacheReset = ButtonTemplates.CACHE_RESET.get();
		TextNode nodeExit = ButtonTemplates.APPLICATION_EXIT.get();
		ClickMenu.install(nodeFile, Direction.DOWN, nodeSave, nodeImport, nodeCacheReset, new BoxSeparatorNode(), nodeExit);
		
		nodeTarget = new TextNode("", true, true, false, true);
		ClickMenu.install(nodeTarget, Direction.DOWN, MouseButton.PRIMARY, ClickMenu.StaticInstance.ENTITY);
		
		HBox hBox = new HBox(nodeFile);
		hBox.setAlignment(Pos.CENTER);
		
		titleBar.setCenter(nodeTarget);
		titleBar.setBorder(null);
		
		this.setBorder(NodeUtil.getBorder(0, 0, 1, 0));
		this.setPrefHeight(SizeUtil.getPrefHeightTopMenu());
		this.setLeft(hBox);
		this.setCenter(titleBar);
	}
	
	public boolean reload() {
		Logger.getGlobal().info(this.toString());
		
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
	
	private PaneToolbar() {super();}
	private static class Loader {
		private static final PaneToolbar INSTANCE = new PaneToolbar();
	}
	public static PaneToolbar getInstance() {
		return Loader.INSTANCE;
	}
}
