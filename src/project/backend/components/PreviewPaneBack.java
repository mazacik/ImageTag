package project.backend.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import project.backend.Database;
import project.frontend.shared.Frontend;

public class PreviewPaneBack {
    private final GraphicsContext gc = Frontend.getPreviewPaneFront().getCanvas().getGraphicsContext2D();

    public void drawPreview() {
        Canvas canvas = Frontend.getPreviewPaneFront().getCanvas();
        gc.clearRect(0, 0, Frontend.getPreviewPaneFront().getWidth(), Frontend.getPreviewPaneFront().getHeight());
        if (Database.getLastSelectedItem() == null) return;
        Image image = new Image("file:" + Database.getLastSelectedItem().getFullPath());
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        double maxWidth = canvas.getWidth();
        double maxHeight = canvas.getHeight();
        double resultWidth;
        double resultHeight;

        // scale image to fit width
        resultWidth = maxWidth;
        resultHeight = imageHeight * maxWidth / imageWidth;
        // if scaled image is too tall, scale to fit height instead
        if (resultHeight > maxHeight) {
            resultHeight = maxHeight;
            resultWidth = imageWidth * maxHeight / imageHeight;
        }

        double resultX = canvas.getWidth() / 2 - resultWidth / 2;
        double resultY = canvas.getHeight() / 2 - resultHeight / 2;

        gc.drawImage(image, resultX, resultY, resultWidth, resultHeight);
    }
}
