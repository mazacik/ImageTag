package gui.event.gallerypane;

import database.object.DataObject;
import gui.node.gallerypane.GalleryTile;
import javafx.scene.input.MouseEvent;
import utils.MainUtil;

public class GalleryTileEvent implements MainUtil {
    public GalleryTileEvent(GalleryTile gallerytile) {
        onMouseClick(gallerytile);
    }

    private void onMouseClick(GalleryTile galleryTile) {
        galleryTile.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    onLeftClick(galleryTile);
                    break;
                case SECONDARY:
                    onRightClick(galleryTile, event);
                    break;
                default:
                    break;
            }
        });
    }
    private void onLeftClick(GalleryTile sender) {
        DataObject dataObject = sender.getParentDataObject();
        focus.set(dataObject);
        selection.swapState(dataObject);
        customStage.getDataObjectContextMenu().hide();
        reload.doReload();
    }
    private void onRightClick(GalleryTile sender, MouseEvent event) {
        DataObject dataObject = sender.getParentDataObject();
        focus.set(dataObject);
        selection.add(dataObject);
        customStage.getDataObjectContextMenu().show(sender, event);
    }
}
