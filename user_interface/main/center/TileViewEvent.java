package user_interface.main.center;

import lifecycle.InstanceManager;

public class TileViewEvent {
    public TileViewEvent() {
        onMouseClick();
    }

    private void onMouseClick() {
        InstanceManager.getGalleryPane().setOnMouseClicked(event -> InstanceManager.getGalleryPane().requestFocus());
    }
}
