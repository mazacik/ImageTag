package application.gui.panes.center;

import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Popup;
import javafx.util.Duration;

public class MediaPaneControls extends Popup {
	private final MediaPaneControlsBase controlsBase;
	private final PauseTransition autoHideDelay;
	
	public MediaPaneControls(MediaPane mediaPane, VideoPlayer videoPlayer) {
		controlsBase = new MediaPaneControlsBase(videoPlayer);
		autoHideDelay = new PauseTransition(new Duration(1000));
		
		controlsBase.prefWidthProperty().bind(mediaPane.widthProperty());
		
		BooleanProperty mouseMoving = new SimpleBooleanProperty();
		mouseMoving.addListener((obs, wasMoving, isNowMoving) -> {
			if (!isNowMoving) {
				this.hide();
			}
		});
		
		autoHideDelay.setOnFinished(e -> mouseMoving.set(false));
		mediaPane.setOnMouseMoved(event -> {
			mouseMoving.set(true);
			autoHideDelay.playFromStart();
			
			double x = mediaPane.localToScreen(mediaPane.getBoundsInLocal()).getMinX();
			double y = mediaPane.localToScreen(mediaPane.getBoundsInLocal()).getMinY();
			
			this.show(mediaPane, x, y);
		});
		
		controlsBase.setOnMouseEntered(event -> autoHideDelay.stop());
		controlsBase.setOnMouseExited(event -> autoHideDelay.playFromStart());
		
		this.getContent().setAll(controlsBase);
	}
	
	public void setVideoMode(boolean enabled) {
		controlsBase.setVideoMode(enabled);
	}
	public void setVideoProgress(double value) {
		controlsBase.setVideoProgress(value);
	}
	
	public void setTimeCurrent(long time) {
		controlsBase.setTimeCurrent(time);
	}
	public void setTimeTotal(long time) {
		controlsBase.setTimeTotal(time);
	}
}
