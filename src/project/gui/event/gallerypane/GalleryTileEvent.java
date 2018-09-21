package project.gui.event.gallerypane;

import javafx.scene.input.MouseEvent;
import project.control.MainControl;
import project.database.object.DataObject;
import project.gui.GUIInstance;
import project.gui.component.gallerypane.GalleryTile;

public abstract class GalleryTileEvent {
    public static void initialize(GalleryTile gallerytile) {
        onMouseClick(gallerytile);
    }

    private static void onMouseClick(GalleryTile galleryTile) {
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
    private static void onLeftClick(GalleryTile sender) {
        DataObject dataObject = sender.getParentDataObject();
        MainControl.getFocusControl().setFocus(dataObject);
        MainControl.getSelectionControl().swapSelectionStateOf(dataObject);
        GUIInstance.getDataObjectContextMenu().hide();
    }
    private static void onRightClick(GalleryTile sender, MouseEvent event) {
        DataObject dataObject = sender.getParentDataObject();
        MainControl.getFocusControl().setFocus(dataObject);
        MainControl.getSelectionControl().addDataObject(dataObject);
        GUIInstance.getDataObjectContextMenu().show(sender, event);
    }
}
