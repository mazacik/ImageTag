package user_interface.event;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import system.InstanceRepo;

public class FullViewEvent implements InstanceRepo {
    public FullViewEvent() {
        onMouseClick();
        onResize();
    }

    private void onMouseClick() {
        mediaView.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    mediaView.requestFocus();
                    dataObjectRCM.hide();
                    break;
                case SECONDARY:
                    dataObjectRCM.show(mediaView, event.getScreenX(), event.getScreenY());
                    break;
                default:
                    break;
            }
        });
    }
    private void onResize() {
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> mediaView.reload();

        final Canvas canvas = mediaView.getCanvas();
        canvas.widthProperty().addListener(previewPaneSizeListener);
        canvas.heightProperty().addListener(previewPaneSizeListener);
    }
}
