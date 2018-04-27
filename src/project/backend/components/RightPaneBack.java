package project.backend.components;

import project.backend.shared.Database;
import project.frontend.shared.Frontend;

public class RightPaneBack {
    public void refreshContent() {
        Frontend.getRightPane().getListView().getItems().setAll(Database.getSelectedItemsSharedTags());
    }
}
