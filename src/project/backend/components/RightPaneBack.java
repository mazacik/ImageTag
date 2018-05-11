package project.backend.components;

import project.backend.common.Filter;
import project.frontend.shared.Frontend;

/**
 * Backend of a GUI component which displays the current selection's intersecting tags in a list.
 */
public class RightPaneBack {

    /**
     * Requests the selection's intersecting tags and displays them in a list.
     */
    public void reloadContent() {
        Frontend.getRightPane().getListView().getItems().setAll(Filter.getSelectedItemsSharedTags());
    }
}
