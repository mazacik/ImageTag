package project.backend.singleton;

import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import project.backend.database.Database;
import project.frontend.component.ColoredText;
import project.frontend.singleton.LeftPaneFront;

import java.util.List;

public class LeftPaneBack {
    private static final LeftPaneBack instance = new LeftPaneBack();

    private final ListView<ColoredText> listView = LeftPaneFront.getInstance().getListView();

    private static final List<String> whitelist = Database.getDatabaseTagsWhitelist();
    private static final List<String> blacklist = Database.getDatabaseTagsBlacklist();


    //todo: add/remove methods
    public void reloadContent() {
        listView.getItems().clear();
        for (String tag : Database.getDatabaseTags()) {
            if (whitelist.contains(tag))
                listView.getItems().add(new ColoredText(tag, Color.GREEN));
            else if (blacklist.contains(tag))
                listView.getItems().add(new ColoredText(tag, Color.RED));
            else
                listView.getItems().add(new ColoredText(tag, Color.BLACK));
        }
    }

    public static LeftPaneBack getInstance() {
        return instance;
    }
}
