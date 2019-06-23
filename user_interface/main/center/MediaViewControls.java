package user_interface.main.center;

import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.canvas.Canvas;
import javafx.stage.Popup;
import javafx.util.Duration;

public class MediaViewControls extends Popup {
    private final MediaViewControlsBase controlsBase;
    private final PauseTransition autoHideDelay;

    public MediaViewControls(Canvas canvas, VideoPlayer videoPlayer) {
        controlsBase = new MediaViewControlsBase(videoPlayer);
        autoHideDelay = new PauseTransition(new Duration(1000));

        controlsBase.prefWidthProperty().bind(canvas.widthProperty());

        BooleanProperty mouseMoving = new SimpleBooleanProperty();
        mouseMoving.addListener((obs, wasMoving, isNowMoving) -> {
            if (!isNowMoving) {
                this.hide();
            }
        });

        autoHideDelay.setOnFinished(e -> mouseMoving.set(false));
        canvas.setOnMouseMoved(event -> {
            mouseMoving.set(true);
            autoHideDelay.playFromStart();

            double x = canvas.localToScreen(canvas.getBoundsInLocal()).getMinX();
            double y = canvas.localToScreen(canvas.getBoundsInLocal()).getMinY();

            this.show(canvas, x, y);
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
