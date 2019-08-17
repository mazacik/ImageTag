package application.gui.panes.side;

import application.database.list.CustomList;
import application.gui.decorator.ColorUtil;
import application.gui.nodes.popup.ClickMenuTag;
import application.gui.nodes.simple.TextNode;
import application.gui.stage.Stages;
import application.main.Instances;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;

public class GroupNode extends VBox {
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
		for (String name : Instances.getTagListMain().getNames(getGroup())) {
			addNameNode(name);
		}
	}
	private void initEvents() {
		ChangeListener<Boolean> shiftChangeListener = (observable, oldValue, newValue) -> {
			if (newValue) {
				for (Node node : ownerPane.getGroupNodes().getChildren()) {
					if (node instanceof GroupNode) {
						GroupNode groupNode = (GroupNode) node;
						groupNode.setArrowFill(ColorUtil.getTextColorAlt());
					}
				}
			} else {
				for (Node node : ownerPane.getGroupNodes().getChildren()) {
					if (node instanceof GroupNode) {
						GroupNode groupNode = (GroupNode) node;
						groupNode.setArrowFill(ColorUtil.getTextColorDef());
					}
				}
			}
		};
		nodeExpCol.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			Stages.getMainStage().shiftDownProperty().addListener(shiftChangeListener);
			if (event.isShiftDown()) {
				for (Node node : ownerPane.getGroupNodes().getChildren()) {
					if (node instanceof GroupNode) {
						GroupNode groupNode = (GroupNode) node;
						groupNode.setArrowFill(ColorUtil.getTextColorAlt());
					}
				}
			} else {
				nodeExpCol.setTextFill(ColorUtil.getTextColorAlt());
			}
		});
		nodeExpCol.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			Stages.getMainStage().shiftDownProperty().removeListener(shiftChangeListener);
			if (event.isShiftDown()) {
				for (Node node : ownerPane.getGroupNodes().getChildren()) {
					if (node instanceof GroupNode) {
						GroupNode groupNode = (GroupNode) node;
						groupNode.setArrowFill(ColorUtil.getTextColorDef());
					}
				}
			} else {
				nodeExpCol.setTextFill(ColorUtil.getTextColorDef());
			}
		});
		hBoxGroup.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> this.setBackground(ColorUtil.getBackgroundAlt()));
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
						Instances.getReload().doReload();
						break;
					case SECONDARY:
						ClickMenuTag clickMenuTag = Instances.getClickMenuTag();
						clickMenuTag.setGroup(nodeTitle.getText());
						clickMenuTag.setName("");
						clickMenuTag.show(hBoxGroup, event);
						break;
				}
			}
		});
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
					Instances.getReload().doReload();
					break;
				case SECONDARY:
					ClickMenuTag clickMenuTag = Instances.getClickMenuTag();
					clickMenuTag.setGroup(getGroup());
					clickMenuTag.setName(nameNode.getText());
					clickMenuTag.show(nameNode, event);
					break;
			}
		});
		
		nameNodes.add(nameNode);
	}
	public void removeNameNode(String name) {
		TextNode TextNode = null;
		for (TextNode nameNode : nameNodes) {
			if (nameNode.getText().equals(name)) {
				TextNode = nameNode;
				break;
			}
		}
		nameNodes.remove(TextNode);
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
		this.getChildren().clear();
		this.getChildren().add(hBoxGroup);
		this.getChildren().addAll(nameNodes);
		nodeExpCol.setText("− ");
	}
	public void hideNameNodes() {
		this.getChildren().clear();
		this.getChildren().add(hBoxGroup);
		nodeExpCol.setText("+ ");
	}
	public void sortNameNodes() {
		nameNodes.sort(Comparator.comparing(Label::getText));
		showNameNodes();
	}
	public boolean isExpanded() {
		return this.getChildren().size() != 1;
	}
	
	public ArrayList<TextNode> getNameNodes() {
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
