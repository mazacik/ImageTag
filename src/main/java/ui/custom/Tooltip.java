package ui.custom;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.util.Duration;
import ui.decorator.Decorator;
import ui.main.stage.StageMain;
import ui.node.NodeText;

public class Tooltip extends Popup {
	private NodeText nodeText;
	
	private Timeline timeline;
	private double eventX;
	private double eventY;
	
	public Tooltip(String text, long delay) {
		nodeText = new NodeText(text, false, false, false, true);
		nodeText.setBorder(Decorator.getBorder(1));
		nodeText.setBackground(Decorator.getBackgroundPrimary());
		Decorator.getNodeList().add(nodeText);
		this.getContent().add(nodeText);
		
		timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(new Duration(delay), event -> {
			this.show(StageMain.getInstance());
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
	
	public NodeText getNodeText() {
		return nodeText;
	}
}
