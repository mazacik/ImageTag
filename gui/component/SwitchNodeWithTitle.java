package gui.component;

import gui.component.simple.TextNode;
import javafx.geometry.Pos;
import gui.component.simple.VBox;

public class SwitchNodeWithTitle extends VBox {
	private TextNode titleNode;
	private SwitchNode switchNode;
	
	public SwitchNodeWithTitle(String title, String text1, String text2) {
		this(title, text1, text2, -1);
	}
	public SwitchNodeWithTitle(String title, String text1, String text2, double prefWidth) {
		titleNode = new TextNode(title);
		switchNode = new SwitchNode(text1, text2, prefWidth);
		
		this.getChildren().add(titleNode);
		this.getChildren().add(switchNode);
		
		this.setAlignment(Pos.CENTER);
	}
	
	public void setSelectedNode(SwitchNode.SwitchNodeEnum node) {
		switchNode.setSelectedNode(node);
	}
	
	public TextNode getTitleNode() {
		return titleNode;
	}
	public SwitchNode getSwitchNode() {
		return switchNode;
	}
}
