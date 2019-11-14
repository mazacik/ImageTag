package gui.main.top;

import baseobject.CustomList;
import baseobject.entity.Entity;
import control.reload.Reloadable;
import gui.component.clickmenu.ClickMenu;
import gui.component.simple.BoxSeparatorNode;
import gui.component.simple.TextNode;
import gui.component.simple.template.ButtonTemplates;
import gui.decorator.SizeUtil;
import gui.stage.TitleBar;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import main.InstanceCollector;
import tools.EntityGroupUtil;
import tools.enums.Direction;

import java.lang.reflect.Method;
import java.util.logging.Logger;

public class ToolbarPane extends TitleBar implements Reloadable, InstanceCollector {
	private TextNode nodeTarget;
	
	public ToolbarPane() {
	
	}
	
	public void init() {
		super.init("");
		
		methodsToInvokeOnNextReload = new CustomList<>();
		
		TextNode nodeFile = new TextNode("File", true, true, false, true);
		TextNode nodeSave = ButtonTemplates.APPLICATION_SAVE.get();
		TextNode nodeImport = ButtonTemplates.APPLICATION_IMPORT.get();
		TextNode nodeExit = ButtonTemplates.APPLICATION_EXIT.get();
		ClickMenu.install(nodeFile, Direction.DOWN, nodeSave, nodeImport, new BoxSeparatorNode(), nodeExit);
		
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
	
	private CustomList<Method> methodsToInvokeOnNextReload;
	@Override
	public CustomList<Method> getMethodsToInvokeOnNextReload() {
		return methodsToInvokeOnNextReload;
	}
	public boolean reload() {
		Logger.getGlobal().info(this.toString());
		
		Entity currentTarget = target.get();
		if (currentTarget.getEntityGroupID() != 0) {
			CustomList<Entity> entityGroup = currentTarget.getEntityGroup();
			String entityGroupIndex = (entityGroup.indexOf(currentTarget) + 1) + "/" + entityGroup.size();
			nodeTarget.setText("[" + entityGroupIndex + "] " + currentTarget.getName());
		} else {
			nodeTarget.setText(currentTarget.getName());
		}
		
		return true;
	}
}
