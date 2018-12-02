package gui.component.leftpane;

import database.object.TagObject;
import gui.component.ColorText;
import gui.component.NodeEnum;
import gui.event.leftpane.LeftPaneEvent;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import utils.MainUtil;

import java.util.ArrayList;

public class LeftPane extends VBox implements MainUtil {
    private final TreeView<ColorText> treeView;
    private final ContextMenu TEMP = new ContextMenu(); // todo fix me

    public LeftPane() {
        this.setMinWidth(200);
        this.setPrefWidth(250);
        this.setMaxWidth(300);

        treeView = new TreeView(new TreeItem());
        treeView.setMaxHeight(this.getMaxHeight());
        treeView.setShowRoot(false);

        VBox.setVgrow(treeView, Priority.ALWAYS);

        this.setCellFactory(TEMP);
        this.getChildren().addAll(treeView);
    }

    public void changeCellState(TreeCell<ColorText> sourceCell) {
        TagObject tagObject = mainTags.getTagObject(sourceCell);
        ColorText colorText;
        try {
            colorText = sourceCell.getTreeItem().getValue();
        } catch (NullPointerException e) {
            return;
        }

        // if sourceCell is group level
        if (tagObject == null) {
            String groupName = colorText.getText();
            if (filter.isGroupWhitelisted(groupName)) {
                filter.blacklistGroup(groupName);
                colorText.setColor(Color.RED);
                for (TreeItem<ColorText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.RED);
                }
            } else if (filter.isGroupBlacklisted(groupName)) {
                filter.unlistGroup(groupName);
                colorText.setColor(Color.BLACK);
                for (TreeItem<ColorText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.BLACK);
                }
            } else {
                filter.whitelistGroup(groupName);
                colorText.setColor(Color.GREEN);
                for (TreeItem<ColorText> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.GREEN);
                }
            }
        } else {
            if (filter.isTagObjectWhitelisted(tagObject)) {
                filter.blacklistTagObject(tagObject);
                colorText.setColor(Color.RED);
            } else if (filter.isTagObjectBlacklisted(tagObject)) {
                filter.unlistTagObject(tagObject);
                colorText.setColor(Color.BLACK);
            } else {
                filter.whitelistTagObject(tagObject);
                colorText.setColor(Color.GREEN);
            }
        }
        filter.apply();
        leftPane.refreshTreeView();
        reload.queue(NodeEnum.GALLERYPANE);
        reload.doReload();
    }

    public void reload() {
        ObservableList<TreeItem<ColorText>> treeViewItems = treeView.getRoot().getChildren();
        treeViewItems.clear();

        ArrayList<String> groupNames = mainTags.getGroups();
        for (String groupName : groupNames) {
            TreeItem groupTreeItem;
            if (filter.isGroupWhitelisted(groupName)) {
                groupTreeItem = new TreeItem(new ColorText(groupName, Color.GREEN));
            } else if (filter.isGroupBlacklisted(groupName)) {
                groupTreeItem = new TreeItem(new ColorText(groupName, Color.RED));
            } else {
                groupTreeItem = new TreeItem(new ColorText(groupName, Color.BLACK));
            }

            for (String tagName : mainTags.getNames(groupName)) {
                if (filter.isTagObjectWhitelisted(groupName, tagName)) {
                    groupTreeItem.getChildren().add(new TreeItem(new ColorText(tagName, Color.GREEN)));
                } else if (filter.isTagObjectBlacklisted(groupName, tagName)) {
                    groupTreeItem.getChildren().add(new TreeItem(new ColorText(tagName, Color.RED)));
                } else {
                    groupTreeItem.getChildren().add(new TreeItem(new ColorText(tagName, Color.BLACK)));
                }
            }

            treeViewItems.add(groupTreeItem);
        }
    }
    public void refreshTreeView() {
        treeView.refresh();
    }
    private void setCellFactory(ContextMenu contextMenu) {
        treeView.setCellFactory(treeView -> new TreeCell<>() {
            @Override
            protected void updateItem(ColorText colorText, boolean empty) {
                super.updateItem(colorText, empty);
                if (colorText == null) {
                    setText(null);
                    setTextFill(null);
                } else {
                    setText(colorText.getText());
                    setTextFill(colorText.getColor());
                }

                this.setContextMenu(contextMenu);
                this.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                    if (e.getClickCount() % 2 == 0 && e.getButton().equals(MouseButton.PRIMARY)) {
                        e.consume();
                    }
                });
                LeftPaneEvent.onMouseClick(this);
            }
        });
    }
}
