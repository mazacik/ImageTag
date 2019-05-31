package user_interface.singleton.center;

import javafx.beans.value.ChangeListener;
import system.Instances;

public class MediaViewEvent implements Instances {
    public MediaViewEvent() {
        onMouseClick();
        onResize();
    }

    private void onMouseClick() {
        mediaView.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    mediaView.requestFocus();
                    clickMenuData.hide();
                    break;
                case SECONDARY:
                    clickMenuData.show(mediaView, event.getScreenX(), event.getScreenY());
                    break;
                default:
                    break;
            }
        });
    }
    private void onResize() {
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> mediaView.reload();

        mediaView.getCanvas().widthProperty().addListener(previewPaneSizeListener);
        mediaView.getCanvas().heightProperty().addListener(previewPaneSizeListener);
    }
}
