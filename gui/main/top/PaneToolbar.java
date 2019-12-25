package gui.main.top;

import baseobject.CustomList;
import baseobject.entity.Entity;
import gui.component.clickmenu.ClickMenu;
import gui.component.simple.BoxSeparatorNode;
import gui.component.simple.HBox;
import gui.component.simple.TextNode;
import gui.component.simple.template.ButtonTemplates;
import gui.decorator.SizeUtil;
import gui.stage.TitleBar;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import main.InstanceCollector;
import tools.enums.Direction;

import java.util.logging.Logger;

public class PaneToolbar extends TitleBar implements InstanceCollector {
	private TextNode nodeTarget;
	
	public PaneToolbar() {
	
	}
	
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
		
		Entity currentTarget = target.get();
		if (currentTarget.getCollectionID() != 0) {
			CustomList<Entity> collection = currentTarget.getCollection();
			String collectionIndex = (collection.indexOf(currentTarget) + 1) + "/" + collection.size();
			nodeTarget.setText("[" + collectionIndex + "] " + currentTarget.getName());
		} else {
			nodeTarget.setText(currentTarget.getName());
		}
		
		return true;
	}
}
