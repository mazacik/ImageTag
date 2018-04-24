package project.backend.components;

import project.backend.Backend;
import project.backend.Database;
import project.backend.DatabaseItem;
import project.frontend.shared.Frontend;
import project.frontend.shared.LeftPaneDisplayMode;

public class RightPaneBack {
    public void removeTag() {
        if (!Frontend.getRightPaneFront().getListView().getSelectionModel().getSelectedIndices().isEmpty()) {
            String tag = Frontend.getRightPaneFront().getListView().getSelectionModel().getSelectedItem();
            if (Frontend.getLeftPaneFront().getDisplayMode() == LeftPaneDisplayMode.TAGS)
                Backend.getLeftPaneBack().refreshContent();
            for (DatabaseItem databaseItem : Database.getSelectedItems())
                databaseItem.getTags().remove(tag);
            refreshContent();
        }
    }

    public void addTag() {
        String newTag = Frontend.getRightPaneFront().getAddTextField().getText();
        Frontend.getRightPaneFront().getAddTextField().clear();
        if (!newTag.isEmpty()) {
            if (!Database.getTagDatabase().contains(newTag)) {
                Database.getTagDatabase().add(newTag);
                if (Frontend.getLeftPaneFront().getDisplayMode() == LeftPaneDisplayMode.TAGS)
                    Backend.getLeftPaneBack().refreshContent();
            }
            for (DatabaseItem databaseItem : Database.getSelectedItems())
                if (!databaseItem.getTags().contains(newTag))
                    databaseItem.getTags().add(newTag);
            refreshContent();
        }
    }

    private void refreshContent() {
        Frontend.getRightPaneFront().getListView().getItems().setAll(Database.getSelectedItemsSharedTags());
    }
}
