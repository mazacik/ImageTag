package project.backend.components;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import project.backend.common.Selection;
import project.backend.database.Database;
import project.backend.database.DatabaseItem;
import project.frontend.shared.Frontend;

public class GalleryPaneBack {
    public static void imageViewClicked(DatabaseItem databaseItem, MouseEvent event) {
        /* assigned in DatabaseItem.setImageView() */
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            Selection.setFocusedItem(databaseItem, true);
        }
    }

    public static void reloadContent() {
        ObservableList<Node> galleryTiles = Frontend.getGalleryPane().getTilePane().getChildren();
        galleryTiles.clear();
        for (DatabaseItem databaseItem : Database.getDatabaseItemsFiltered()) {
            galleryTiles.add(databaseItem.getImageView());
        }
    }
}
