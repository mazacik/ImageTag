package project.backend.components;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import project.backend.shared.Database;
import project.backend.shared.DatabaseItem;
import project.frontend.shared.Frontend;

/**
 * Backend of a GUI component which displays every filtered item's image as a thumbnail in a grid.
 */
public class GalleryPaneBack {
    /**
     * Notifies the database of a gallery tile being clicked.
     *
     * @param databaseItem the database item whose gallery tile has been clicked
     * @param event        respective onMouseClicked event
     */
    public void imageViewClicked(DatabaseItem databaseItem, MouseEvent event) {
        /* assigned in DatabaseItem.setImageView() */
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            Database.setSelectedItem(databaseItem);
            if (!Database.getSelectedItems().contains(databaseItem))
                Database.addToSelection(databaseItem);
            else Database.removeIndexFromSelection(databaseItem);
        }
    }

    /**
     * Reloads the gallery content from the filtered database.
     */
    public void reloadContent() {
        Frontend.getGalleryPane().getTilePane().getChildren().clear();
        for (DatabaseItem item : Database.getFilteredItems()) {
            Frontend.getGalleryPane().getTilePane().getChildren().add(item.getImageView());
        }
    }
}
