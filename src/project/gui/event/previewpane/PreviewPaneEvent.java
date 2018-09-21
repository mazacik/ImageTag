package project.gui.event.previewpane;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Region;
import project.control.Control;
import project.gui.GUIInstance;
import project.gui.component.GUINode;
import project.gui.component.previewpane.PreviewPane;
import project.gui.custom.generic.DataObjectContextMenu;

public abstract class PreviewPaneEvent {
    public static void initialize() {
        onMouseClick();
        onResize();
    }

    private static void onMouseClick() {
        final DataObjectContextMenu contextMenu = GUIInstance.getDataObjectContextMenu();
        final Region previewPane = PreviewPane.getInstance();

        PreviewPane.getCanvas().setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    previewPane.requestFocus();
                    contextMenu.hide();
                    break;
                case SECONDARY:
                    Control.getSelectionControl().addDataObject(Control.getFocusControl().getCurrentFocus());
                    contextMenu.show(previewPane, event.getScreenX(), event.getScreenY());
                    break;
                default:
                    break;
            }
        });
    }
    private static void onResize() {
        final Canvas canvas = PreviewPane.getCanvas();
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) ->
                Control.getReloadControl().reload(true, GUINode.PREVIEWPANE);
        canvas.widthProperty().addListener(previewPaneSizeListener);
        canvas.heightProperty().addListener(previewPaneSizeListener);
    }
}
