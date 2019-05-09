package user_interface.singleton.center;

import database.object.DataObject;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaView;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.singleton.BaseNode;

public class FullView extends BorderPane implements BaseNode, InstanceRepo {
    private DataObject currentDataObject = null;
    private final ImageView imageView = new ImageView();
    private final Canvas canvas = new Canvas();
    private final MediaView mediaView = new MediaView();
    private Image currentImage = null;

    public FullView() {
        canvas.widthProperty().bind(tileView.widthProperty());
        canvas.heightProperty().bind(tileView.heightProperty());

        imageView.fitWidthProperty().bind(tileView.widthProperty());
        imageView.fitHeightProperty().bind(tileView.heightProperty());

        mediaView.fitWidthProperty().bind(tileView.widthProperty());
        mediaView.fitHeightProperty().bind(tileView.heightProperty());

        this.setBorder(NodeFactory.getBorder(0, 1, 0, 1));
        this.setCenter(canvas);
    }

    public void reload() {
        if (!CommonUtil.isFullView()) return;

        DataObject currentTarget = target.getCurrentTarget();
        if (currentTarget == null) return;

        switch (currentTarget.getFileType()) {
            case IMAGE:
                reloadAsImage(currentTarget);
                break;
            case GIF:
                reloadAsGif(currentTarget);
                break;
            case VIDEO:
                reloadAsVideo(currentTarget);
                break;
        }
    }

    private void reloadAsImage(DataObject dataObject) {
        if (currentDataObject == null || !currentDataObject.equals(dataObject)) {
            currentDataObject = dataObject;
            currentImage = new Image("file:" + settings.getCurrentDirectory() + dataObject.getName());
        }

        if (this.getCenter() != canvas) this.setCenter(canvas);

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
    private void reloadAsGif(DataObject dataObject) {
        if (currentDataObject == null || !currentDataObject.equals(dataObject)) {
            currentDataObject = dataObject;
            currentImage = new Image("file:" + settings.getCurrentDirectory() + dataObject.getName());
        }

        if (this.getCenter() != imageView) this.setCenter(imageView);

        imageView.setImage(currentImage);
    }
    private void reloadAsVideo(DataObject dataObject) {

    }

    public Canvas getCanvas() {
        return canvas;
    }
}
