package userinterface.node.center;

import utils.MainUtil;

public class TileViewEvent implements MainUtil {
    public TileViewEvent() {
        onWidthChange();
    }

    private void onWidthChange() {
        tileView.getTilePane().widthProperty().addListener((observable, oldValue, newValue) -> tileView.calculateTilePaneHGap());
    }
}
