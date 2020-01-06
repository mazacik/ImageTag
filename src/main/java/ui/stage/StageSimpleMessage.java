package ui.stage;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.node.NodeText;

public class StageSimpleMessage extends StageBase {
	private static final NodeText nodeMessage;
	private static final NodeText nodeOK;
	
	static {
		nodeMessage = new NodeText("", false, false, false, true);
		
		nodeOK = new NodeText("OK", true, true, false, true);
		nodeOK.addMouseEvent(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY, getInstance()::close);
	}
	
	public static void show(String... args) {
		if (args.length > 0) {
			if (args.length == 1) {
				getInstance().setTitle("Error");
				nodeMessage.setText(args[0]);
			} else {
				getInstance().setTitle(args[0]);
				nodeMessage.setText(args[1]);
			}
			getInstance().showAndWait();
		}
	}
	
	private StageSimpleMessage() {
		super("", false, true, true);
		
		setRoot(nodeMessage);
		setButtons(nodeOK);
	}
	private static class Loader {
		private static final StageSimpleMessage INSTANCE = new StageSimpleMessage();
	}
	public static StageSimpleMessage getInstance() {
		return Loader.INSTANCE;
	}
}
