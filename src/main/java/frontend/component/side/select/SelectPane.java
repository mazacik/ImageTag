package frontend.component.side.select;

import backend.entity.Entity;
import frontend.decorator.DecoratorUtil;
import frontend.node.SeparatorNode;
import frontend.node.override.HBox;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import main.Main;

public class SelectPane extends VBox {
	private final TextNode selectionTagsNode;
	private final TextNode targetDetailsNode;
	
	private final SelectionTagsPane selectionTagsPane;
	private final TargetDetailsPane targetDetailsPane;
	
	public SelectPane() {
		selectionTagsPane = new SelectionTagsPane();
		targetDetailsPane = new TargetDetailsPane();
		
		selectionTagsNode = new TextNode("", true, true, false, true);
		targetDetailsNode = new TextNode("Details", true, true, false, true);
	}
	
	public void initialize() {
		selectionTagsNode.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		selectionTagsNode.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(selectionTagsNode, Priority.ALWAYS);
		selectionTagsNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			this.getChildren().set(1, selectionTagsPane);
			this.refresh();
		});
		
		targetDetailsNode.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		targetDetailsNode.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(targetDetailsNode, Priority.ALWAYS);
		targetDetailsNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			this.getChildren().set(1, targetDetailsPane);
			this.refresh();
		});
		
		HBox boxTop = new HBox(selectionTagsNode, new SeparatorNode(), targetDetailsNode);
		
		HBox.setHgrow(this, Priority.ALWAYS);
		this.setBorder(DecoratorUtil.getBorder(0, 0, 0, 1));
		this.getChildren().addAll(boxTop, selectionTagsPane);
	}
	
	public boolean refresh() {
		int shown = 0;
		int hidden = 0;
		for (Entity entity : Main.SELECT) {
			if (Main.FILTER.contains(entity)) {
				shown++;
			} else {
				hidden++;
			}
		}
		String text = "Selection: " + shown;
		if (hidden > 0) text += " (+" + hidden + " filtered out)";
		selectionTagsNode.setText(text);
		
		if (this.getChildren().contains(targetDetailsPane)) {
			targetDetailsPane.refresh();
		} else if (this.getChildren().contains(selectionTagsPane)) {
			selectionTagsPane.refresh();
		}
		
		return true;
	}
	public boolean reload() {
		return selectionTagsPane.reload();
	}
	
	public SelectionTagsPane getSelectionTagsPane() {
		return selectionTagsPane;
	}
	public TargetDetailsPane getTargetDetailsPane() {
		return targetDetailsPane;
	}
}
