package project.backend.components;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import project.backend.shared.Database;
import project.backend.shared.DatabaseItem;
import project.frontend.shared.Frontend;

public class GalleryPaneBack {
    public void imageViewClicked(DatabaseItem databaseItem, MouseEvent event) {
        /* assigned in DatabaseItem.setImageView() */
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            Database.setSelectedItem(databaseItem);
            if (!Database.getSelectedItems().contains(databaseItem))
                Database.addToSelection(databaseItem);
            else
                Database.removeIndexFromSelection(databaseItem);
        }
    }

    public void refreshContent() {
        Frontend.getGalleryPane().getTilePane().getChildren().clear();
        for (DatabaseItem item : Database.getFilteredItems()) {
            Frontend.getGalleryPane().getTilePane().getChildren().add(item.getImageView());
        }
    }
}
