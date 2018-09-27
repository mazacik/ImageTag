package project.gui.event.previewpane;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import project.MainUtil;
import project.gui.component.GUINode;
import project.gui.custom.generic.DataObjectContextMenu;

public abstract class PreviewPaneEvent implements MainUtil {
    public PreviewPaneEvent() {
        onMouseClick();
        onResize();
    }

    private static void onMouseClick() {
        final DataObjectContextMenu contextMenu = customStage.getDataObjectContextMenu();

        previewPane.getCanvas().setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    previewPane.requestFocus();
                    contextMenu.hide();
                    break;
                case SECONDARY:
                    selection.add(focus.getCurrentFocus());
                    contextMenu.show(previewPane, event.getScreenX(), event.getScreenY());
                    break;
                default:
                    break;
            }
        });
    }
    private static void onResize() {
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) ->
                reload.queue(true, GUINode.PREVIEWPANE);
        final Canvas canvas = previewPane.getCanvas();
        canvas.widthProperty().addListener(previewPaneSizeListener);
        canvas.heightProperty().addListener(previewPaneSizeListener);
    }
}
