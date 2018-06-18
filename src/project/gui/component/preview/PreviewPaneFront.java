package project.gui.component.preview;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

public class PreviewPaneFront extends Pane {
    /* lazy singleton */
    private static PreviewPaneFront instance;
    public static PreviewPaneFront getInstance() {
        if (instance == null) instance = new PreviewPaneFront();
        return instance;
    }

    /* variables */
    private final Canvas canvas = new Canvas();

    /* constructors */
    private PreviewPaneFront() {
        canvas.setOnMouseClicked(event -> this.requestFocus());
        getChildren().add(canvas);
    }

    /* getters */
    public Canvas getCanvas() {
        return canvas;
    }

    /* setters */
    public void setCanvasSize(double width, double height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
    }
}
