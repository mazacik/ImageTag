package userinterface.nodes.node;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.util.Duration;
import main.InstanceManager;
import userinterface.nodes.base.TextNode;
import userinterface.style.enums.ColorType;

public class Tooltip extends Popup {
	private Timeline timeline;
	
	private double eventX;
	private double eventY;
	
	public Tooltip(String text, long delay) {
		TextNode textNode = new TextNode(text, ColorType.DEF, ColorType.DEF);
		this.getContent().add(textNode);
		//this.setAutoHide(true);
		
		timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(new Duration(delay), event -> {
			this.show(InstanceManager.getMainStage());
			//todo off-screen checks
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
