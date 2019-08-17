package application.gui.panes.side;

import application.database.list.CustomList;
import application.gui.decorator.ColorUtil;
import application.gui.nodes.popup.ClickMenuTag;
import application.gui.nodes.simple.TextNode;
import application.gui.stage.Stages;
import application.main.Instances;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Comparator;

public class GroupNode extends VBox {
	//todo split between their panes
	private final TextNode labelArrow;
	private final TextNode labelText;
	
	private final HBox groupNode;
	private final CustomList<TextNode> nameNodes;
	
	private final SidePaneBase owner;
	
	public GroupNode(SidePaneBase owner, String group) {
		this.owner = owner;
		
		labelArrow = new TextNode("+ ", false, false, false, false);
		labelText = new TextNode(group, false, false, false, false);
		labelArrow.setPadding(new Insets(0, 5, 0, 15));
		labelText.setPadding(new Insets(0, 15, 0, 5));
		labelArrow.setTextFill(ColorUtil.getTextColorDef());
		
		groupNode = new HBox(labelArrow, labelText);
		getChildren().add(groupNode);
		
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
				for (Node node : owner.getGroupNodes().getChildren()) {
					if (node instanceof GroupNode) {
						GroupNode groupNode = (GroupNode) node;
						groupNode.setArrowFill(ColorUtil.getTextColorAlt());
					}
				}
			} else {
				for (Node node : owner.getGroupNodes().getChildren()) {
					if (node instanceof GroupNode) {
						GroupNode groupNode = (GroupNode) node;
						groupNode.setArrowFill(ColorUtil.getTextColorDef());
					}
				}
			}
		};
		labelArrow.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			Stages.getMainStage().shiftDownProperty().addListener(shiftChangeListener);
			if (event.isShiftDown()) {
				for (Node node : owner.getGroupNodes().getChildren()) {
					if (node instanceof GroupNode) {
						GroupNode groupNode = (GroupNode) node;
						groupNode.setArrowFill(ColorUtil.getTextColorAlt());
					}
				}
			} else {
				labelArrow.setTextFill(ColorUtil.getTextColorAlt());
			}
		});
		labelArrow.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			Stages.getMainStage().shiftDownProperty().removeListener(shiftChangeListener);
			if (event.isShiftDown()) {
				for (Node node : owner.getGroupNodes().getChildren()) {
					if (node instanceof GroupNode) {
						GroupNode groupNode = (GroupNode) node;
						groupNode.setArrowFill(ColorUtil.getTextColorDef());
					}
				}
			} else {
				labelArrow.setTextFill(ColorUtil.getTextColorDef());
			}
		});
		groupNode.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> this.setBackground(ColorUtil.getBackgroundAlt()));
		groupNode.addEventFilter(MouseEvent.MOUSE_EXITED, event -> this.setBackground(Background.EMPTY));
		groupNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getPickResult().getIntersectedNode().getParent().equals(labelArrow)) {
				if (!isExpanded()) {
					if (event.isShiftDown()) {
						owner.expandAll();
					} else {
						showNameNodes();
					}
				} else {
					if (event.isShiftDown()) {
						owner.collapseAll();
					} else {
						hideNameNodes();
					}
				}
			} else {
				switch (event.getButton()) {
					case PRIMARY:
						owner.changeNodeState(this, null);
						Instances.getReload().doReload();
						break;
					case SECONDARY:
						ClickMenuTag clickMenuTag = Instances.getClickMenuTag();
						clickMenuTag.setGroup(labelText.getText());
						clickMenuTag.setName("");
						clickMenuTag.show(groupNode, event);
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
					owner.changeNodeState(this, nameNode);
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
		this.getChildren().add(groupNode);
		this.getChildren().addAll(nameNodes);
		labelArrow.setText("− ");
	}
	public void hideNameNodes() {
		this.getChildren().clear();
		this.getChildren().add(groupNode);
		labelArrow.setText("+ ");
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
		return labelText.getText();
	}
	
	public void setGroup(String group) {
		labelText.setText(group);
	}
	public void setArrowFill(Color fill) {
		labelArrow.setTextFill(fill);
	}
	public void setTextFill(Color fill) {
		labelText.setTextFill(fill);
	}
	public void setFont(Font font) {
		labelArrow.setFont(font);
		labelText.setFont(font);
	}
}
