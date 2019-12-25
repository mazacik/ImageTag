package ui.main.side;

import base.CustomList;
import base.tag.TagList;
import ui.component.clickmenu.ClickMenu;
import ui.component.simple.HBox;
import ui.component.simple.TextNode;
import ui.component.simple.VBox;
import ui.decorator.ColorUtil;
import ui.stage.StageManager;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

import java.util.Comparator;

public class GroupNode extends VBox {
	private final CustomList<NameNode> nodes;
	
	private final SidePaneBase parentPane;
	
	private final TextNode nodeToggle;
	private final TextNode nodeText;
	private final HBox hBoxMain;
	
	public GroupNode(SidePaneBase parentPane, String text) {
		nodes = new CustomList<>();
		
		this.parentPane = parentPane;
		
		nodeToggle = new TextNode("+ ", false, false, false, false);
		nodeToggle.setPadding(new Insets(0, 5, 0, 15));
		nodeText = new TextNode(text, false, false, false, false);
		nodeText.setPadding(new Insets(0, 15, 0, 5));
		
		hBoxMain = new HBox(nodeToggle, nodeText);
		this.getChildren().add(hBoxMain);
		
		for (String name : TagList.getMainInstance().getNames(getText())) {
			this.add(name);
		}
		
		this.initEvents();
	}
	
	private void initEvents() {
		//todo rework
		ChangeListener<Boolean> shiftChangeListener = (observable, oldValue, newValue) -> {
			if (newValue) {
				parentPane.getGroupNodes().forEach(groupNode -> groupNode.setToggleFill(ColorUtil.getColorSecondary()));
			} else {
				parentPane.getGroupNodes().forEach(groupNode -> groupNode.setToggleFill(ColorUtil.getColorPrimary()));
			}
		};
		nodeToggle.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			StageManager.getStageMain().shiftDownProperty().addListener(shiftChangeListener);
			if (event.isShiftDown()) {
				parentPane.getGroupNodes().forEach(groupNode -> groupNode.setToggleFill(ColorUtil.getColorSecondary()));
			} else {
				nodeToggle.setTextFill(ColorUtil.getColorSecondary());
			}
		});
		nodeToggle.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			StageManager.getStageMain().shiftDownProperty().removeListener(shiftChangeListener);
			if (event.isShiftDown()) {
				parentPane.getGroupNodes().forEach(groupNode -> groupNode.setToggleFill(ColorUtil.getColorPrimary()));
			} else {
				nodeToggle.setTextFill(ColorUtil.getColorPrimary());
			}
		});
		
		hBoxMain.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> this.setBackground(ColorUtil.getBackgroundSecondary()));
		hBoxMain.addEventFilter(MouseEvent.MOUSE_EXITED, event -> this.setBackground(Background.EMPTY));
		hBoxMain.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			Bounds boundsExpCol = nodeToggle.getBoundsInLocal();
			if (event.getX() <= boundsExpCol.getWidth() && event.getY() <= boundsExpCol.getHeight()) {
				if (!this.isExpanded()) {
					if (event.isShiftDown()) {
						parentPane.expandAll();
					} else {
						this.show();
					}
				} else {
					if (event.isShiftDown()) {
						parentPane.collapseAll();
					} else {
						this.hide();
					}
				}
			} else {
				switch (event.getButton()) {
					case PRIMARY:
						if (this.isExpanded()) {
							this.hide();
						} else {
							this.show();
						}
						break;
					case SECONDARY:
						ClickMenu.setGroup(getText());
						ClickMenu.setName("");
						break;
				}
			}
		});
		
		ClickMenu.install(hBoxMain, MouseButton.SECONDARY, ClickMenu.StaticInstance.TAG_GROUP);
	}
	
	public void add(String name) {
		if (!this.contains(name)) {
			nodes.add(new NameNode(this, name));
		}
	}
	public void remove(String name) {
		NameNode nameNode = null;
		
		for (NameNode _nameNode : nodes) {
			if (name.equals(_nameNode.getText())) {
				nameNode = _nameNode;
				break;
			}
		}
		
		this.getChildren().remove(nameNode);
		nodes.remove(nameNode);
	}
	public void update(String nameOld, String nameNew) {
		for (NameNode nameNode : nodes) {
			if (nameNode.getText().equals(nameOld)) {
				nameNode.setText(nameNew);
				break;
			}
		}
	}
	
	public boolean contains(String name) {
		for (NameNode nameNode : nodes) {
			if (nameNode.getText().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	public void show() {
		this.getChildren().addAll(nodes);
		nodeToggle.setText("− ");
	}
	public void hide() {
		this.getChildren().retainAll(hBoxMain);
		nodeToggle.setText("+ ");
	}
	public void sort() {
		nodes.sort(Comparator.comparing(Label::getText));
	}
	
	public boolean isExpanded() {
		return getChildren().size() > 1;
	}
	
	public String getText() {
		return nodeText.getText();
	}
	public CustomList<NameNode> getNodes() {
		return nodes;
	}
	public SidePaneBase getParentPane() {
		return parentPane;
	}
	
	public void setText(String group) {
		nodeText.setText(group);
	}
	public void setToggleFill(Color fill) {
		nodeToggle.setTextFill(fill);
	}
	public void setTextFill(Color fill) {
		nodeText.setTextFill(fill);
	}
}
