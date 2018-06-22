package project.gui.component;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import project.common.Settings;
import project.database.part.DatabaseItem;
import project.gui.*;

import java.util.ArrayList;

public class PanePreview extends Pane implements ChangeEventListener {
    /* change listeners */
    private final ArrayList<ChangeEventListener> changeListeners = new ArrayList<>();

    /* components */
    private final Canvas canvas = new Canvas();

    /* variables */
    private DatabaseItem currentDatabaseItem = null;
    private Image currentPreviewImage = null;

    /* constructors */
    public PanePreview() {
        canvas.setOnMouseClicked(event -> this.requestFocus());
        getChildren().add(canvas);
        setSizePropertyListener();

        ChangeEventControl.subscribe(this, ChangeEventEnum.FOCUS);
    }

    /* public methods */
    public void refresh() {
        if (!GUIUtility.isPreviewFullscreen()) return;
        if (GUIStage.getPaneGallery().getCurrentFocusedItem() == null) return;
        if (!GUIStage.getPaneGallery().getCurrentFocusedItem().equals(currentDatabaseItem)) loadImage();

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

        gc.clearRect(0, 0, GUIStage.getPanePreview().getWidth(), GUIStage.getPanePreview().getHeight());
        gc.drawImage(currentPreviewImage, resultX, resultY, resultWidth, resultHeight);
    }

    /* private methods */
    private void loadImage() {
        currentDatabaseItem = GUIStage.getPaneGallery().getCurrentFocusedItem();
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
    public ArrayList<ChangeEventListener> getChangeListeners() {
        return changeListeners;
    }

    /* setters */
    public void setCanvasSize(double width, double height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
    }
}
