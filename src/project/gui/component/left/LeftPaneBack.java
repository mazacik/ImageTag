package project.gui.component.left;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import project.database.TagDatabase;
import project.database.part.ColoredText;
import project.database.part.TagItem;

import java.util.ArrayList;

public class LeftPaneBack {
    /* lazy singleton */
    private static LeftPaneBack instance;
    public static LeftPaneBack getInstance() {
        if (instance == null) instance = new LeftPaneBack();
        return instance;
    }

    /* imports */
    private final ObservableList<TreeItem<ColoredText>> treeViewItems = LeftPane.getInstance().getTreeView().getRoot().getChildren();

    private final ArrayList<TagItem> whitelist = TagDatabase.getDatabaseTagsWhitelist();
    private final ArrayList<TagItem> blacklist = TagDatabase.getDatabaseTagsBlacklist();

    /* public methods */
    public void reloadContent() {
        treeViewItems.clear();

        for (TagItem tagItem : TagDatabase.getDatabaseTags()) {
            if (whitelist.contains(tagItem))
                treeViewItems.add(new TreeItem(new ColoredText(tagItem, Color.GREEN)));
            else if (blacklist.contains(tagItem))
                treeViewItems.add(new TreeItem(new ColoredText(tagItem, Color.RED)));
            else
                treeViewItems.add(new TreeItem(new ColoredText(tagItem, Color.BLACK)));
        }
    }
}
