package application.gui.panes.top;

import application.database.list.CustomList;
import application.database.object.DataObject;
import application.gui.decorator.SizeUtil;
import application.gui.nodes.ClickMenu;
import application.gui.nodes.buttons.ButtonTemplates;
import application.gui.nodes.custom.TitleBar;
import application.gui.nodes.simple.SeparatorNode;
import application.gui.nodes.simple.TextNode;
import application.gui.panes.NodeBase;
import application.main.Instances;
import application.misc.enums.Direction;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

public class ToolbarPane extends TitleBar implements NodeBase {
	private final TextNode nodeTarget;
	
	private boolean needsReload;
	
	public ToolbarPane() {
		super("");
		
		needsReload = false;
		
		TextNode nodeFile = new TextNode("File", true, true, false, true);
		TextNode nodeSave = ButtonTemplates.APPLICATION_SAVE.get();
		TextNode nodeImport = ButtonTemplates.APPLICATION_IMPORT.get();
		TextNode nodeExit = ButtonTemplates.APPLICATION_EXIT.get();
		ClickMenu.install(nodeFile, Direction.DOWN, nodeSave, nodeImport, new SeparatorNode(), nodeExit);
		
		TextNode nodeRandom = ButtonTemplates.FILTER_RANDOM.get();
		HBox hBoxTools = new HBox(nodeRandom);
		hBoxTools.setAlignment(Pos.CENTER);
		
		nodeTarget = new TextNode("", true, true, false, true);
		ClickMenu.install(nodeTarget, Direction.DOWN, MouseButton.PRIMARY, ClickMenu.StaticInstance.DATA);
		
		HBox hBox = new HBox(nodeFile, hBoxTools);
		hBox.setAlignment(Pos.CENTER);
		
		this.setPrefHeight(SizeUtil.getPrefHeightTopMenu());
		this.setLeft(hBox);
		this.setCenter(nodeTarget);
	}
	
	public boolean reload() {
		DataObject currentTarget = Instances.getTarget().getCurrentTarget();
		if (currentTarget.getJointID() != 0) {
			CustomList<DataObject> jointObject = currentTarget.getJointObjects();
			String jointGroupIndex = (jointObject.indexOf(currentTarget) + 1) + "/" + jointObject.size();
			nodeTarget.setText("[" + jointGroupIndex + "] " + currentTarget.getName());
		} else {
			nodeTarget.setText(currentTarget.getName());
		}
		
		return true;
	}
	
	@Override
	public boolean getNeedsReload() {
		return needsReload;
	}
	@Override
	public void setNeedsReload(boolean needsReload) {
		this.needsReload = needsReload;
	}
}
