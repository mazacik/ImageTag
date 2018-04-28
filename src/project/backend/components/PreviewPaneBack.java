package project.backend.components;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import project.backend.shared.Backend;
import project.backend.shared.Database;
import project.backend.shared.DatabaseItem;
import project.frontend.components.PreviewPaneFront;
import project.frontend.shared.Frontend;

public class PreviewPaneBack {
    private Canvas canvas = Frontend.getPreviewPane().getCanvas();
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private DatabaseItem currentDatabaseItem = null;
    private Image currentPreviewImage = null;

    public PreviewPaneBack() {
        addOnResizeListener();
    }

    private void addOnResizeListener() {
        PreviewPaneFront previewPaneFront = Frontend.getPreviewPane();
        ChangeListener<Number> previewPaneSizeListener =
                (observable, oldValue, newValue) -> {
                    previewPaneFront.setCanvasSize(previewPaneFront.getWidth(), previewPaneFront.getHeight());
                    Backend.getPreviewPane().drawPreview();
        };
        previewPaneFront.widthProperty().addListener(previewPaneSizeListener);
        previewPaneFront.heightProperty().addListener(previewPaneSizeListener);
    }

    public void drawPreview() {
        if (Database.getSelectedItem() == null) return;
        if (!Database.getSelectedItem().equals(currentDatabaseItem)) loadImage();
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

        gc.clearRect(0, 0, Frontend.getPreviewPane().getWidth(), Frontend.getPreviewPane().getHeight());
        gc.drawImage(currentPreviewImage, resultX, resultY, resultWidth, resultHeight);
    }

    private void loadImage() {
        currentDatabaseItem = Database.getSelectedItem();
        currentPreviewImage = new Image("file:" + Database.getSelectedItem().getFullPath());
    }
}
