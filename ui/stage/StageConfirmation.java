package ui.stage;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.node.NodeText;

public class StageConfirmation extends StageBase {
	private static final NodeText labelContent;
	private static final NodeText nodeYes;
	private static final NodeText nodeNo;
	
	private static boolean result;
	
	static {
		labelContent = new NodeText("", false, false, false, false);
		
		nodeYes = new NodeText("Yes", true, true, false, true);
		nodeYes.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, () -> {
			result = true;
			getInstance().close();
		});
		
		nodeNo = new NodeText("No", true, true, false, true);
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
	
	private StageConfirmation() {
		super("Confirmation", false, true, true);
		setRoot(labelContent);
		setButtons(nodeYes, nodeNo);
	}
	private static class Loader {
		private static final StageConfirmation INSTANCE = new StageConfirmation();
	}
	public static StageConfirmation getInstance() {
		return Loader.INSTANCE;
	}
}
