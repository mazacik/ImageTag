package project.gui.component.previewpane;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import project.control.FocusControl;
import project.database.element.DataObject;
import project.gui.GUIUtils;
import project.gui.event.listener.previewpane.EventListenerPreviewPaneCanvas;
import project.settings.Settings;

public abstract class PreviewPane {
    /* components */
    private static final Pane _this = new Pane();
    private static final Canvas canvas = new Canvas();

    private static RightClickMenu contextMenu = new RightClickMenu();

    /* vars */
    private static DataObject currentDataObject = null;
    private static Image currentPreviewImage = null;

    /* initialize */
    public static void initialize() {
        initializeComponents();
        initializeInstance();
        EventListenerPreviewPaneCanvas.initialize();
    }
    private static void initializeComponents() {

    }
    private static void initializeInstance() {
        canvas.widthProperty().bind(_this.widthProperty());
        canvas.heightProperty().bind(_this.heightProperty());

        _this.getChildren().add(canvas);
    }

    /* public */
    public static void reload() {
        if (!GUIUtils.isPreviewFullscreen()) return;

        DataObject currentFocus = FocusControl.getCurrentFocus();
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

        gc.clearRect(0, 0, _this.getWidth(), _this.getHeight());
        gc.drawImage(currentPreviewImage, resultX, resultY, resultWidth, resultHeight);
    }

    /* private */
    private static void loadImageOfCurrentFocus() {
        String url = "file:" + Settings.getMainDirectoryPath() + "\\" + FocusControl.getCurrentFocus().getName();
        currentPreviewImage = new Image(url);
    }

    /* get */
    public static Canvas getCanvas() {
        return canvas;
    }
    public static ContextMenu getContextMenu() {
        return contextMenu;
    }
    public static Region getInstance() {
        return _this;
    }
}
