package project.gui.component.right;

import project.backend.Selection;
import project.database.part.TagItem;

import java.util.ArrayList;

public class RightPaneBack {
    /* lazy singleton */
    private static RightPaneBack instance;
    public static RightPaneBack getInstance() {
        if (instance == null) instance = new RightPaneBack();
        return instance;
    }

    /* constructors */
    private RightPaneBack() {
        RightPaneListener.getInstance();
    }

    /* public methods */
    public void reloadContent() {
        ArrayList<String> sharedTags = new ArrayList<>();
        for (TagItem tagItem : Selection.getSharedTags())
            sharedTags.add(tagItem.getCategoryAndName());
        RightPaneFront.getInstance().getListView().getItems().setAll(sharedTags);
    }
}
