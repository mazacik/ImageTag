package user_interface.singleton.center;

import control.Reload;
import database.object.DataObject;
import javafx.scene.input.MouseEvent;
import lifecycle.InstanceManager;
import settings.SettingsEnum;

public class BaseTileEvent {
    public BaseTileEvent(BaseTile gallerytile) {
        onMouseClick(gallerytile);
    }

    public static void onGroupButtonClick(DataObject dataObject) {
        if (!InstanceManager.getGalleryPane().getExpandedGroups().contains(dataObject.getMergeID())) {
            InstanceManager.getGalleryPane().getExpandedGroups().add(dataObject.getMergeID());
        } else {
            //noinspection RedundantCollectionOperation
            InstanceManager.getGalleryPane().getExpandedGroups().remove(InstanceManager.getGalleryPane().getExpandedGroups().indexOf(dataObject.getMergeID()));
        }
        for (DataObject dataObject1 : dataObject.getMergeGroup()) {
            dataObject1.generateTileEffect();
        }
        InstanceManager.getReload().flag(Reload.Control.DATA);
    }
    private void onMouseClick(BaseTile baseTile) {
        baseTile.setOnMouseClicked(event -> {
            event.consume();
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

        int tileSize = InstanceManager.getSettings().intValueOf(SettingsEnum.THUMBSIZE);
        if (event.getX() > tileSize - BaseTile.getEffectGroupSize() && event.getY() < BaseTile.getEffectGroupSize()) {
            onGroupButtonClick(dataObject);
        } else {
            InstanceManager.getTarget().set(dataObject);
            InstanceManager.getSelect().swapState(dataObject);
        }
        InstanceManager.getReload().doReload();
        InstanceManager.getClickMenuData().hide();
    }
    private void onRightClick(BaseTile sender, MouseEvent event) {
        DataObject dataObject = sender.getParentDataObject();

        if (!InstanceManager.getSelect().contains(dataObject)) {
            InstanceManager.getSelect().add(dataObject);
        }

        InstanceManager.getTarget().set(dataObject);
        InstanceManager.getReload().doReload();
        InstanceManager.getClickMenuData().show(sender, event);
    }
}
