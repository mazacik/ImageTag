package frontend.component.side.select;

import backend.list.entity.Entity;
import frontend.component.side.SidePaneBase;
import frontend.decorator.DecoratorUtil;
import frontend.node.SeparatorNode;
import frontend.node.override.HBox;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import main.Main;

public class SelectionPane extends VBox {
	private final TextNode selectionTagsNode;
	private final TextNode targetDetailsNode;
	
	private final SelectionTagsPane selectionTagsPane;
	private final TargetDetailsPane targetDetailsPane;
	
	public SelectionPane() {
		selectionTagsPane = new SelectionTagsPane();
		targetDetailsPane = new TargetDetailsPane();
		
		selectionTagsNode = new TextNode("Selection Tags", true, true, false, true);
		
		selectionTagsNode.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		selectionTagsNode.setMaxWidth(Double.MAX_VALUE);
		selectionTagsNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			this.getChildren().set(1, selectionTagsPane);
		});
		
		targetDetailsNode = new TextNode("Target Details", true, true, false, true);
		targetDetailsNode.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		targetDetailsNode.setMaxWidth(Double.MAX_VALUE);
		targetDetailsNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			this.getChildren().set(1, targetDetailsPane);
		});
		
		HBox boxTop = new HBox(selectionTagsNode, new SeparatorNode(), targetDetailsNode);
		HBox.setHgrow(selectionTagsNode, Priority.ALWAYS);
		HBox.setHgrow(targetDetailsNode, Priority.ALWAYS);
		
		this.setMinWidth(SidePaneBase.MIN_WIDTH);
		HBox.setHgrow(this, Priority.ALWAYS);
		
		this.setBorder(DecoratorUtil.getBorder(0, 0, 0, 1));
		this.getChildren().addAll(boxTop, selectionTagsPane);
	}
	
	public boolean refresh() {
		targetDetailsPane.refresh();
		
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
		
		return selectionTagsPane.refresh();
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