package userinterface.main.center;

import main.InstanceManager;

public class TileViewEvent {
    public TileViewEvent() {
        onMouseClick();
    }

    private void onMouseClick() {
        InstanceManager.getGalleryPane().setOnMouseClicked(event -> InstanceManager.getGalleryPane().requestFocus());
    }
}
