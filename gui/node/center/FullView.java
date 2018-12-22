package gui.node.center;

import control.reload.Reload;
import database.object.DataObject;
import gui.node.BaseNode;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import settings.SettingsNamespace;
import utils.MainUtil;

public class FullView extends Pane implements MainUtil, BaseNode {
    private final Canvas canvas;

    private DataObject currentDataObject;
    private Image currentPreviewImage;

    public FullView() {
        canvas = new Canvas();
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        currentDataObject = null;
        currentPreviewImage = null;

        reload.subscribe(this, Reload.Control.FOCUS);

        this.setWidth(settings.valueOf(SettingsNamespace.MAINSCENE_WIDTH));
        this.setHeight(settings.valueOf(SettingsNamespace.MAINSCENE_HEIGHT));
        this.setPadding(new Insets(settings.valueOf(SettingsNamespace.GLOBAL_SPACING)));
        this.getChildren().add(canvas);
    }

    public void reload() {
        if (!isFullView()) return;

        DataObject currentFocus = target.getCurrentFocus();
        if (currentFocus == null) return;
        if (currentDataObject == null || !currentDataObject.equals(currentFocus)) {
            loadImageOfCurrentFocus();
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

    private void loadImageOfCurrentFocus() {
        String url = "file:" + settings.getCurrentDirectory() + "\\" + target.getCurrentFocus().getName();
        currentPreviewImage = new Image(url);
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
