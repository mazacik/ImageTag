package ui.main.display;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import ui.main.stage.MainStage;
import ui.override.Scene;

import java.util.concurrent.atomic.AtomicBoolean;

public class Controls extends Stage {
	private final ControlsBase controlsBase;
	private final PauseTransition autoHideDelay;
	
	public Controls(DisplayPane entityPane, VideoPlayer videoPlayer) {
		controlsBase = new ControlsBase(videoPlayer);
		autoHideDelay = new PauseTransition(new Duration(1000));
		
		controlsBase.prefWidthProperty().bind(entityPane.widthProperty());
		
		autoHideDelay.setOnFinished(e -> this.hide());
		
		MainStage.getInstance().xProperty().addListener((observable, oldValue, newValue) -> this.hide());
		MainStage.getInstance().yProperty().addListener((observable, oldValue, newValue) -> this.hide());
		
		AtomicBoolean initDone = new AtomicBoolean(false);
		AtomicBoolean initBeingDone = new AtomicBoolean(false);
		entityPane.setOnMouseMoved(event -> {
			Bounds boundsInScene = entityPane.localToScreen(entityPane.getBoundsInLocal());
			double x = boundsInScene.getMinX();
			double y = boundsInScene.getMinY();
			
			if (!initDone.get() && !initBeingDone.get()) {
				initBeingDone.set(true);
				setOpacity(0);
				this.setX(x);
				this.setY(y);
				this.show();
				Platform.runLater(() -> {
					setOpacity(1);
					this.hide();
					initDone.set(true);
				});
			} else if (initDone.get()) {
				autoHideDelay.playFromStart();
				this.setX(x);
				this.setY(y);
				this.show();
			}
		});
		
		controlsBase.setOnMouseEntered(event -> autoHideDelay.stop());
		controlsBase.setOnMouseExited(event -> autoHideDelay.playFromStart());
		
		this.initStyle(StageStyle.UNDECORATED);
		this.initOwner(MainStage.getInstance());
		this.setScene(new Scene(controlsBase));
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
