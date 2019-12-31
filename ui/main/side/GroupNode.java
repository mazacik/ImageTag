package ui.main.side;

import base.CustomList;
import base.tag.TagList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import ui.component.clickmenu.ClickMenu;
import ui.component.simple.HBox;
import ui.component.simple.TextNode;
import ui.component.simple.VBox;
import ui.decorator.ColorUtil;

import java.util.Comparator;

public class GroupNode extends VBox {
	private final CustomList<NameNode> nameNodes;
	
	private final SidePaneBase parentPane;
	
	private final TextNode nodeToggle;
	private final TextNode nodeText;
	private final HBox hBoxMain;
	
	public GroupNode(SidePaneBase parentPane, String text) {
		nameNodes = new CustomList<>();
		
		this.parentPane = parentPane;
		
		nodeToggle = new TextNode("+ ", false, false, false, false);
		nodeToggle.setPadding(new Insets(0, 5, 0, 15));
		nodeText = new TextNode(text, false, false, false, false);
		nodeText.setPadding(new Insets(0, 15, 0, 5));
		
		hBoxMain = new HBox(nodeToggle, nodeText);
		this.getChildren().add(hBoxMain);
		
		TagList.getMainInstance().getNames(this.getGroup()).forEach(this::addNameNode);
		
		hBoxMain.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> this.setBackground(ColorUtil.getBackgroundSecondary()));
		hBoxMain.addEventFilter(MouseEvent.MOUSE_EXITED, event -> this.setBackground(Background.EMPTY));
		hBoxMain.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			switch (event.getButton()) {
				case PRIMARY:
					if (!this.isExpanded()) {
						if (event.isShiftDown()) {
							parentPane.expandAll();
						} else {
							this.expand();
						}
					} else {
						if (event.isShiftDown()) {
							parentPane.collapseAll();
						} else {
							this.collapse();
						}
					}
					break;
				case SECONDARY:
					ClickMenu.setGroup(getGroup());
					ClickMenu.setName("");
					break;
			}
		});
		
		ClickMenu.install(hBoxMain, MouseButton.SECONDARY, ClickMenu.StaticInstance.TAG_GROUP);
	}
	
	public void addNameNode(String name) {
		if (!this.contains(name)) {
			nameNodes.add(new NameNode(this, name));
		}
	}
	public void removeNameNode(String name) {
		NameNode nameNode = this.getNameNode(name);
		if (nameNode != null) {
			if (nameNodes.size() == 1) {
				parentPane.boxGroupNodes.getChildren().remove(this);
			} else {
				nameNodes.remove(nameNode);
				this.getChildren().remove(nameNode);
			}
		}
	}
	
	public void sort() {
		nameNodes.sort(Comparator.comparing(Label::getText));
	}
	public boolean contains(String name) {
		return this.getNameNode(name) != null;
	}
	
	public void expand() {
		this.getChildren().addAll(nameNodes);
		nodeToggle.setText("− ");
	}
	public void collapse() {
		this.getChildren().retainAll(hBoxMain);
		nodeToggle.setText("+ ");
	}
	
	public boolean isExpanded() {
		return getChildren().size() > 1;
	}
	
	public String getGroup() {
		return nodeText.getText();
	}
	public NameNode getNameNode(String name) {
		for (NameNode nameNode : nameNodes) {
			if (name.equals(nameNode.getText())) {
				return nameNode;
			}
		}
		return null;
	}
	public CustomList<NameNode> getNameNodes() {
		return nameNodes;
	}
	public SidePaneBase getParentPane() {
		return parentPane;
	}
	
	public void setGroup(String group) {
		nodeText.setText(group);
	}
	public void setTextFill(Color fill) {
		nodeText.setTextFill(fill);
	}
}
