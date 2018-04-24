package project.backend.components;

import project.backend.shared.Backend;
import project.backend.shared.Database;
import project.backend.shared.DatabaseItem;
import project.frontend.shared.Frontend;
import project.frontend.shared.LeftPaneDisplayMode;

public class RightPaneBack {
    public void removeTag() {
        if (!Frontend.getRightPane().getListView().getSelectionModel().getSelectedIndices().isEmpty()) {
            String tag = Frontend.getRightPane().getListView().getSelectionModel().getSelectedItem();
            if (Frontend.getLeftPane().getDisplayMode() == LeftPaneDisplayMode.TAGS)
                Backend.getLeftPane().refreshContent();
            for (DatabaseItem databaseItem : Database.getSelectedItems())
                databaseItem.getTags().remove(tag);
            refreshContent();
        }
    }

    public void addTag() {
        String newTag = Frontend.getRightPane().getAddTextField().getText();
        Frontend.getRightPane().getAddTextField().clear();
        if (!newTag.isEmpty()) {
            if (!Database.getTagDatabase().contains(newTag)) {
                Database.getTagDatabase().add(newTag);
                if (Frontend.getLeftPane().getDisplayMode() == LeftPaneDisplayMode.TAGS)
                    Backend.getLeftPane().refreshContent();
            }
            for (DatabaseItem databaseItem : Database.getSelectedItems())
                if (!databaseItem.getTags().contains(newTag))
                    databaseItem.getTags().add(newTag);
            refreshContent();
        }
    }

    private void refreshContent() {
        Frontend.getRightPane().getListView().getItems().setAll(Database.getSelectedItemsSharedTags());
    }
}
