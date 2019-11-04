package application.frontend.pane.top;

import application.backend.base.CustomList;
import application.backend.base.entity.Entity;
import application.backend.util.JointGroupUtil;
import application.backend.util.enums.Direction;
import application.frontend.component.ClickMenu;
import application.frontend.component.buttons.ButtonTemplates;
import application.frontend.component.custom.TitleBar;
import application.frontend.component.simple.SeparatorNode;
import application.frontend.component.simple.TextNode;
import application.frontend.decorator.SizeUtil;
import application.frontend.pane.PaneInterface;
import application.main.InstanceCollector;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

public class ToolbarPane extends TitleBar implements PaneInterface, InstanceCollector {
	private TextNode nodeTarget;
	
	private boolean needsReload;
	
	public ToolbarPane() {
	
	}
	
	public void init() {
		super.init("");
		
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
		Entity currentTarget = target.get();
		if (currentTarget.getJointID() != 0) {
			CustomList<Entity> jointObject = JointGroupUtil.getJointObjects(currentTarget);
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
