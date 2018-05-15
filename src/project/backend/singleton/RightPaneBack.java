package project.backend.singleton;

import project.backend.common.Filter;
import project.frontend.Frontend;
import project.frontend.singleton.RightPaneFront;

public class RightPaneBack {
    private static RightPaneBack instance = new RightPaneBack();


    public void reloadContent() {
        RightPaneFront.getInstance().getListView().getItems().setAll(Filter.getSelectedItemsSharedTags());
    }

    public static RightPaneBack getInstance() {
        return instance;
    }
}
