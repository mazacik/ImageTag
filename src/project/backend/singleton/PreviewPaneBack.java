package project.backend.singleton;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import project.backend.common.Selection;
import project.backend.common.Settings;
import project.backend.database.DatabaseItem;
import project.frontend.Frontend;
import project.frontend.singleton.PreviewPaneFront;

public class PreviewPaneBack {
    private static PreviewPaneBack instance = new PreviewPaneBack();

    private static Canvas canvas = PreviewPaneFront.getInstance().getCanvas();
    private static GraphicsContext gc = canvas.getGraphicsContext2D();
    private static DatabaseItem currentDatabaseItem = null;
    private static Image currentPreviewImage = null;


    public void draw() {
        if (!Frontend.isPreviewFullscreen()) return;
        if (Selection.getFocusedItem() == null) return;
        if (!Selection.getFocusedItem().equals(currentDatabaseItem)) loadImage();
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

    private void loadImage() {
        currentDatabaseItem = Selection.getFocusedItem();
        currentPreviewImage = new Image("file:" + Settings.getMainDirectoryPath() + "/" + Selection.getFocusedItem().getName());
    }

    public static PreviewPaneBack getInstance() {
        return instance;
    }
}
