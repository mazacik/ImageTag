package user_interface.event;

import system.InstanceRepo;

public class TileViewEvent implements InstanceRepo {
    public TileViewEvent() {
        onMouseClick();
    }

    private void onMouseClick() {
        tileView.setOnMouseClicked(event -> tileView.requestFocus());
    }
}
