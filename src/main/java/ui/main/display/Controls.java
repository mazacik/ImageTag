package ui.main.display;

import javafx.animation.PauseTransition;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Controls extends ControlsBase {
	private final PauseTransition autoHideDelay;
	
	public Controls(Pane ownerPane, VideoPlayer videoPlayer) {
		super(videoPlayer);
		
		autoHideDelay = new PauseTransition(new Duration(1000));
		autoHideDelay.setOnFinished(event -> this.setVisible(false));
		
		ownerPane.setOnMouseMoved(event -> {
			autoHideDelay.playFromStart();
			this.setVisible(true);
		});
		
		this.prefWidthProperty().bind(ownerPane.widthProperty());
		this.setOnMouseEntered(event -> autoHideDelay.stop());
		this.setOnMouseExited(event -> autoHideDelay.playFromStart());
	}
}
