package user_interface.singleton.center;

import database.object.DataObject;
import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.util.Duration;
import system.CommonUtil;
import system.Instances;
import user_interface.factory.NodeUtil;
import user_interface.singleton.BaseNode;

public class MediaView extends BorderPane implements BaseNode, Instances {
    private Image currentImage = null;
    private final Canvas canvas = new Canvas();
    private final ImageView gifPlayer = new ImageView();
    private final VideoPlayer videoPlayer = new VideoPlayer(canvas);
    private final MediaViewControls controls = new MediaViewControls(videoPlayer);
    private final Popup controlsPopup = new Popup();
    private final PauseTransition controlsPopupDelay = new PauseTransition(new Duration(1000));

    private DataObject currentCache = null;

    public MediaView() {
        setupControls();

        gifPlayer.fitWidthProperty().bind(tileView.widthProperty());
        gifPlayer.fitHeightProperty().bind(tileView.heightProperty());

        this.setBorder(NodeUtil.getBorder(0, 1, 0, 1));
        this.setCenter(canvas);
    }
    public Canvas getCanvas() {
        return canvas;
    }
    public VideoPlayer getVideoPlayer() {
        return videoPlayer;
    }
    public MediaViewControls getControls() {
        return controls;
    }
    private void setupControls() {
        controls.prefWidthProperty().bind(canvas.widthProperty());

        BooleanProperty mouseMoving = new SimpleBooleanProperty();
        mouseMoving.addListener((obs, wasMoving, isNowMoving) -> {
            if (!isNowMoving) {
                controlsPopup.hide();
            }
        });

        controlsPopupDelay.setOnFinished(e -> mouseMoving.set(false));
        canvas.setOnMouseMoved(event -> {
            mouseMoving.set(true);
            controlsPopupDelay.playFromStart();

            double x = canvas.localToScreen(canvas.getBoundsInLocal()).getMinX();
            double y = canvas.localToScreen(canvas.getBoundsInLocal()).getMinY();

            controlsPopup.show(this, x, y);
        });

        controls.setOnMouseEntered(event -> controlsPopupDelay.stop());
        controls.setOnMouseExited(event -> controlsPopupDelay.playFromStart());

        controlsPopup.getContent().setAll(controls);
    }

    public boolean reload() {
        DataObject currentTarget = target.getCurrentTarget();
        if (CommonUtil.isCenterFullscreen() && currentTarget != null) {
            switch (currentTarget.getFileType()) {
                case IMAGE:
                    controls.setVideoMode(false);
                    reloadAsImage(currentTarget);
                    break;
                case GIF:
                    controls.setVideoMode(false);
                    reloadAsGif(currentTarget);
                    break;
                case VIDEO:
                    controls.setVideoMode(true);
                    reloadAsVideo(currentTarget);
                    break;
            }
            return true;
        }

        return false;
    }

    private void reloadAsImage(DataObject currentTarget) {
        if (this.getCenter() != canvas) this.setCenter(canvas);

        if (currentCache == null || !currentCache.equals(currentTarget)) {
            currentCache = currentTarget;
            currentImage = new Image("file:" + currentCache.getSourcePath());
        }

        double imageWidth = currentImage.getWidth();
        double imageHeight = currentImage.getHeight();
        double maxWidth = canvas.getWidth();
        double maxHeight = canvas.getHeight();

        boolean upScale = true;

        double resultWidth;
        double resultHeight;

        //noinspection ConstantConditions
        if (!upScale && imageWidth < maxWidth && imageHeight < maxHeight) {
            // image is smaller than canvas, upscaling is off
            resultWidth = imageWidth;
            resultHeight = imageHeight;
        } else {
            // scale image to fit width
            resultWidth = maxWidth;
            resultHeight = imageHeight * maxWidth / imageWidth;

            // if scaled image is too tall, scale to fit height instead
            if (resultHeight > maxHeight) {
                resultHeight = maxHeight;
                resultWidth = imageWidth * maxHeight / imageHeight;
            }
        }

        double resultX = maxWidth / 2 - resultWidth / 2;
        double resultY = maxHeight / 2 - resultHeight / 2;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(currentImage, resultX, resultY, resultWidth, resultHeight);
    }
    private void reloadAsGif(DataObject currentTarget) {
        if (this.getCenter() != gifPlayer) this.setCenter(gifPlayer);

        if (currentCache == null || !currentCache.equals(currentTarget)) {
            currentCache = currentTarget;
            currentImage = new Image("file:" + currentCache.getSourcePath());
        }

        gifPlayer.setImage(currentImage);
    }
    private void reloadAsVideo(DataObject currentTarget) {
        if (this.getCenter() != canvas) this.setCenter(canvas);

        if (currentCache == null || !currentCache.equals(currentTarget)) {
            currentCache = currentTarget;
            videoPlayer.start(currentTarget.getSourcePath());
        } else {
            videoPlayer.resume();
        }
    }

    public Popup getControlsPopup() {
        return controlsPopup;
    }
    public PauseTransition getControlsPopupDelay() {
        return controlsPopupDelay;
    }
}
