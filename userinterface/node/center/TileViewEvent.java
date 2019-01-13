package userinterface.node.center;

import utils.InstanceRepo;

public class TileViewEvent implements InstanceRepo {
    public TileViewEvent() {
        onWidthChange();
    }

    private void onWidthChange() {
        tileView.getTilePane().widthProperty().addListener((observable, oldValue, newValue) -> tileView.calculateTilePaneHGap());
    }
}
