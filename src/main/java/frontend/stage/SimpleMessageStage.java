package frontend.stage;

import frontend.node.textnode.TextNode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class SimpleMessageStage extends BaseStage {
	private TextNode nodeMessage;
	
	public SimpleMessageStage(String title, String message) {
		super("");
		
		nodeMessage = new TextNode("", false, false, false, true);
		nodeMessage.setText(message);
		
		TextNode nodeOK = new TextNode("OK", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, this::close);
		
		this.setTitle(title);
		this.setRoot(nodeMessage);
		this.setButtons(nodeOK);
	}
	
	public void setText(String message) {
		nodeMessage.setText(message);
	}
}
