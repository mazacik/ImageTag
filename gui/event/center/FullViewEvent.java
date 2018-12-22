package gui.event.center;

import gui.template.generic.DataObjectContextMenu;
import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import utils.MainUtil;

public class FullViewEvent implements MainUtil {
    public FullViewEvent() {
        onMouseClick();
        onResize();
    }

    private void onMouseClick() {
        final DataObjectContextMenu contextMenu = mainStage.getDataObjectContextMenu();
        fullView.getCanvas().setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    contextMenu.hide();
                    break;
                case SECONDARY:
                    contextMenu.show(fullView, event.getScreenX(), event.getScreenY());
                    break;
                default:
                    break;
            }
        });
    }
    private void onResize() {
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> fullView.reload();

        final Canvas canvas = fullView.getCanvas();
        canvas.widthProperty().addListener(previewPaneSizeListener);
        canvas.heightProperty().addListener(previewPaneSizeListener);
    }
}
