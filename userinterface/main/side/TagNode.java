package userinterface.main.side;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.InstanceManager;
import userinterface.nodes.NodeUtil;
import userinterface.nodes.base.TextNode;
import userinterface.nodes.menu.ClickMenuTag;
import userinterface.style.ColorUtil;
import userinterface.style.StyleUtil;
import userinterface.style.enums.ColorType;

import java.util.ArrayList;
import java.util.Comparator;

public class TagNode extends VBox {
	private final TextNode labelArrow;
	private final TextNode labelText;
	
	private final HBox groupNode;
	private final ArrayList<TextNode> nameNodes;
	
	private final SidePaneBase owner;
	
	public TagNode(SidePaneBase owner, String group) {
		this.owner = owner;
		
		labelArrow = new TextNode("+ ");
		labelText = new TextNode(group);
		labelArrow.setPadding(new Insets(0, 5, 0, 15));
		labelText.setPadding(new Insets(0, 15, 0, 5));
		labelArrow.setTextFill(ColorUtil.getTextColorDef());
		
		groupNode = NodeUtil.getHBox(ColorType.DEF, ColorType.ALT, labelArrow, labelText);
		getChildren().add(groupNode);
		
		nameNodes = new ArrayList<>();
		
		initNameNodes();
		initEvents();
		
		StyleUtil.addToManager(this, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
	}
	private void initNameNodes() {
		for (String name : InstanceManager.getTagListMain().getNames(getGroup())) {
			addNameNode(name);
		}
	}
	private void initEvents() {
		ChangeListener<Boolean> shiftChangeListener = (observable, oldValue, newValue) -> {
			if (newValue) {
				for (Node node : owner.getTagNodesBox().getChildren()) {
					if (node instanceof TagNode) {
						TagNode tagNode = (TagNode) node;
						tagNode.setArrowFill(ColorUtil.getTextColorAlt());
					}
				}
			} else {
				for (Node node : owner.getTagNodesBox().getChildren()) {
					if (node instanceof TagNode) {
						TagNode tagNode = (TagNode) node;
						tagNode.setArrowFill(ColorUtil.getTextColorDef());
					}
				}
			}
		};
		labelArrow.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			InstanceManager.getMainStageEvent().shiftDownProperty().addListener(shiftChangeListener);
			if (event.isShiftDown()) {
				for (Node node : owner.getTagNodesBox().getChildren()) {
					if (node instanceof TagNode) {
						TagNode tagNode = (TagNode) node;
						tagNode.setArrowFill(ColorUtil.getTextColorAlt());
					}
				}
			} else {
				labelArrow.setTextFill(ColorUtil.getTextColorAlt());
			}
		});
		labelArrow.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			InstanceManager.getMainStageEvent().shiftDownProperty().removeListener(shiftChangeListener);
			if (event.isShiftDown()) {
				for (Node node : owner.getTagNodesBox().getChildren()) {
					if (node instanceof TagNode) {
						TagNode tagNode = (TagNode) node;
						tagNode.setArrowFill(ColorUtil.getTextColorDef());
					}
				}
			} else {
				labelArrow.setTextFill(ColorUtil.getTextColorDef());
			}
		});
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
						InstanceManager.getReload().doReload();
						break;
					case SECONDARY:
						ClickMenuTag clickMenuTag = InstanceManager.getClickMenuTag();
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
		
		TextNode nameNode = new TextNode(name, ColorType.DEF, ColorType.ALT, ColorType.NULL, ColorType.NULL);
		nameNode.setAlignment(Pos.CENTER_LEFT);
		nameNode.prefWidthProperty().bind(this.widthProperty());
		nameNode.setPadding(new Insets(0, 0, 0, 50));
		nameNode.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			switch (event.getButton()) {
				case PRIMARY:
					owner.changeNodeState(this, nameNode);
					InstanceManager.getReload().doReload();
					break;
				case SECONDARY:
					ClickMenuTag clickMenuTag = InstanceManager.getClickMenuTag();
					clickMenuTag.setGroup(getGroup());
					clickMenuTag.setName(nameNode.getText());
					clickMenuTag.show(nameNode, event);
					break;
			}
		});
		
		nameNodes.add(nameNode);
	}
	public void removeNameNode(String name) {
		TextNode textNode = null;
		for (TextNode nameNode : nameNodes) {
			if (nameNode.getText().equals(name)) {
				textNode = nameNode;
				break;
			}
		}
		nameNodes.remove(textNode);
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
		nameNodes.sort(Comparator.comparing(Labeled::getText));
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
