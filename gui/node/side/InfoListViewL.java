package gui.node.side;

import control.reload.Reload;
import database.object.InfoObject;
import gui.event.side.InfoListLeftEvent;
import gui.node.BaseNode;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import settings.SettingsNamespace;
import utils.MainUtil;

import java.util.ArrayList;

public class InfoListViewL extends VBox implements MainUtil, BaseNode {
    private final TreeView<CustomTreeCell> treeView;
    private final Button btnExpand = new Button("Expand All");
    private final Button btnCollapse = new Button("Collapse All");
    private final Button btnNew = new Button("New");

    public InfoListViewL() {
        this.setMinWidth(200);
        this.setPrefWidth(250);
        this.setMaxWidth(300);

        treeView = new TreeView(new TreeItem());
        treeView.setMaxHeight(this.getMaxHeight());
        treeView.setShowRoot(false);
        VBox.setVgrow(treeView, Priority.ALWAYS);
        this.setSpacing(settings.valueOf(SettingsNamespace.GLOBAL_SPACING));
        this.setPadding(new Insets(settings.valueOf(SettingsNamespace.GLOBAL_SPACING)));

        btnNew.setPrefWidth(this.getMaxWidth());

        reload.subscribe(this, Reload.Control.INFO);

        this.setCellFactory();

        btnExpand.setPrefWidth(this.getPrefWidth());
        btnCollapse.setPrefWidth(this.getPrefWidth());
        HBox hbox = new HBox(btnExpand, btnCollapse);
        hbox.setSpacing(settings.valueOf(SettingsNamespace.GLOBAL_SPACING));

        this.getChildren().addAll(hbox, treeView, btnNew);
    }

    public void changeCellState(TreeCell<CustomTreeCell> sourceCell) {
        InfoObject infoObject = mainListInfo.getInfoObject(sourceCell);
        CustomTreeCell customTreeCell;
        try {
            customTreeCell = sourceCell.getTreeItem().getValue();
        } catch (NullPointerException e) {
            return;
        }

        // if sourceCell is group level
        if (infoObject == null) {
            String groupName = customTreeCell.getText();
            if (filter.isGroupWhitelisted(groupName)) {
                filter.blacklistGroup(groupName);
                customTreeCell.setColor(Color.RED);
                for (TreeItem<CustomTreeCell> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.RED);
                }
            } else if (filter.isGroupBlacklisted(groupName)) {
                filter.unlistGroup(groupName);
                customTreeCell.setColor(Color.BLACK);
                for (TreeItem<CustomTreeCell> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.BLACK);
                }
            } else {
                filter.whitelistGroup(groupName);
                customTreeCell.setColor(Color.GREEN);
                for (TreeItem<CustomTreeCell> children : sourceCell.getTreeItem().getChildren()) {
                    children.getValue().setColor(Color.GREEN);
                }
            }
        } else {
            if (filter.isTagObjectWhitelisted(infoObject)) {
                filter.blacklistTagObject(infoObject);
                customTreeCell.setColor(Color.RED);
            } else if (filter.isTagObjectBlacklisted(infoObject)) {
                filter.unlistTagObject(infoObject);
                customTreeCell.setColor(Color.BLACK);
            } else {
                filter.whitelistTagObject(infoObject);
                customTreeCell.setColor(Color.GREEN);
            }
        }
        filter.apply();
        treeView.refresh();
        reload.doReload();
    }

    public void reload() {
        ObservableList<TreeItem<CustomTreeCell>> treeViewItems = treeView.getRoot().getChildren();
        treeViewItems.clear();

        ArrayList<String> groupNames = mainListInfo.getGroups();
        for (String groupName : groupNames) {
            TreeItem groupTreeItem;
            if (filter.isGroupWhitelisted(groupName)) {
                groupTreeItem = new TreeItem(new CustomTreeCell(groupName, Color.GREEN));
            } else if (filter.isGroupBlacklisted(groupName)) {
                groupTreeItem = new TreeItem(new CustomTreeCell(groupName, Color.RED));
            } else {
                groupTreeItem = new TreeItem(new CustomTreeCell(groupName, Color.BLACK));
            }

            for (String tagName : mainListInfo.getNames(groupName)) {
                if (filter.isTagObjectWhitelisted(groupName, tagName)) {
                    groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(tagName, Color.GREEN)));
                } else if (filter.isTagObjectBlacklisted(groupName, tagName)) {
                    groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(tagName, Color.RED)));
                } else {
                    groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(tagName, Color.BLACK)));
                }
            }

            treeViewItems.add(groupTreeItem);
        }
    }
    private void setCellFactory() {
        treeView.setCellFactory(treeView -> new TreeCell<>() {
            @Override
            protected void updateItem(CustomTreeCell customTreeCell, boolean empty) {
                super.updateItem(customTreeCell, empty);
                if (customTreeCell == null) {
                    setText(null);
                    setTextFill(null);
                } else {
                    setText(customTreeCell.getText());
                    setTextFill(customTreeCell.getColor());
                }

                this.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                    if (e.getClickCount() % 2 == 0 && e.getButton().equals(MouseButton.PRIMARY)) {
                        e.consume();
                    }
                });
                InfoListLeftEvent.onMouseClick(this);
            }
        });
    }

    public TreeView<CustomTreeCell> getTreeView() {
        return treeView;
    }
    public Button getBtnExpand() {
        return btnExpand;
    }
    public Button getBtnCollapse() {
        return btnCollapse;
    }
    public Button getBtnNew() {
        return btnNew;
    }
}
