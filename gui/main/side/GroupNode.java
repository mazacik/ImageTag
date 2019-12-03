package gui.main.side;

import baseobject.CustomList;
import gui.component.clickmenu.ClickMenu;
import gui.component.simple.HBox;
import gui.component.simple.TextNode;
import gui.component.simple.VBox;
import gui.decorator.ColorUtil;
import gui.stage.StageManager;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import main.InstanceCollector;

import java.util.Comparator;

public class GroupNode extends VBox implements InstanceCollector {
	private final TextNode nodeExpCol;
	private final TextNode nodeTitle;
	
	private final HBox hBoxGroup;
	private final CustomList<TextNode> nameNodes;
	
	private final SidePaneBase ownerPane;
	
	public GroupNode(SidePaneBase ownerPane, String group) {
		this.ownerPane = ownerPane;
		
		nodeExpCol = new TextNode("+ ", false, false, false, false);
		nodeTitle = new TextNode(group, false, false, false, false);
		nodeExpCol.setPadding(new Insets(0, 5, 0, 15));
		nodeTitle.setPadding(new Insets(0, 15, 0, 5));
		
		hBoxGroup = new HBox(nodeExpCol, nodeTitle);
		getChildren().add(hBoxGroup);
		
		nameNodes = new CustomList<>();
		
		initNameNodes();
		initEvents();
	}
	
	private void initNameNodes() {
		for (String name : tagListMain.getNames(getGroup())) {
			addNameNode(name);
		}
	}
	private void initEvents() {
		ChangeListener<Boolean> shiftChangeListener = (observable, oldValue, newValue) -> {
			if (newValue) {
				ownerPane.getGroupNodes().forEach(groupNode -> groupNode.setArrowFill(ColorUtil.getColorSecondary()));
			} else {
				ownerPane.getGroupNodes().forEach(groupNode -> groupNode.setArrowFill(ColorUtil.getColorPrimary()));
			}
		};
		nodeExpCol.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			StageManager.getMainStage().shiftDownProperty().addListener(shiftChangeListener);
			if (event.isShiftDown()) {
				ownerPane.getGroupNodes().forEach(groupNode -> groupNode.setArrowFill(ColorUtil.getColorSecondary()));
			} else {
				nodeExpCol.setTextFill(ColorUtil.getColorSecondary());
			}
		});
		nodeExpCol.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			StageManager.getMainStage().shiftDownProperty().removeListener(shiftChangeListener);
			if (event.isShiftDown()) {
				ownerPane.getGroupNodes().forEach(groupNode -> groupNode.setArrowFill(ColorUtil.getColorPrimary()));
			} else {
				nodeExpCol.setTextFill(ColorUtil.getColorPrimary());
			}
		});
		hBoxGroup.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> this.setBackground(ColorUtil.getBackgroundSecondary()));
		hBoxGroup.addEventFilter(MouseEvent.MOUSE_EXITED, event -> this.setBackground(Background.EMPTY));
		hBoxGroup.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			Bounds boundsExpCol = nodeExpCol.getBoundsInLocal();
			if (event.getX() <= boundsExpCol.getWidth() && event.getY() <= boundsExpCol.getHeight()) {
				if (!isExpanded()) {
					if (event.isShiftDown()) {
						ownerPane.expandAll();
					} else {
						showNameNodes();
					}
				} else {
					if (event.isShiftDown()) {
						ownerPane.collapseAll();
					} else {
						hideNameNodes();
					}
				}
			} else {
				switch (event.getButton()) {
					case PRIMARY:
						ownerPane.changeNodeState(this, null);
						reload.doReload();
						break;
					case SECONDARY:
						ClickMenu.setGroup(getGroup());
						ClickMenu.setName("");
						break;
				}
			}
		});
		ClickMenu.install(hBoxGroup, MouseButton.SECONDARY, ClickMenu.StaticInstance.TAG);
	}
	
	public void addNameNode(String name) {
		for (TextNode nameNode : nameNodes) {
			if (nameNode.getText().equals(name)) {
				return;
			}
		}
		
		TextNode nameNode = new TextNode(name, true, false, false, false);
		nameNode.setAlignment(Pos.CENTER_LEFT);
		nameNode.prefWidthProperty().bind(this.widthProperty());
		nameNode.setPadding(new Insets(0, 0, 0, 50));
		nameNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			switch (event.getButton()) {
				case PRIMARY:
					ownerPane.changeNodeState(this, nameNode);
					reload.doReload();
					break;
				case SECONDARY:
					ClickMenu.setGroup(getGroup());
					ClickMenu.setName(nameNode.getText());
					break;
			}
		});
		ClickMenu.install(nameNode, MouseButton.SECONDARY, ClickMenu.StaticInstance.TAG);
		
		nameNodes.add(nameNode);
	}
	public void removeNameNode(String name) {
		TextNode textNode = null;
		for (TextNode nameNode : nameNodes) {
			if (nameNode.getText().equals(name)) {
				this.getChildren().remove(nameNode);
				textNode = nameNode;
				break;
			}
		}
		nameNodes.remove(textNode);
	}
	
	public void updateNameNode(String oldName, String newName) {
		for (TextNode nameNode : nameNodes) {
			if (nameNode.getText().equals(oldName)) {
				nameNode.setText(newName);
				break;
			}
		}
	}
	
	public void showNameNodes() {
		this.getChildren().retainAll(hBoxGroup);
		this.getChildren().addAll(nameNodes);
		nodeExpCol.setText("− ");
	}
	public void hideNameNodes() {
		this.getChildren().retainAll(hBoxGroup);
		nodeExpCol.setText("+ ");
	}
	public void sortNameNodes() {
		nameNodes.sort(Comparator.comparing(Label::getText));
		showNameNodes();
	}
	public boolean isExpanded() {
		return this.getChildren().size() > 1;
	}
	
	public CustomList<TextNode> getNameNodes() {
		return nameNodes;
	}
	public String getGroup() {
		return nodeTitle.getText();
	}
	
	public void setGroup(String group) {
		nodeTitle.setText(group);
	}
	public void setArrowFill(Color fill) {
		nodeExpCol.setTextFill(fill);
	}
	public void setTextFill(Color fill) {
		nodeTitle.setTextFill(fill);
	}
}
