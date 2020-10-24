package frontend.node;

import frontend.UserInterface;
import frontend.decorator.DecoratorUtil;
import frontend.node.textnode.TextNode;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.util.Duration;

public class Tooltip extends Popup {
	private final TextNode textNode;
	
	private final Timeline timeline;
	private double eventX;
	private double eventY;
	
	public Tooltip(String text, long delay) {
		textNode = new TextNode(text, false, false, false, true);
		textNode.setBorder(DecoratorUtil.getBorder(1));
		textNode.setBackground(DecoratorUtil.getBackgroundDefault());
		DecoratorUtil.getNodeList().add(textNode);
		this.getContent().add(textNode);
		
		timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(new Duration(delay), event -> {
			this.show(UserInterface.getStage());
			//needs off-screen checks
			this.setAnchorX(eventX - this.getWidth() / 2);
			this.setAnchorY(eventY + this.getHeight());
		}));
	}
	
	public static void install(Node node, Tooltip tooltip) {
		node.addEventFilter(MouseEvent.MOUSE_MOVED, tooltip::startDelay);
		node.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			tooltip.timeline.stop();
			tooltip.hide();
		});
	}
	
	private void startDelay(MouseEvent event) {
		if (!this.isShowing()) {
			eventX = event.getScreenX();
			eventY = event.getScreenY();
			timeline.playFromStart();
		}
	}
	
	public TextNode getTextNode() {
		return textNode;
	}
}
