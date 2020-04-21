package frontend.stage;

import frontend.node.textnode.TextNode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ConfirmationStage extends BaseStage {
	private boolean result;
	
	public ConfirmationStage(String message) {
		this("Confirmation", message);
	}
	public ConfirmationStage(String title, String message) {
		super(title, false);
		
		TextNode labelContent = new TextNode(message, false, false, false, true);
		
		TextNode nodeYes = new TextNode("Yes", true, true, false, true);
		nodeYes.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = true;
			this.close();
		});
		
		TextNode nodeNo = new TextNode("No", true, true, false, true);
		nodeNo.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = false;
			this.close();
		});
		
		this.setRoot(labelContent);
		this.setButtons(nodeYes, nodeNo);
	}
	
	public boolean getResult() {
		this.showAndWait();
		return result;
	}
}
