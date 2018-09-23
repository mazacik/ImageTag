package project.gui.event.gallerypane;

import javafx.scene.input.MouseEvent;
import project.MainUtils;
import project.database.object.DataObject;
import project.gui.component.gallerypane.GalleryTile;

public class GalleryTileEvent implements MainUtils {
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
        focusControl.setFocus(dataObject);
        selectionControl.swapSelectionStateOf(dataObject);
        customStage.getDataObjectContextMenu().hide();
    }
    private void onRightClick(GalleryTile sender, MouseEvent event) {
        DataObject dataObject = sender.getParentDataObject();
        focusControl.setFocus(dataObject);
        selectionControl.addDataObject(dataObject);
        customStage.getDataObjectContextMenu().show(sender, event);
    }
}
