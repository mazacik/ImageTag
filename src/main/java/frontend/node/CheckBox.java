package frontend.node;

import frontend.decorator.DecoratorUtil;
import frontend.node.override.HBox;
import frontend.node.textnode.TextNode;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.MouseEvent;

public class CheckBox extends HBox {
	private static final double WIDTH = 25;
	
	private final TextNode nodeMark;
	private final TextNode textNode;
	
	public CheckBox(String text) {
		this(text, false);
	}
	public CheckBox(String text, boolean selected) {
		textNode = new TextNode(text, false, false, false, false);
		nodeMark = new TextNode("", true, true, false, false);
		nodeMark.setBorder(DecoratorUtil.getBorder(0, 1, 0, 1));
		
		selectedProperty = new SimpleBooleanProperty();
		selectedProperty.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				nodeMark.setText("✕");
			} else {
				nodeMark.setText("");
			}
		});
		
		this.setSelected(selected);
		this.setSpacing(5);
		this.getChildren().addAll(nodeMark, textNode);
		
		nodeMark.setPrefWidth(WIDTH);
		nodeMark.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> this.setSelected(!this.isSelected()));
	}
	
	public void setText(String text) {
		textNode.setText(text);
	}
	
	private final SimpleBooleanProperty selectedProperty;
	public SimpleBooleanProperty selectedProperty() {
		return selectedProperty;
	}
	public boolean isSelected() {
		return selectedProperty.getValue();
	}
	public void setSelected(boolean selected) {
		selectedProperty.setValue(selected);
	}
}
