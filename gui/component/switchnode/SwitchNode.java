package application.gui.component.switchnode;

import application.gui.component.simple.TextNode;
import application.tools.NodeUtil;
import javafx.geometry.Pos;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class SwitchNode extends HBox {
	protected TextNode node1;
	protected TextNode node2;
	
	public SwitchNode() {
		this("", "");
	}
	public SwitchNode(String text1, String text2) {
		this(text1, text2, -1);
	}
	public SwitchNode(String text1, String text2, double prefWidth) {
		node1 = new TextNode(text1, true, false, true, true);
		node2 = new TextNode(text2, true, false, true, true);
		
		if (prefWidth >= 0) {
			node1.setPrefWidth(prefWidth / 2);
			node2.setPrefWidth(prefWidth / 2);
			this.setMaxWidth(prefWidth);
		}
		
		node1.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> setSelectedNode(SwitchNodeEnum.LEFT));
		node2.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> setSelectedNode(SwitchNodeEnum.RIGHT));
		
		this.getChildren().add(node1);
		this.getChildren().add(node2);
		this.setAlignment(Pos.CENTER);
	}
	
	public TextNode getNode1() {
		return node1;
	}
	public TextNode getNode2() {
		return node2;
	}
	
	public void setSelectedNode(SwitchNodeEnum node) {
		if (node == SwitchNodeEnum.LEFT) {
			node1.setBorder(NodeUtil.getBorder(1));
			node2.setBorder(null);
		} else {
			node1.setBorder(null);
			node2.setBorder(NodeUtil.getBorder(1));
		}
	}
	
	public enum SwitchNodeEnum {
		LEFT,
		RIGHT
	}
}
