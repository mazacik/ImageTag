package ui.node;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import ui.decorator.Decorator;
import enums.Direction;
import ui.override.HBox;

public class NodeCheckbox extends HBox {
	private final NodeText nodeMark;
	private final NodeText nodeText;
	
	private SimpleBooleanProperty selectedProperty;
	
	public NodeCheckbox(String text) {
		this(text, Direction.LEFT, false);
	}
	public NodeCheckbox(String text, Direction boxPosition) {
		this(text, boxPosition, false);
	}
	public NodeCheckbox(String text, Direction boxPosition, boolean startSelected) {
		nodeText = new NodeText(text);
		nodeMark = new NodeText("");
		nodeMark.setBorder(Decorator.getBorder(1));
		selectedProperty = new SimpleBooleanProperty();
		setSelected(startSelected);
		setSpacing(3);
		setAlignment(Pos.CENTER);
		
		if (boxPosition == Direction.LEFT) {
			getChildren().addAll(nodeMark, nodeText);
		} else {
			getChildren().addAll(nodeText, nodeMark);
		}
		addEventFilter(MouseEvent.MOUSE_PRESSED, event -> setSelected(!isSelected()));
		
		/* Silly problems require silly workarounds */
		EventHandler<WindowEvent> eventHandler = event -> {
			setSelected(!isSelected());
			setSelected(!isSelected());
		};
		this.sceneProperty().addListener((observable, oldScene, newScene) -> {
			if (newScene.getWindow() == null) {
				newScene.windowProperty().addListener((observable1, oldStage, newStage) -> {
					if (newStage != null) newStage.addEventFilter(WindowEvent.WINDOW_SHOWN, eventHandler);
				});
			} else {
				newScene.getWindow().addEventFilter(WindowEvent.WINDOW_SHOWN, eventHandler);
			}
		});
	}
	
	public boolean isSelected() {
		return selectedProperty.getValue();
	}
	
	public void setText(String text) {
		nodeText.setText(text);
	}
	public void setSelected(boolean selected) {
		selectedProperty.setValue(selected);
		if (selected) nodeMark.setText("✕");
		else nodeMark.setText("");
	}
	public SimpleBooleanProperty getSelectedProperty() {
		return selectedProperty;
	}
}
