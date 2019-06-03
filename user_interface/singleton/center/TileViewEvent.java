package user_interface.singleton.center;

import lifecycle.InstanceManager;

public class TileViewEvent {
    public TileViewEvent() {
        onMouseClick();
    }

    private void onMouseClick() {
        InstanceManager.getGalleryPane().setOnMouseClicked(event -> InstanceManager.getGalleryPane().requestFocus());
    }
}
