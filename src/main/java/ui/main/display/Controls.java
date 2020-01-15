package ui.main.display;

import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Popup;
import javafx.util.Duration;

public class Controls extends Popup {
	private final ControlsBase controlsBase;
	private final PauseTransition autoHideDelay;
	
	public Controls(DisplayPane entityPane, VideoPlayer videoPlayer) {
		controlsBase = new ControlsBase(videoPlayer);
		autoHideDelay = new PauseTransition(new Duration(1000));
		
		controlsBase.prefWidthProperty().bind(entityPane.widthProperty());
		
		BooleanProperty mouseMoving = new SimpleBooleanProperty();
		mouseMoving.addListener((obs, wasMoving, isNowMoving) -> {
			if (!isNowMoving) {
				this.hide();
			}
		});
		
		autoHideDelay.setOnFinished(e -> mouseMoving.set(false));
		entityPane.setOnMouseMoved(event -> {
			mouseMoving.set(true);
			autoHideDelay.playFromStart();
			
			double x = entityPane.localToScreen(entityPane.getBoundsInLocal()).getMinX();
			double y = entityPane.localToScreen(entityPane.getBoundsInLocal()).getMinY();
			
			this.show(entityPane, x, y);
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
