package project.gui.component.leftpane;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import project.MainUtils;
import project.gui.custom.specific.LeftPaneContextMenu;
import project.gui.event.leftpane.ColoredTextEvent;

import java.util.ArrayList;

public class LeftPane extends BorderPane implements MainUtils {
    private final TreeView<ColoredText> treeView;

    public LeftPane() {
        treeView = new TreeView(new TreeItem());
        treeView.setShowRoot(false);
        this.setCellFactory();

        this.setMinWidth(200);
        this.setPrefWidth(250);
        this.setMaxWidth(300);
        this.setCenter(treeView);
    }

    public void reload() {
        ObservableList<TreeItem<ColoredText>> treeViewItems = treeView.getRoot().getChildren();
        treeViewItems.clear();

        ArrayList<String> groupNames = tagControl.getGroups();
        for (String groupName : groupNames) {
            TreeItem groupTreeItem;
            if (filterControl.isGroupWhitelisted(groupName)) {
                groupTreeItem = new TreeItem(new ColoredText(groupName, Color.GREEN));
            } else if (filterControl.isGroupBlacklisted(groupName)) {
                groupTreeItem = new TreeItem(new ColoredText(groupName, Color.RED));
            } else {
                groupTreeItem = new TreeItem(new ColoredText(groupName, Color.BLACK));
            }

            for (String tagName : tagControl.getNames(groupName)) {
                if (filterControl.isTagObjectWhitelisted(groupName, tagName)) {
                    groupTreeItem.getChildren().add(new TreeItem(new ColoredText(tagName, Color.GREEN)));
                } else if (filterControl.isTagObjectBlacklisted(groupName, tagName)) {
                    groupTreeItem.getChildren().add(new TreeItem(new ColoredText(tagName, Color.RED)));
                } else {
                    groupTreeItem.getChildren().add(new TreeItem(new ColoredText(tagName, Color.BLACK)));
                }
            }

            treeViewItems.add(groupTreeItem);
        }
    }
    public void refreshTreeView() {
        treeView.refresh();
    }

    private void setCellFactory() {
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
}
