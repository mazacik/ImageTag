package application.pane.top;

import application.base.CustomList;
import application.base.entity.Entity;
import application.component.ClickMenu;
import application.component.buttons.ButtonTemplates;
import application.component.custom.TitleBar;
import application.component.simple.SeparatorNode;
import application.component.simple.TextNode;
import application.control.reload.Reloadable;
import application.decorator.SizeUtil;
import application.main.InstanceCollector;
import application.util.EntityGroupUtil;
import application.util.enums.Direction;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

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
		ClickMenu.install(nodeFile, Direction.DOWN, nodeSave, nodeImport, new SeparatorNode(), nodeExit);
		
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
			CustomList<Entity> entityGroup = EntityGroupUtil.getEntityGroup(currentTarget);
			String entityGroupIndex = (entityGroup.indexOf(currentTarget) + 1) + "/" + entityGroup.size();
			nodeTarget.setText("[" + entityGroupIndex + "] " + currentTarget.getName());
		} else {
			nodeTarget.setText(currentTarget.getName());
		}
		
		return true;
	}
}
