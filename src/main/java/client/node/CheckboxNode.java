package client.node;

import client.decorator.DecoratorUtil;
import client.node.override.HBox;
import client.node.textnode.TextNode;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;
import server.enums.Direction;

public class CheckboxNode extends HBox {
	private final TextNode nodeMark;
	private final TextNode textNode;
	
	private SimpleBooleanProperty checkedProperty;
	
	public CheckboxNode(String text) {
		this(text, Direction.LEFT, false);
	}
	public CheckboxNode(String text, boolean startChecked) {
		this(text, Direction.LEFT, startChecked);
	}
	public CheckboxNode(String text, Direction boxPosition, boolean startChecked) {
		textNode = new TextNode(text, false, false, false, false);
		nodeMark = new TextNode("", true, true, false, false);
		nodeMark.setBorder(DecoratorUtil.getBorder(1));
		checkedProperty = new SimpleBooleanProperty();
		setChecked(startChecked);
		setSpacing(5);
		
		if (boxPosition == Direction.LEFT) {
			getChildren().addAll(nodeMark, textNode);
		} else {
			getChildren().addAll(textNode, nodeMark);
		}
		addEventFilter(MouseEvent.MOUSE_PRESSED, event -> setChecked(!isChecked()));
		
		/* Silly problems require silly workarounds */
		EventHandler<WindowEvent> eventHandler = event -> {
			//todo properly fix this
			setChecked(!isChecked());
			setChecked(!isChecked());
		};
		this.sceneProperty().addListener((observable, oldScene, newScene) -> {
			if (newScene != null) {
				if (newScene.getWindow() != null) {
					newScene.getWindow().addEventFilter(WindowEvent.WINDOW_SHOWN, eventHandler);
				} else {
					newScene.windowProperty().addListener((observable1, oldStage, newStage) -> {
						if (newStage != null) newStage.addEventFilter(WindowEvent.WINDOW_SHOWN, eventHandler);
					});
				}
			}
		});
	}
	
	public boolean isChecked() {
		return checkedProperty.getValue();
	}
	
	public void setText(String text) {
		textNode.setText(text);
	}
	public void setChecked(boolean selected) {
		checkedProperty.setValue(selected);
		if (selected) nodeMark.setText("✕");
		else nodeMark.setText("");
	}
	public SimpleBooleanProperty checkedProperty() {
		return checkedProperty;
	}
}
