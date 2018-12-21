package gui.singleton.side;

import control.reload.Reload;
import database.object.DataObject;
import database.object.InfoObject;
import gui.event.side.InfoListRightEvent;
import gui.singleton.BaseNode;
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
import java.util.Comparator;

public class InfoListR extends VBox implements MainUtil, BaseNode {
    private final TreeView<CustomTreeCell> treeView;
    private final ContextMenu TEMP = new ContextMenu(); // todo fix me

    public InfoListR() {
        this.setMinWidth(200);
        this.setPrefWidth(250);
        this.setMaxWidth(300);

        treeView = new TreeView(new TreeItem());
        treeView.setMaxHeight(this.getMaxHeight());
        treeView.setShowRoot(false);
        VBox.setVgrow(treeView, Priority.ALWAYS);

        reload.subscribe(this, Reload.Control.TAGS, Reload.Control.SELECTION, Reload.Control.FOCUS);

        this.setCellFactory(TEMP);
        this.getChildren().addAll(treeView);
    }

    public void changeCellState(TreeCell<CustomTreeCell> sourceCell) {
        InfoObject infoObject = infoListMain.getTagObject(sourceCell);
        CustomTreeCell customTreeCell;
        try {
            customTreeCell = sourceCell.getTreeItem().getValue();
        } catch (NullPointerException e) {
            return;
        }

        if (infoObject != null) {
            if (customTreeCell.getColor().equals(Color.GREEN) || customTreeCell.getColor().equals(Color.BLUE)) {
                customTreeCell.setColor(Color.BLACK);
                this.removeTagObjectFromSelection(infoObject);
            } else {
                customTreeCell.setColor(Color.GREEN);
                this.addTagObjectToSelection(infoObject);
            }
        }
        treeView.refresh();
    }
    public void addTagObjectToSelection(InfoObject infoObject) {
        if (select.size() < 1) {
            DataObject currentFocusedItem = target.getCurrentFocus();
            if (currentFocusedItem != null) {
                currentFocusedItem.getBaseListInfo().add(infoObject);
            }
        } else {
            select.addTagObject(infoObject);
        }
    }
    public void removeTagObjectFromSelection(InfoObject infoObject) {
        if (select.size() < 1) {
            DataObject currentFocusedItem = target.getCurrentFocus();
            if (currentFocusedItem != null) {
                currentFocusedItem.getBaseListInfo().remove(infoObject);
            }
        } else {
            select.removeTagObject(infoObject);
        }
    }

    public void reload() {
        //todo split
        ObservableList<TreeItem<CustomTreeCell>> treeViewItems = treeView.getRoot().getChildren();

        int treeViewItemsCountBefore = treeViewItems.size();
        ArrayList<Integer> expandedItemIndices = new ArrayList<>();
        for (TreeItem<CustomTreeCell> treeItem : treeViewItems) {
            if (treeItem.isExpanded()) {
                expandedItemIndices.add(treeViewItems.indexOf(treeItem));
            }
        }

        treeViewItems.clear();

        ArrayList<String> groupsInter = select.getIntersectingTags().getGroups();
        ArrayList<String> groupsShare = select.getSharedTags().getGroups();
        ArrayList<String> groupsAll = infoListMain.getGroups();

        for (String groupInter : groupsInter) {
            groupsShare.remove(groupInter);
            groupsAll.remove(groupInter);

            TreeItem groupTreeItem = new TreeItem(new CustomTreeCell(groupInter, Color.GREEN));
            ArrayList<String> namesInter = select.getIntersectingTags().getNames(groupInter);
            ArrayList<String> namesShare = select.getSharedTags().getNames(groupInter);
            ArrayList<String> namesAll = infoListMain.getNames(groupInter);

            for (String nameInter : namesInter) {
                namesShare.remove(nameInter);
                namesAll.remove(nameInter);

                groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(nameInter, Color.GREEN)));
            }
            for (String nameShare : namesShare) {
                namesAll.remove(nameShare);

                groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(nameShare, Color.BLUE)));
            }
            for (String nameAll : namesAll) {
                groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(nameAll, Color.BLACK)));
            }

            treeViewItems.add(groupTreeItem);
        }
        for (String groupShare : groupsShare) {
            groupsAll.remove(groupShare);

            TreeItem groupTreeItem = new TreeItem(new CustomTreeCell(groupShare, Color.BLUE));
            ArrayList<String> namesShare = select.getSharedTags().getNames(groupShare);
            ArrayList<String> namesAll = infoListMain.getNames(groupShare);

            for (String nameShare : namesShare) {
                namesAll.remove(nameShare);

                groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(nameShare, Color.BLUE)));
            }
            for (String nameAll : namesAll) {
                groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(nameAll, Color.BLACK)));
            }

            treeViewItems.add(groupTreeItem);
        }
        for (String groupAll : groupsAll) {
            TreeItem groupTreeItem = new TreeItem(new CustomTreeCell(groupAll, Color.BLACK));
            ArrayList<String> namesAll = infoListMain.getNames(groupAll);

            for (String nameAll : namesAll) {
                groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(nameAll, Color.BLACK)));
            }

            treeViewItems.add(groupTreeItem);
        }

        treeViewItems.sort(Comparator.comparing(colorTextTreeItem -> colorTextTreeItem.getValue().getText()));

        if (treeViewItemsCountBefore == treeViewItems.size()) {
            for (TreeItem<CustomTreeCell> treeItem : treeViewItems) {
                if (expandedItemIndices.contains(treeViewItems.indexOf(treeItem))) {
                    treeItem.setExpanded(true);
                }
            }
        }

        treeView.refresh();
    }
    private void setCellFactory(ContextMenu contextMenu) {
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

                this.setContextMenu(contextMenu);
                this.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                    if (e.getClickCount() % 2 == 0 && e.getButton().equals(MouseButton.PRIMARY)) {
                        e.consume();
                    }
                });
                InfoListRightEvent.onMouseClick(this);
            }
        });
    }
}
