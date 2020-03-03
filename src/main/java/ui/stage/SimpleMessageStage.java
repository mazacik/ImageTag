package ui.stage;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import ui.node.textnode.TextNode;

public class SimpleMessageStage extends AbstractStage {
	private static final TextNode nodeMessage;
	private static final TextNode nodeOK;
	
	static {
		nodeMessage = new TextNode("", false, false, false, true);
		
		nodeOK = new TextNode("OK", true, true, false, true);
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
	
	private SimpleMessageStage() {
		super("", false);
		
		setRoot(nodeMessage);
		setButtons(nodeOK);
	}
	private static class Loader {
		private static final SimpleMessageStage INSTANCE = new SimpleMessageStage();
	}
	public static SimpleMessageStage getInstance() {
		return Loader.INSTANCE;
	}
}
