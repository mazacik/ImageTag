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
import project.gui.ChangeEventListener;

import java.util.ArrayList;

public class PaneLeft extends BorderPane implements ChangeEventListener {
    /* change listeners */
    private final ArrayList<ChangeEventListener> changeListeners = new ArrayList<>();

    /* components */
    private final TreeView<ColoredText> treeView = new TreeView(new TreeItem());

    /* constructors */
    public PaneLeft() {
        setMinWidth(150);
        setPrefWidth(200);
        setMaxWidth(300);

        setCellFactory();
        setCenter(treeView);

        //ChangeEventControl.subscribe(this, GUIStage.getPaneGallery());
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
}
