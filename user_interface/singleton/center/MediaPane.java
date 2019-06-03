package user_interface.singleton.center;

import database.object.DataObject;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import lifecycle.InstanceManager;
import system.CommonUtil;
import user_interface.factory.NodeUtil;
import user_interface.singleton.NodeBase;

public class MediaPane extends BorderPane implements NodeBase {
    private final Canvas canvas;
    private final ImageView gifPlayer;
    private final VideoPlayer videoPlayer;
    private final MediaViewControls controls;

    private Image currentImage = null;
    private DataObject currentCache = null;

    public MediaPane() {
        canvas = new Canvas();
        gifPlayer = new ImageView();
        videoPlayer = new VideoPlayer(canvas);
        controls = new MediaViewControls(canvas, videoPlayer);

        GalleryPane galleryPane = InstanceManager.getGalleryPane();

        gifPlayer.fitWidthProperty().bind(galleryPane.widthProperty());
        gifPlayer.fitHeightProperty().bind(galleryPane.heightProperty());

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

    public boolean reload() {
        DataObject currentTarget = InstanceManager.getTarget().getCurrentTarget();
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
            currentImage = new Image("file:" + currentCache.getPath());
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
            currentImage = new Image("file:" + currentCache.getPath());
        }

        gifPlayer.setImage(currentImage);
    }
    private void reloadAsVideo(DataObject currentTarget) {
        if (this.getCenter() != canvas) this.setCenter(canvas);

        if (currentCache == null || !currentCache.equals(currentTarget)) {
            currentCache = currentTarget;
            videoPlayer.start(currentTarget.getPath());
        } else {
            videoPlayer.resume();
        }
    }
}
