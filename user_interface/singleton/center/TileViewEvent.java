package user_interface.singleton.center;

import system.Instances;

public class TileViewEvent implements Instances {
    public TileViewEvent() {
        onMouseClick();
    }

    private void onMouseClick() {
        tileView.setOnMouseClicked(event -> tileView.requestFocus());
    }
}
