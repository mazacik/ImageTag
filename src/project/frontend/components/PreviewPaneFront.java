package project.frontend.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

public class PreviewPaneFront extends Pane {
  private final Canvas canvas = new Canvas();

  public PreviewPaneFront() {
    getChildren().add(canvas);
  }

  public void setCanvasSize(double width, double height) {
    canvas.setWidth(width);
    canvas.setHeight(height);
  }

  public Canvas getCanvas() {
    return canvas;
  }
}
