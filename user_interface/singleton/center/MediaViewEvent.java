package user_interface.singleton.center;

import javafx.beans.value.ChangeListener;
import lifecycle.InstanceManager;

public class MediaViewEvent {
    public MediaViewEvent() {
        onMouseClick();
        onResize();
    }

    private void onMouseClick() {
        InstanceManager.getMediaView().setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    InstanceManager.getMediaView().requestFocus();
                    InstanceManager.getClickMenuData().hide();
                    break;
                case SECONDARY:
                    InstanceManager.getClickMenuData().show(InstanceManager.getMediaView(), event.getScreenX(), event.getScreenY());
                    break;
                default:
                    break;
            }
        });
    }
    private void onResize() {
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> InstanceManager.getMediaView().reload();

        InstanceManager.getMediaView().getCanvas().widthProperty().addListener(previewPaneSizeListener);
        InstanceManager.getMediaView().getCanvas().heightProperty().addListener(previewPaneSizeListener);
    }
}
