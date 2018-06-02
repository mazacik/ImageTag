package project.component.left;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import project.common.Database;
import project.component.left.part.ColoredText;

import java.util.List;

public class LeftPaneBack {
    /* lazy singleton */
    private static LeftPaneBack instance;
    public static LeftPaneBack getInstance() {
        if (instance == null) instance = new LeftPaneBack();
        return instance;
    }

    /* imports */
    private final ObservableList<ColoredText> listViewItems = LeftPaneFront.getInstance().getListView().getItems();

    private final List<String> whitelist = Database.getDatabaseTagsWhitelist();
    private final List<String> blacklist = Database.getDatabaseTagsBlacklist();

    /* public methods */
    public void reloadContent() {
        listViewItems.clear();

        for (String tag : Database.getDatabaseTags()) {
            if (whitelist.contains(tag))
                listViewItems.add(new ColoredText(tag, Color.GREEN));
            else if (blacklist.contains(tag))
                listViewItems.add(new ColoredText(tag, Color.RED));
            else
                listViewItems.add(new ColoredText(tag, Color.BLACK));
        }
    }
}
