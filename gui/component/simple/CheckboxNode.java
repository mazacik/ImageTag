package gui.component.simple;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import tools.NodeUtil;
import tools.enums.Direction;

public class CheckboxNode extends HBox {
	private final TextNode nodeMark;
	private final TextNode nodeText;
	
	private SimpleBooleanProperty selectedProperty;
	
	public CheckboxNode(String text) {
		this(text, Direction.LEFT, false);
	}
	public CheckboxNode(String text, Direction boxPosition) {
		this(text, boxPosition, false);
	}
	public CheckboxNode(String text, Direction boxPosition, boolean startSelected) {
		nodeText = new TextNode(text);
		nodeMark = new TextNode("");
		nodeMark.setBorder(NodeUtil.getBorder(1));
		selectedProperty = new SimpleBooleanProperty();
		setSelected(startSelected);
		setSpacing(3);
		setAlignment(Pos.CENTER);
		
		if (boxPosition == Direction.LEFT) {
			getChildren().addAll(nodeMark, nodeText);
		} else {
			getChildren().addAll(nodeText, nodeMark);
		}
		addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			setSelected(!isSelected());
		});
		
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
