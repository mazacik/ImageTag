package project.gui.component;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import project.control.FilterControl;
import project.control.change.ChangeEventControl;
import project.control.change.ChangeEventEnum;
import project.database.control.TagElementControl;
import project.gui.component.part.ColoredText;

import java.util.ArrayList;

public abstract class LeftPane extends BorderPane {
    /* components */
    private static final BorderPane _this = new BorderPane();
    private static final TreeView<ColoredText> treeView = new TreeView(new TreeItem());

    /* initialize */
    public static void initialize() {
        _this.setMinWidth(150);
        _this.setPrefWidth(200);
        _this.setMaxWidth(300);

        treeView.setShowRoot(false);
        LeftPane.setCellFactory();
        _this.setCenter(treeView);

        ChangeEventControl.subscribe(LeftPane.class, (ChangeEventEnum[]) null);
    }

    /* public */
    public static void refreshComponent() {
        ObservableList<TreeItem<ColoredText>> treeViewItems = treeView.getRoot().getChildren();
        ArrayList<String> groupNames = TagElementControl.getGroups();

        treeViewItems.clear();

        for (String groupName : groupNames) {
            TreeItem groupTreeItem;
            if (FilterControl.isGroupWhitelisted(groupName)) {
                groupTreeItem = new TreeItem(new ColoredText(groupName, Color.GREEN));
            } else if (FilterControl.isGroupBlacklisted(groupName)) {
                groupTreeItem = new TreeItem(new ColoredText(groupName, Color.RED));
            } else {
                groupTreeItem = new TreeItem(new ColoredText(groupName, Color.BLACK));
            }

            for (String tagName : TagElementControl.getNamesInGroup(groupName)) {
                if (FilterControl.isTagElementWhitelisted(groupName, tagName)) {
                    groupTreeItem.getChildren().add(new TreeItem(new ColoredText(tagName, Color.GREEN)));
                } else if (FilterControl.isTagElementBlacklisted(groupName, tagName)) {
                    groupTreeItem.getChildren().add(new TreeItem(new ColoredText(tagName, Color.RED)));
                } else {
                    groupTreeItem.getChildren().add(new TreeItem(new ColoredText(tagName, Color.BLACK)));
                }
            }
            treeViewItems.add(groupTreeItem);
        }
    }
    public static void refreshTreeview() {
        treeView.refresh();
    }

    /* private */
    private static void setCellFactory() {
        treeView.setCellFactory(treeView -> new TreeCell<>() {
            @Override
            protected void updateItem(ColoredText coloredText, boolean empty) {
                super.updateItem(coloredText, empty);
                if (coloredText == null) {
                    setText(null);
                    setTextFill(null);
                } else {
                    setText(coloredText.getText());
                    setTextFill(coloredText.getColor());
                }

                ColoredText.setOnMouseClick(this);
                ColoredText.setContextMenu(this);

                addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                    if (e.getClickCount() % 2 == 0 && e.getButton().equals(MouseButton.PRIMARY)) {
                        e.consume();
                    }
                });
            }
        });
    }

    /* get */
    public static Node getInstance() {
        return _this;
    }
}
