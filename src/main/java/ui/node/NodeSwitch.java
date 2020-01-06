package ui.node;

import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.decorator.Decorator;
import ui.override.HBox;
import ui.override.VBox;

public class NodeSwitch extends VBox {
	protected NodeText left;
	protected NodeText right;
	
	public NodeSwitch(String title, String text1, String text2, double prefWidth) {
		left = new NodeText(text1, true, false, true, true);
		right = new NodeText(text2, true, false, true, true);
		
		left.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::selectLeft);
		right.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::selectRight);
		
		if (prefWidth >= 0) {
			left.setPrefWidth(prefWidth / 2);
			right.setPrefWidth(prefWidth / 2);
			this.setMaxWidth(prefWidth);
		}
		
		this.setAlignment(Pos.CENTER);
		this.getChildren().add(new NodeText(title));
		this.getChildren().add(new HBox(left, right));
	}
	
	public void selectLeft() {
		left.setBorder(Decorator.getBorder(1));
		right.setBorder(null);
	}
	public void selectRight() {
		left.setBorder(null);
		right.setBorder(Decorator.getBorder(1));
	}
	
	public NodeText getLeft() {
		return left;
	}
	public NodeText getRight() {
		return right;
	}
}
