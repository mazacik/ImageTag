package project.gui.component;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import project.control.FocusControl;
import project.control.ReloadControl;
import project.database.element.DataElement;
import project.gui.control.GUIControl;
import project.helper.Settings;

public abstract class PreviewPane {
    /* components */
    private static final Pane _this = new Pane();
    private static final Canvas canvas = new Canvas();

    /* vars */
    private static DataElement currentDataElement = null;
    private static Image currentPreviewImage = null;

    /* initialize */
    public static void initialize() {
        initializeComponents();
        initializeProperties();
    }
    private static void initializeComponents() {
        canvas.setOnMouseClicked(event -> _this.requestFocus());
    }
    private static void initializeProperties() {
        _this.getChildren().add(canvas);

        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> {
            PreviewPane.setCanvasSize(_this.getWidth(), _this.getHeight());
            ReloadControl.requestReloadOf(true, PreviewPane.class);
        };
        _this.widthProperty().addListener(previewPaneSizeListener);
        _this.heightProperty().addListener(previewPaneSizeListener);
    }

    /* public */
    public static void reload() {
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

        gc.clearRect(0, 0, _this.getWidth(), _this.getHeight());
        gc.drawImage(currentPreviewImage, resultX, resultY, resultWidth, resultHeight);
    }

    /* private */
    private static void loadImage() {
        String url = "file:" + Settings.getMainDirectoryPath() + "\\" + FocusControl.getCurrentFocus().getName();
        currentPreviewImage = new Image(url);
    }

    /* get */
    public static Region getInstance() {
        return _this;
    }

    /* set */
    public static void setCanvasSize(double width, double height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
    }
}
