package client.ui.stage;

import client.ui.custom.textnode.TextNode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class SimpleMessageStage extends AbstractStageBase {
	private TextNode nodeMessage;
	
	public SimpleMessageStage(String title, String message) {
		super("", false);
		
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
