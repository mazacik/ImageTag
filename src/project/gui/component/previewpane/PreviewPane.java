package project.gui.component.previewpane;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import project.MainUtil;
import project.database.object.DataObject;
import project.settings.Settings;

public class PreviewPane extends Pane implements MainUtil {
    private final Canvas canvas;

    private DataObject currentDataObject;
    private Image currentPreviewImage;

    public PreviewPane() {
        currentDataObject = null;
        currentPreviewImage = null;

        canvas = new Canvas();
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        this.getChildren().add(canvas);
    }

    public void reload() {
        if (!isPreviewFullscreen()) return;

        DataObject currentFocus = focus.getCurrentFocus();
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

        double resultX = canvas.getWidth() / 2 - resultWidth / 2;
        double resultY = canvas.getHeight() / 2 - resultHeight / 2;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, this.getWidth(), this.getHeight());
        gc.drawImage(currentPreviewImage, resultX, resultY, resultWidth, resultHeight);
    }

    private void loadImageOfCurrentFocus() {
        String url = "file:" + Settings.getPath_source() + "\\" + focus.getCurrentFocus().getName();
        currentPreviewImage = new Image(url);
    }

    public Canvas getCanvas() {
        return canvas;
    }
}
