package userinterface.nodes.node;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import userinterface.nodes.ColorData;
import userinterface.nodes.NodeUtil;
import userinterface.nodes.base.TextNode;
import userinterface.style.enums.ColorType;

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
		ColorData colorData = new ColorData(ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.DEF);
		
		node1 = new TextNode(text1, colorData);
		node2 = new TextNode(text2, colorData);
		
		node1.setBorder(NodeUtil.getBorder(1, 1, 1, 1));
		
		if (prefWidth >= 0) {
			node1.setPrefWidth(prefWidth / 2);
			node2.setPrefWidth(prefWidth / 2);
			this.setMaxWidth(prefWidth);
		}
		
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
}
