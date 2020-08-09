package frontend.component.side.select;

import backend.entity.Entity;
import frontend.UserInterface;
import frontend.decorator.DecoratorUtil;
import frontend.node.SeparatorNode;
import frontend.node.override.HBox;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import main.Main;

import static frontend.component.side.SidePaneBase.WIDTH;

public class SelectPane extends VBox {
	private final TextNode selectionTagsNode;
	private final TextNode targetDetailsNode;
	
	private final SelectionTagsPane selectionTagsPane;
	private final TargetDetailsPane targetDetailsPane;
	
	private boolean bHidden = false;
	
	public SelectPane() {
		selectionTagsPane = new SelectionTagsPane();
		targetDetailsPane = new TargetDetailsPane();
		
		selectionTagsNode = new TextNode("", true, true, false, true);
		targetDetailsNode = new TextNode("Details", true, true, false, true);
	}
	
	public void initialize() {
		selectionTagsNode.setMaxWidth(Double.MAX_VALUE);
		selectionTagsNode.setBorder(DecoratorUtil.getBorder(0, 0, 0, 1));
		HBox.setHgrow(selectionTagsNode, Priority.ALWAYS);
		selectionTagsNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			this.getChildren().set(1, selectionTagsPane);
			this.refresh();
		});
		
		targetDetailsNode.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(targetDetailsNode, Priority.ALWAYS);
		targetDetailsNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			this.getChildren().set(1, targetDetailsPane);
			this.refresh();
		});
		
		HBox boxTop = new HBox(selectionTagsNode, new SeparatorNode(), targetDetailsNode);
		boxTop.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		
		String cHide = "→";
		String cShow = "←";
		TextNode btnHide = new TextNode(cHide, true, true, false, true);
		btnHide.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			bHidden = !bHidden;
			if (bHidden) {
				btnHide.setText(cShow);
				this.getChildren().setAll(btnHide);
				this.setMinWidth(btnHide.getWidth() + 1);
				this.setMaxWidth(btnHide.getWidth() + 1);
				UserInterface.getStage().getMainScene().handleWidthChange(UserInterface.getFilterPane().getWidth(), btnHide.getWidth() + 1);
			} else {
				btnHide.setText(cHide);
				this.getChildren().setAll(boxTop, selectionTagsPane);
				boxTop.getChildren().add(0, btnHide);
				this.setMinWidth(WIDTH);
				this.setMaxWidth(WIDTH);
				UserInterface.getStage().getMainScene().handleWidthChange(UserInterface.getFilterPane().getWidth(), WIDTH);
			}
		});
		
		boxTop.getChildren().add(0, btnHide);
		
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
