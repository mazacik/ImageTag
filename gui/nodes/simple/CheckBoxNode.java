package application.gui.nodes.simple;

import application.gui.decorator.SizeUtil;
import application.gui.decorator.enums.ColorType;
import application.gui.nodes.NodeUtil;
import application.misc.enums.Direction;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class CheckBoxNode extends Pane {
	private final TextNode nodeMark;
	private final TextNode nodeText;
	
	private SimpleBooleanProperty selectedProperty;
	
	public CheckBoxNode(String text) {
		this(text, Direction.LEFT, false);
	}
	public CheckBoxNode(String text, Direction boxPosition) {
		this(text, boxPosition, false);
	}
	public CheckBoxNode(String text, Direction boxPosition, boolean selected) {
		this.nodeText = new TextNode(text, ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.DEF);
		this.nodeMark = new TextNode("", ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.ALT);
		
		this.selectedProperty = new SimpleBooleanProperty();
		this.setSelected(selected);
		
		Insets insets = new Insets(-1, 2, -1, 2);
		nodeText.setPadding(insets);
		nodeMark.setPadding(insets);
		nodeMark.setBorder(NodeUtil.getBorder(1));
		nodeMark.heightProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> nodeMark.setMinWidth(nodeMark.getHeight())));
		
		HBox hBox = NodeUtil.getHBox(ColorType.DEF, ColorType.DEF);
		if (boxPosition == Direction.LEFT) {
			hBox.getChildren().addAll(nodeMark, nodeText);
		} else {
			hBox.getChildren().addAll(nodeText, nodeMark);
		}
		
		hBox.addEventFilter(MouseEvent.MOUSE_PRESSED, (EventHandler<Event>) event -> setSelected(!isSelected()));
		hBox.setSpacing(SizeUtil.getGlobalSpacing());
		
		this.getChildren().add(hBox);
	}
	
	public boolean isSelected() {
		return selectedProperty.getValue();
	}
	
	public void setTextFill(Color color) {
		nodeMark.setTextFill(color);
		nodeText.setTextFill(color);
	}
	public void setText(String text) {
		nodeText.setText(text);
	}
	public void setSelected(boolean selected) {
		selectedProperty.setValue(selected);
		
		if (selected) {
			nodeMark.setText("✕");
		} else {
			nodeMark.setText("");
		}
	}
	public SimpleBooleanProperty getSelectedProperty() {
		return selectedProperty;
	}
}
