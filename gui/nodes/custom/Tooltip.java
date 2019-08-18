package application.gui.nodes.custom;

import application.gui.nodes.simple.TextNode;
import application.gui.stage.Stages;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.util.Duration;

public class Tooltip extends Popup {
	private Timeline timeline;
	private double eventX;
	private double eventY;
	
	public Tooltip(String text, long delay) {
		TextNode TextNode = new TextNode(text);
		this.getContent().add(TextNode);
		
		timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(new Duration(delay), event -> {
			this.show(Stages.getMainStage());
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
	
	public void startTimeline(MouseEvent event) {
		if (!this.isShowing()) {
			eventX = event.getScreenX();
			eventY = event.getScreenY();
			timeline.playFromStart();
		}
	}
}
