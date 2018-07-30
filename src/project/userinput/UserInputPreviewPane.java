package project.userinput;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import project.control.FocusControl;
import project.control.ReloadControl;
import project.control.SelectionControl;
import project.database.element.DataElement;
import project.gui.component.PreviewPane.PreviewPane;

public abstract class UserInputPreviewPane {
    public static void initialize() {
        setOnMouseClicked_canvas();
        setSizeListener_canvas();
        UserInputPreviewPaneContextMenu.initialize();
    }

    private static void setOnMouseClicked_canvas() {
        PreviewPane.getCanvas().setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                PreviewPane.getInstance().requestFocus();
                PreviewPane.getContextMenu().hide();
            } else if (event.getButton() == MouseButton.SECONDARY) {
                DataElement dataElement = FocusControl.getCurrentFocus();
                FocusControl.setFocus(dataElement);
                SelectionControl.addDataElement(dataElement);
                PreviewPane.getContextMenu().show(PreviewPane.getInstance(), event.getScreenX(), event.getScreenY());
            }
        });
    }

    private static void setSizeListener_canvas() {
        Canvas canvas = PreviewPane.getCanvas();
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> {
            ReloadControl.requestComponentReload(true, PreviewPane.class);
        };
        canvas.widthProperty().addListener(previewPaneSizeListener);
        canvas.heightProperty().addListener(previewPaneSizeListener);
    }
}
