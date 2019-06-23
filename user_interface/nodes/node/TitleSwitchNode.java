package user_interface.nodes.node;

import javafx.scene.layout.VBox;
import user_interface.nodes.base.TextNode;
import user_interface.style.enums.ColorType;

public class TitleSwitchNode extends VBox {
	private TextNode titleNode;
	private SwitchNode switchNode;
	
	public TitleSwitchNode(String title, String text1, String text2) {
		this(title, text1, text2, -1);
	}
	public TitleSwitchNode(String title, String text1, String text2, double prefWidth) {
		titleNode = new TextNode(title, ColorType.DEF, ColorType.DEF, ColorType.DEF, ColorType.DEF);
		switchNode = new SwitchNode(text1, text2, prefWidth);
		
		this.getChildren().add(titleNode);
		this.getChildren().add(switchNode);
	}
	
	public TextNode getTitleNode() {
		return titleNode;
	}
	public SwitchNode getSwitchNode() {
		return switchNode;
	}
}
