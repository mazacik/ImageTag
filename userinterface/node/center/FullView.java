package userinterface.node.center;

import database.object.DataObject;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import settings.SettingsNamespace;
import userinterface.node.BaseNode;
import utils.MainUtil;

public class FullView extends Pane implements BaseNode, MainUtil {
    private final Canvas canvas = new Canvas();

    private DataObject currentDataObject = null;
    private Image currentPreviewImage = null;

    public FullView() {
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        this.setWidth(settings.valueOf(SettingsNamespace.MAINSCENE_WIDTH));
        this.setHeight(settings.valueOf(SettingsNamespace.MAINSCENE_HEIGHT));
        this.setPadding(new Insets(settings.valueOf(SettingsNamespace.GLOBAL_PADDING)));
        this.getChildren().add(canvas);
    }

    public void reload() {
        if (!isFullView()) return;

        DataObject currentFocus = target.getCurrentTarget();
        if (currentFocus == null) return;
        if (currentDataObject == null || !currentDataObject.equals(currentFocus)) {
            String url = "file:" + settings.getCurrentDirectory() + "\\" + target.getCurrentTarget().getName();
            currentPreviewImage = new Image(url);
            currentDataObject = currentFocus;
        }

        double imageWidth = currentPreviewImage.getWidth();
        double imageHeight = currentPreviewImage.getHeight();
        double maxWidth = canvas.getWidth();
        double maxHeight = canvas.getHeight();

        // scale image to fit width
        double resultWidth = maxWidth;
        double resultHeight = imageHeight * maxWidth / imageWidth;

        // if scaled image is too tall, scale to fit height instead
        if (resultHeight > maxHeight) {
            resultHeight = maxHeight;
            resultWidth = imageWidth * maxHeight / imageHeight;
        }

        double resultX = maxWidth / 2 - resultWidth / 2;
        double resultY = maxHeight / 2 - resultHeight / 2;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, this.getWidth(), this.getHeight());
        gc.drawImage(currentPreviewImage, resultX, resultY, resultWidth, resultHeight);
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
