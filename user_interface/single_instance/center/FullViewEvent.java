package user_interface.single_instance.center;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import system.InstanceRepo;
import user_interface.node_factory.template.DataContextMenu;

public class FullViewEvent implements InstanceRepo {
    public FullViewEvent() {
        onMouseClick();
        onResize();
    }

    private void onMouseClick() {
        final DataContextMenu contextMenu = mainStage.getDataContextMenu();
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
