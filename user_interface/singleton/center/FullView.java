package user_interface.singleton.center;

import database.object.DataObject;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.singleton.BaseNode;

public class FullView extends BorderPane implements BaseNode, InstanceRepo {
    private DataObject currentDataObject = null;
    private Image currentPreviewImage = null;
    private final Canvas canvas = new Canvas();

    public FullView() {
        canvas.widthProperty().bind(tileView.widthProperty());
        canvas.heightProperty().bind(tileView.heightProperty());

        this.setBorder(NodeFactory.getBorder(0, 1, 0, 1));
        this.setCenter(canvas);
    }

    public void reload() {
        if (!CommonUtil.isFullView()) return;

        DataObject currentTarget = target.getCurrentTarget();
        if (currentTarget == null) return;
        if (currentDataObject == null || !currentDataObject.equals(currentTarget)) {
            currentDataObject = currentTarget;
            currentPreviewImage = new Image("file:" + settings.getCurrentDirectory() + "\\" + currentTarget.getName());
        }

        double imageWidth = currentPreviewImage.getWidth();
        double imageHeight = currentPreviewImage.getHeight();
        double maxWidth = canvas.getWidth();
        double maxHeight = canvas.getHeight();

        boolean upScale = false;

        double resultWidth;
        double resultHeight;

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
        gc.drawImage(currentPreviewImage, resultX, resultY, resultWidth, resultHeight);
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
