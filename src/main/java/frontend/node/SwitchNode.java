package frontend.node;

import frontend.decorator.DecoratorUtil;
import frontend.node.override.HBox;
import frontend.node.override.VBox;
import frontend.node.textnode.TextNode;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

//todo rework to work off of properties
public class SwitchNode extends VBox {
	protected TextNode left;
	protected TextNode right;
	
	public SwitchNode(String text1, String text2, double prefWidth) {
		this("", text1, text2, prefWidth);
	}
	public SwitchNode(String title, String text1, String text2, double prefWidth) {
		left = new TextNode(text1, true, false, true, true);
		right = new TextNode(text2, true, false, true, true);
		
		left.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::selectLeft);
		right.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::selectRight);
		
		if (prefWidth >= 0) {
			left.setPrefWidth(prefWidth / 2);
			right.setPrefWidth(prefWidth / 2);
			this.setMaxWidth(prefWidth);
		}
		
		this.setAlignment(Pos.CENTER);
		if (!title.isEmpty()) this.getChildren().add(new TextNode(title, false, false, false, false));
		this.getChildren().add(new HBox(left, right));
	}
	
	public void selectLeft() {
		left.setBorder(DecoratorUtil.getBorder(1));
		right.setBorder(null);
	}
	public void selectRight() {
		left.setBorder(null);
		right.setBorder(DecoratorUtil.getBorder(1));
	}
	
	public TextNode getLeft() {
		return left;
	}
	public TextNode getRight() {
		return right;
	}
}
