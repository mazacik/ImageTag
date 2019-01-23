package userinterface.node.center;

import database.object.DataObject;
import javafx.scene.input.MouseEvent;
import utils.InstanceRepo;

public class BaseTileEvent implements InstanceRepo {
    public BaseTileEvent(BaseTile gallerytile) {
        onMouseClick(gallerytile);
    }

    private void onMouseClick(BaseTile baseTile) {
        baseTile.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    onLeftClick(baseTile);
                    break;
                case SECONDARY:
                    onRightClick(baseTile, event);
                    break;
                default:
                    break;
            }
        });
    }
    private void onLeftClick(BaseTile sender) {
        DataObject dataObject = sender.getParentDataObject();
        target.set(dataObject);
        select.swapState(dataObject);
        reload.doReload();
        mainStage.getDataContextMenu().hide();
    }
    private void onRightClick(BaseTile sender, MouseEvent event) {
        DataObject dataObject = sender.getParentDataObject();
        target.set(dataObject);
        select.set(dataObject);
        mainStage.getDataContextMenu().show(sender, event);
    }
}
