package project.component.preview;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import project.common.Settings;
import project.common.Utility;
import project.component.gallery.GalleryPaneFront;
import project.database.DatabaseItem;

public class PreviewPaneBack {
    /* lazy singleton */
    private static PreviewPaneBack instance;
    public static PreviewPaneBack getInstance() {
        if (instance == null) instance = new PreviewPaneBack();
        return instance;
    }

    /* imports */
    private final Canvas canvas = PreviewPaneFront.getInstance().getCanvas();

    /* constructor */
    private PreviewPaneBack() {
        PreviewPaneListener.getInstance();
    }

    /* variables */
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private DatabaseItem currentDatabaseItem = null;
    private Image currentPreviewImage = null;

    /* public methods */
    public void reloadContent() {
        if (!Utility.isPreviewFullscreen()) return;
        if (GalleryPaneFront.getInstance().getCurrentFocusedItem() == null) return;
        if (!GalleryPaneFront.getInstance().getCurrentFocusedItem().equals(currentDatabaseItem)) loadImage();

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

        gc.clearRect(0, 0, PreviewPaneFront.getInstance().getWidth(), PreviewPaneFront.getInstance().getHeight());
        gc.drawImage(currentPreviewImage, resultX, resultY, resultWidth, resultHeight);
    }

    /* private methods */
    private void loadImage() {
        currentDatabaseItem = GalleryPaneFront.getInstance().getCurrentFocusedItem();
        currentPreviewImage = new Image("file:" + Settings.getMainDirectoryPath() + "/" + currentDatabaseItem.getName());
    }
}
