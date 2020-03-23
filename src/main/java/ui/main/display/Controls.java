package ui.main.display;

import javafx.animation.PauseTransition;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Controls extends ControlsBase {
	private final PauseTransition autoHideDelay;
	
	public Controls(Pane ownerPane, VideoPlayer videoPlayer) {
		super(videoPlayer);
		
		autoHideDelay = new PauseTransition(new Duration(1000));
		autoHideDelay.setOnFinished(event -> {
			System.out.println("timer finished");
			this.setVisible(false);
		});
		
		ownerPane.setOnMouseMoved(event -> {
			System.out.println("entitypane mouse moved");
			autoHideDelay.playFromStart();
			this.setVisible(true);
		});
		
		this.prefWidthProperty().bind(ownerPane.widthProperty());
		this.setOnMouseEntered(event -> {
			System.out.println("ctrls mouse entered");
			autoHideDelay.stop();
		});
		this.setOnMouseExited(event -> {
			System.out.println("ctrls mouse exited");
			autoHideDelay.playFromStart();
		});
	}
}
