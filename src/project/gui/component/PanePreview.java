package project.gui.component;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import project.control.FocusControl;
import project.database.element.DataElement;
import project.gui.change.ChangeEventControl;
import project.gui.change.ChangeEventEnum;
import project.gui.change.ChangeEventListener;
import project.gui.control.GUIControl;
import project.gui.control.GUIStage;
import project.helper.Settings;

public class PanePreview extends Pane implements ChangeEventListener {
    /* components */
    private final Canvas canvas = new Canvas();

    /* vars */
    private DataElement currentDataElement = null;
    private Image currentPreviewImage = null;

    /* constructors */
    public PanePreview() {
        initializeComponents();
        initializeProperties();
    }

    /* initialize */
    private void initializeComponents() {
        canvas.setOnMouseClicked(event -> requestFocus());
    }
    private void initializeProperties() {
        getChildren().add(canvas);

        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> {
            setCanvasSize(getWidth(), getHeight());
            refreshComponent();
        };
        widthProperty().addListener(previewPaneSizeListener);
        heightProperty().addListener(previewPaneSizeListener);

        ChangeEventControl.subscribe(this, ChangeEventEnum.FOCUS);
    }

    /* public */
    public void refreshComponent() {
        if (!GUIControl.isPreviewFullscreen()) return;

        DataElement currentFocus = FocusControl.getCurrentFocus();

        if (currentFocus == null) return;
        if (!currentFocus.equals(currentDataElement)) loadImage();

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

    /* private */
    private void loadImage() {
        String url = "file:" + Settings.getMainDirectoryPath() + "\\" + FocusControl.getCurrentFocus().getName();
        currentPreviewImage = new Image(url);
    }

    /* set */
    public void setCanvasSize(double width, double height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
    }
}
