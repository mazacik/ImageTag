package project.component.right;

import project.common.Filter;

public class RightPaneBack {
    /* lazy singleton */
    private static RightPaneBack instance;
    public static RightPaneBack getInstance() {
        if (instance == null) instance = new RightPaneBack();
        return instance;
    }

    /* constructor */
    private RightPaneBack() {
        RightPaneListener.getInstance();
    }

    /* method */
    public void reloadContent() {
        RightPaneFront.getInstance().getListView().getItems().setAll(Filter.getIntersectingTagsOfSelectedItems());
    }
}
