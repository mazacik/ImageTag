package application.gui.component.tooltip;

import application.gui.component.simple.TextNode;
import application.gui.stage.StageManager;
import application.tools.NodeUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.util.Duration;

public class Tooltip extends Popup {
	private TextNode textNode;
	
	private Timeline timeline;
	private double eventX;
	private double eventY;
	
	public Tooltip(String text, long delay) {
		textNode = new TextNode(text, false, false, false, true);
		textNode.setBorder(NodeUtil.getBorder(1));
		this.getContent().add(textNode);
		
		timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(new Duration(delay), event -> {
			this.show(StageManager.getMainStage());
			//needs off-screen checks
			this.setAnchorX(eventX - this.getWidth() / 2);
			this.setAnchorY(eventY + this.getHeight());
		}));
	}
	
	public static void install(Node node, Tooltip tooltip) {
		node.addEventFilter(MouseEvent.MOUSE_MOVED, tooltip::startTimeline);
		node.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			tooltip.timeline.stop();
			tooltip.hide();
		});
	}
	
	private void startTimeline(MouseEvent event) {
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
