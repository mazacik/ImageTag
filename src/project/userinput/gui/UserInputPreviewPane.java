package project.userinput.gui;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import project.control.ReloadControl;
import project.gui.component.PreviewPane;

public abstract class UserInputPreviewPane {
    public static void initialize() {
        setOnMouseClicked_canvas();
        setSizeListener_canvas();
    }

    private static void setOnMouseClicked_canvas() {
        PreviewPane.getCanvas().setOnMouseClicked(event -> PreviewPane.getInstance().requestFocus());
    }
    private static void setSizeListener_canvas() {
        Canvas canvas = PreviewPane.getCanvas();
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> {
            ReloadControl.requestReloadOf(true, PreviewPane.class);
        };
        canvas.widthProperty().addListener(previewPaneSizeListener);
        canvas.heightProperty().addListener(previewPaneSizeListener);
    }
}
