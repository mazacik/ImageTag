package frontend.component.side.select;

import backend.entity.Entity;
import frontend.UserInterface;
import frontend.decorator.DecoratorUtil;
import frontend.node.SeparatorNode;
import frontend.node.override.HBox;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import main.Main;

public class SelectPane extends VBox {
	public static final double DRAG_AREA_HEIGHT = 5;
	
	private final TextNode selectionTagsNode;
	private final TextNode targetDetailsNode;
	
	private final SelectionTagsPane tagsPane;
	private final TargetDetailsPane detailsPane;
	
	public SelectPane() {
		tagsPane = new SelectionTagsPane();
		detailsPane = new TargetDetailsPane();
		
		selectionTagsNode = new TextNode("", true, true, false, true);
		targetDetailsNode = new TextNode("Details", true, true, false, true);
		
		this.setMinHeight(100);
	}
	
	public void initialize() {
		selectionTagsNode.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(selectionTagsNode, Priority.ALWAYS);
		selectionTagsNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			this.getChildren().set(1, tagsPane);
			this.refresh();
		});
		
		targetDetailsNode.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(targetDetailsNode, Priority.ALWAYS);
		targetDetailsNode.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			this.getChildren().set(1, detailsPane);
			this.refresh();
		});
		
		initResizeHandlers();
		
		HBox boxTop = new HBox(selectionTagsNode, new SeparatorNode(), targetDetailsNode);
		boxTop.setBorder(DecoratorUtil.getBorder(0, 0, 1, 0));
		
		this.getChildren().addAll(boxTop, tagsPane);
	}
	
	private void initResizeHandlers() {
		this.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
			if (event.getY() > this.getHeight() - DRAG_AREA_HEIGHT) {
				UserInterface.getStage().getScene().setCursor(Cursor.N_RESIZE);
			} else {
				UserInterface.getStage().getScene().setCursor(Cursor.DEFAULT);
			}
		});
		
		this.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			UserInterface.getStage().getScene().setCursor(Cursor.DEFAULT);
		});
		
		final double[] start = new double[]{-1, -1};
		this.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				if (event.getY() > this.getHeight() - DRAG_AREA_HEIGHT) {
					start[0] = event.getSceneY();
					start[1] = this.getHeight();
				}
			}
		});
		
		this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				if (event.getY() > this.getHeight() - DRAG_AREA_HEIGHT) {
					event.consume();
				}
			}
		});
		
		this.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				if (start[0] > 0) {
					double height = event.getSceneY() - start[0] + start[1];
					if (height > DRAG_AREA_HEIGHT) this.setMinHeight(height);
				}
			}
		});
		
		this.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				start[0] = -1;
			}
		});
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
		
		if (this.getChildren().contains(detailsPane)) {
			detailsPane.refresh();
		} else if (this.getChildren().contains(tagsPane)) {
			tagsPane.refresh();
		}
		
		return true;
	}
	public boolean reload() {
		return tagsPane.reload();
	}
	
	public SelectionTagsPane getTagsPane() {
		return tagsPane;
	}
	public TargetDetailsPane getDetailsPane() {
		return detailsPane;
	}
}
