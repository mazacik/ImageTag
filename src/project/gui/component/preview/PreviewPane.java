package project.gui.component.preview;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

public class PreviewPane extends Pane {
    /* variables */
    private final Canvas canvas = new Canvas();

    /* constructors */
    public PreviewPane() {
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
