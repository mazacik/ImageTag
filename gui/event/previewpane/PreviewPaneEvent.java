package gui.event.previewpane;

import gui.template.generic.DataObjectContextMenu;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import utils.MainUtil;

public class PreviewPaneEvent implements MainUtil {
    public PreviewPaneEvent() {
        onMouseClick();
        onResize();
    }

    private void onMouseClick() {
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
    private void onResize() {
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> previewPane.reload();

        final Canvas canvas = previewPane.getCanvas();
        canvas.widthProperty().addListener(previewPaneSizeListener);
        canvas.heightProperty().addListener(previewPaneSizeListener);
    }
}
