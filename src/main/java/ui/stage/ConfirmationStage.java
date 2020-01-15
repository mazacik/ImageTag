package ui.stage;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.node.TextNode;

public class ConfirmationStage extends AbstractStage {
	private static final TextNode labelContent;
	private static final TextNode nodeYes;
	private static final TextNode nodeNo;
	
	private static boolean result;
	
	static {
		labelContent = new TextNode("", false, false, false, false);
		
		nodeYes = new TextNode("Yes", true, true, false, true);
		nodeYes.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = true;
			getInstance().close();
		});
		
		nodeNo = new TextNode("No", true, true, false, true);
		nodeNo.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = false;
			getInstance().close();
		});
	}
	
	public static Boolean show(String... args) {
		result = false;
		labelContent.setText(args[0]);
		
		getInstance().showAndWait();
		
		return result;
	}
	
	private ConfirmationStage() {
		super("Confirmation", false);
		setRoot(labelContent);
		setButtons(nodeYes, nodeNo);
	}
	private static class Loader {
		private static final ConfirmationStage INSTANCE = new ConfirmationStage();
	}
	public static ConfirmationStage getInstance() {
		return Loader.INSTANCE;
	}
}
