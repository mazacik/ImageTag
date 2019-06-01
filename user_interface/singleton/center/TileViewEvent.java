package user_interface.singleton.center;

import lifecycle.InstanceManager;

public class TileViewEvent {
    public TileViewEvent() {
        onMouseClick();
    }

    private void onMouseClick() {
        InstanceManager.getTileView().setOnMouseClicked(event -> InstanceManager.getTileView().requestFocus());
    }
}
