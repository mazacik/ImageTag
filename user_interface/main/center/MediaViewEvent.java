package user_interface.main.center;

import javafx.beans.value.ChangeListener;
import lifecycle.InstanceManager;

public class MediaViewEvent {
    public MediaViewEvent() {
        onMouseClick();
        onResize();
    }

    private void onMouseClick() {
        InstanceManager.getMediaPane().setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    InstanceManager.getMediaPane().requestFocus();
                    InstanceManager.getClickMenuData().hide();
                    break;
                case SECONDARY:
                    InstanceManager.getClickMenuData().show(InstanceManager.getMediaPane(), event.getScreenX(), event.getScreenY());
                    break;
                default:
                    break;
            }
        });
    }
    private void onResize() {
        ChangeListener<Number> previewPaneSizeListener = (observable, oldValue, newValue) -> InstanceManager.getMediaPane().reload();

        InstanceManager.getMediaPane().getCanvas().widthProperty().addListener(previewPaneSizeListener);
        InstanceManager.getMediaPane().getCanvas().heightProperty().addListener(previewPaneSizeListener);
    }
}
