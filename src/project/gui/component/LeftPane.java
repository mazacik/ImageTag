package project.gui.component;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import project.database.TagDatabase;
import project.database.part.ColoredText;
import project.database.part.TagItem;
import project.gui.ChangeNotificationHelper;

import java.util.ArrayList;

public class LeftPane extends BorderPane implements ChangeNotificationHelper {
    /* change listeners */
    private final ArrayList<ChangeNotificationHelper> changeListeners = new ArrayList<>();

    /* components */
    private final TreeView<ColoredText> treeView = new TreeView(new TreeItem());

    /* constructors */
    public LeftPane() {
        setMinWidth(150);
        setPrefWidth(200);
        setMaxWidth(300);

        setCellFactory();
        setCenter(treeView);

        //GUIController.subscribe(this, GUIStage.getGalleryPane());
    }

    /* public methods */
    public void refresh() {
        ArrayList<TagItem> whitelist = TagDatabase.getDatabaseTagsWhitelist();
        ArrayList<TagItem> blacklist = TagDatabase.getDatabaseTagsBlacklist();
        ObservableList<TreeItem<ColoredText>> treeViewItems = treeView.getRoot().getChildren();

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

    public void refreshTreeview() {
        treeView.refresh();
    }

    /* private methods */
    private void setCellFactory() {
        treeView.setCellFactory(treeView -> new TreeCell<>() {
            @Override
            protected void updateItem(ColoredText coloredText, boolean empty) {
                super.updateItem(coloredText, empty);
                if (coloredText == null) {
                    setText(null);
                    setTextFill(null);
                } else {
                    setText(coloredText.getTagItem().getCategoryAndName());
                    setTextFill(coloredText.getColor());
                }
                ColoredText.setOnMouseClick(this, coloredText);
                ColoredText.setOnContextMenuRequest(this, coloredText);
            }
        });
    }

    /* getters */
    public ArrayList<ChangeNotificationHelper> getChangeListeners() {
        return changeListeners;
    }
}
