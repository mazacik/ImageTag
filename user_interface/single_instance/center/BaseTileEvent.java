package user_interface.single_instance.center;

import control.reload.Reload;
import database.object.DataObject;
import javafx.scene.input.MouseEvent;
import settings.SettingsNamespace;
import system.InstanceRepo;

public class BaseTileEvent implements InstanceRepo {
    public BaseTileEvent(BaseTile gallerytile) {
        onMouseClick(gallerytile);
    }

    public static void onGroupButtonClick(DataObject dataObject) {
        if (!tileView.getExpandedGroups().contains(dataObject.getMergeID())) {
            tileView.getExpandedGroups().add(dataObject.getMergeID());
        } else {
            //noinspection RedundantCollectionOperation
            tileView.getExpandedGroups().remove(tileView.getExpandedGroups().indexOf(dataObject.getMergeID()));
        }
        for (DataObject dataObject1 : dataObject.getMergeGroup()) {
            dataObject1.generateTileEffect();
        }
        reload.notifyChangeIn(Reload.Control.DATA);
    }
    private void onMouseClick(BaseTile baseTile) {
        baseTile.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    onLeftClick(baseTile, event);
                    break;
                case SECONDARY:
                    onRightClick(baseTile, event);
                    break;
                default:
                    break;
            }
        });
    }
    private void onLeftClick(BaseTile baseTile, MouseEvent event) {
        DataObject dataObject = baseTile.getParentDataObject();

        int tileSize = userSettings.valueOf(SettingsNamespace.TILEVIEW_ICONSIZE);
        if (event.getX() > tileSize - BaseTile.getEffectGroupSize() && event.getY() < BaseTile.getEffectGroupSize()) {
            onGroupButtonClick(dataObject);
        } else {
            target.set(dataObject);
            select.swapState(dataObject);
        }
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
