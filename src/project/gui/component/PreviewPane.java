package project.gui.component;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import project.control.FocusControl;
import project.database.element.DataElement;
import project.gui.GUIControl;
import project.helper.Settings;
import project.userinput.gui.UserInputPreviewPane;

public abstract class PreviewPane {
    /* components */
    private static final Pane _this = new Pane();
    private static final Canvas canvas = new Canvas();

    private static MenuItem menuCopy = new MenuItem("Copy Name");
    private static MenuItem menuDelete = new MenuItem("Delete Selection");
    private static ContextMenu contextMenu = new ContextMenu(menuCopy, menuDelete);

    /* vars */
    private static DataElement currentDataElement = null;
    private static Image currentPreviewImage = null;

    /* initialize */
    public static void initialize() {
        initializeComponents();
        initializeInstance();
        UserInputPreviewPane.initialize();
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
        if (!GUIControl.isPreviewFullscreen()) return;

        DataElement currentFocus = FocusControl.getCurrentFocus();
        if (currentFocus == null) return;
        if (currentDataElement == null || !currentDataElement.equals(currentFocus)) {
            loadImageOfCurrentFocus();
            currentDataElement = currentFocus;
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
    public static MenuItem getMenuCopy() {
        return menuCopy;
    }
    public static MenuItem getMenuDelete() {
        return menuDelete;
    }
    public static ContextMenu getContextMenu() {
        return contextMenu;
    }
    public static Region getInstance() {
        return _this;
    }
}
