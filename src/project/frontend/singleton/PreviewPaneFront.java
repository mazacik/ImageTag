package project.frontend.singleton;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

public class PreviewPaneFront extends Pane {
    private static final PreviewPaneFront instance = new PreviewPaneFront();

    private final Canvas canvas = new Canvas();


    private PreviewPaneFront() {
        getChildren().add(canvas);
    }

    public void setCanvasSize(double width, double height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public static PreviewPaneFront getInstance() {
        return instance;
    }
}
