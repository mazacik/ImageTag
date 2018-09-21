package project.gui.component.leftpane;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import project.control.MainControl;
import project.control.TagControl;
import project.gui.custom.specific.LeftPaneContextMenu;
import project.gui.event.leftpane.ColoredTextEvent;

import java.util.ArrayList;

public abstract class LeftPane {
    private static final BorderPane _this = new BorderPane();
    private static final TreeView<ColoredText> treeView = new TreeView(new TreeItem());

    public static void initialize() {
        treeView.setShowRoot(false);
        LeftPane.setCellFactory();

        _this.setMinWidth(200);
        _this.setPrefWidth(250);
        _this.setMaxWidth(300);
        _this.setCenter(treeView);
    }

    public static void reload() {
        ObservableList<TreeItem<ColoredText>> treeViewItems = treeView.getRoot().getChildren();
        treeViewItems.clear();

        ArrayList<String> groupNames = TagControl.getGroups();
        for (String groupName : groupNames) {
            TreeItem groupTreeItem;
            if (MainControl.getFilterControl().isGroupWhitelisted(groupName)) {
                groupTreeItem = new TreeItem(new ColoredText(groupName, Color.GREEN));
            } else if (MainControl.getFilterControl().isGroupBlacklisted(groupName)) {
                groupTreeItem = new TreeItem(new ColoredText(groupName, Color.RED));
            } else {
                groupTreeItem = new TreeItem(new ColoredText(groupName, Color.BLACK));
            }

            for (String tagName : TagControl.getNames(groupName)) {
                if (MainControl.getFilterControl().isTagObjectWhitelisted(groupName, tagName)) {
                    groupTreeItem.getChildren().add(new TreeItem(new ColoredText(tagName, Color.GREEN)));
                } else if (MainControl.getFilterControl().isTagObjectBlacklisted(groupName, tagName)) {
                    groupTreeItem.getChildren().add(new TreeItem(new ColoredText(tagName, Color.RED)));
                } else {
                    groupTreeItem.getChildren().add(new TreeItem(new ColoredText(tagName, Color.BLACK)));
                }
            }

            treeViewItems.add(groupTreeItem);
        }
    }
    public static void refreshTreeView() {
        treeView.refresh();
    }

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

                this.setContextMenu(new LeftPaneContextMenu(this));
                ColoredTextEvent.onMouseClick(this);

                addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                    if (e.getClickCount() % 2 == 0 && e.getButton().equals(MouseButton.PRIMARY)) {
                        e.consume();
                    }
                });
            }
        });
    }

    public static Region getInstance() {
        return _this;
    }
}
