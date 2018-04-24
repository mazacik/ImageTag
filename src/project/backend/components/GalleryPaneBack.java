package project.backend.components;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import project.backend.Database;
import project.backend.DatabaseItem;
import project.frontend.shared.Frontend;

public class GalleryPaneBack {
    public void imageViewClicked(DatabaseItem databaseItem, MouseEvent event) {
        /* assigned in DatabaseItem.setImageView() */
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            Database.setLastSelectedItem(databaseItem);
            if (!Database.getSelectedItems().contains(databaseItem))
                Database.addToSelection(databaseItem);
            else
                Database.removeIndexFromSelection(databaseItem);
        }
    }

    public void refreshContent() {
        Frontend.getGalleryPaneFront().getTilePane().getChildren().clear();
        for (DatabaseItem item : Database.getFilteredItems()) {
            Frontend.getGalleryPaneFront().getTilePane().getChildren().add(item.getImageView());
        }
    }
}
