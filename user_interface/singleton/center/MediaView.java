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
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.singleton.BaseNode;
import user_interface.singleton.center.VideoPlayer.MediaViewControls;
import user_interface.singleton.center.VideoPlayer.VideoPlayer;

public class MediaView extends BorderPane implements BaseNode, InstanceRepo {
    private Image currentImage = null;
    private final Canvas canvas = new Canvas();
    private final ImageView gifPlayer = new ImageView();
    private final VideoPlayer videoPlayer = new VideoPlayer(canvas);
    private final MediaViewControls controls = new MediaViewControls(videoPlayer);
    private final Popup controlsPopup = new Popup();
    private DataObject currentCache = null;

    public MediaView() {
        canvas.widthProperty().bind(tileView.widthProperty());
        canvas.heightProperty().bind(tileView.heightProperty());

        setupControls();

        gifPlayer.fitWidthProperty().bind(tileView.widthProperty());
        gifPlayer.fitHeightProperty().bind(tileView.heightProperty());

        this.setBorder(NodeFactory.getBorder(0, 1, 0, 1));
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
        PauseTransition timer = new PauseTransition(new Duration(1000));
        timer.setOnFinished(e -> mouseMoving.set(false));

        canvas.setOnMouseMoved(event -> {
            mouseMoving.set(true);
            timer.playFromStart();

            double x = canvas.localToScreen(canvas.getBoundsInLocal()).getMinX();
            double y = canvas.localToScreen(canvas.getBoundsInLocal()).getMinY();

            controlsPopup.show(this, x, y);
        });

        controls.setOnMouseEntered(event -> timer.stop());
        controls.setOnMouseExited(event -> timer.playFromStart());

        controlsPopup.getContent().setAll(controls);
        controlsPopup.setAutoHide(true);
        controlsPopup.setHideOnEscape(true);
    }

    public void reload() {
        if (!CommonUtil.isFullView()) return;

        DataObject currentTarget = target.getCurrentTarget();

        if (currentTarget != null) {
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
        }
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
}
