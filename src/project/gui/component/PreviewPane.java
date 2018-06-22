package project.gui.component;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import project.backend.Settings;
import project.database.part.DatabaseItem;
import project.gui.ChangeEvent;
import project.gui.ChangeNotificationHelper;
import project.gui.GUIController;
import project.gui.GUIStage;

import java.util.ArrayList;

public class PreviewPane extends Pane implements ChangeNotificationHelper {
    /* change listeners */
    private final ArrayList<ChangeNotificationHelper> changeListeners = new ArrayList<>();

    /* components */
    private final Canvas canvas = new Canvas();

    /* variables */
    private DatabaseItem currentDatabaseItem = null;
    private Image currentPreviewImage = null;

    /* constructors */
    public PreviewPane() {
        canvas.setOnMouseClicked(event -> this.requestFocus());
        getChildren().add(canvas);
        setSizePropertyListener();

        GUIController.subscribe(this, ChangeEvent.FOCUS);
    }

    /* public methods */
    public void refresh() {
        if (!GUIController.isPreviewFullscreen()) return;
        if (GUIStage.getGalleryPane().getCurrentFocusedItem() == null) return;
        if (!GUIStage.getGalleryPane().getCurrentFocusedItem().equals(currentDatabaseItem)) loadImage();

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

        gc.clearRect(0, 0, GUIStage.getPreviewPane().getWidth(), GUIStage.getPreviewPane().getHeight());
        gc.drawImage(currentPreviewImage, resultX, resultY, resultWidth, resultHeight);
    }

    /* private methods */
    private void loadImage() {
        currentDatabaseItem = GUIStage.getGalleryPane().getCurrentFocusedItem();
        currentPreviewImage = new Image("file:" + Settings.getMainDirectoryPath() + "/" + currentDatabaseItem.getName());
    }

    private void setSizePropertyListener() {
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> {
            setCanvasSize(getWidth(), getHeight());
            refresh();
        };
        widthProperty().addListener(previewPaneSizeListener);
        heightProperty().addListener(previewPaneSizeListener);
    }

    /* getters */
    public ArrayList<ChangeNotificationHelper> getChangeListeners() {
        return changeListeners;
    }

    /* setters */
    public void setCanvasSize(double width, double height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
    }
}
