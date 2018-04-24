package project.frontend;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import project.backend.Database;

public class PreviewPane extends Pane {
    private static final Canvas canvas = new Canvas();
    private static final GraphicsContext gc = canvas.getGraphicsContext2D();

    PreviewPane() {
        getChildren().add(canvas);
    }

    public void setCanvasSize(double width, double height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
    }

    public void drawPreview() {
        gc.clearRect(0, 0, getWidth(), getHeight());
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
