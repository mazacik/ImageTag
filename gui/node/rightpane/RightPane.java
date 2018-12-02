package gui.node.rightpane;

import control.reload.Reload;
import database.object.DataObject;
import database.object.TagObject;
import gui.event.rightpane.RightPaneEvent;
import gui.node.BaseNode;
import gui.node.ColorText;
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

public class RightPane extends VBox implements MainUtil, BaseNode {
    private final TreeView<ColorText> treeView;
    private final ContextMenu TEMP = new ContextMenu(); // todo fix me

    public RightPane() {
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

    public void changeCellState(TreeCell<ColorText> sourceCell) {
        TagObject tagObject = mainTags.getTagObject(sourceCell);
        ColorText colorText;
        try {
            colorText = sourceCell.getTreeItem().getValue();
        } catch (NullPointerException e) {
            return;
        }

        if (tagObject != null) {
            if (colorText.getColor().equals(Color.GREEN) || colorText.getColor().equals(Color.BLUE)) {
                colorText.setColor(Color.BLACK);
                this.removeTagObjectFromSelection(tagObject);
            } else {
                colorText.setColor(Color.GREEN);
                this.addTagObjectToSelection(tagObject);
            }
        }
        treeView.refresh();
    }
    public void addTagObjectToSelection(TagObject tagObject) {
        if (selection.size() < 1) {
            DataObject currentFocusedItem = focus.getCurrentFocus();
            if (currentFocusedItem != null) {
                currentFocusedItem.getTagCollection().add(tagObject);
            }
        } else {
            selection.addTagObject(tagObject);
        }
    }
    public void removeTagObjectFromSelection(TagObject tagObject) {
        if (selection.size() < 1) {
            DataObject currentFocusedItem = focus.getCurrentFocus();
            if (currentFocusedItem != null) {
                currentFocusedItem.getTagCollection().remove(tagObject);
            }
        } else {
            selection.removeTagObject(tagObject);
        }
    }

    public void reload() {
        //todo split
        ObservableList<TreeItem<ColorText>> treeViewItems = treeView.getRoot().getChildren();

        int treeViewItemsCountBefore = treeViewItems.size();
        ArrayList<Integer> expandedItemIndices = new ArrayList<>();
        for (TreeItem<ColorText> treeItem : treeViewItems) {
            if (treeItem.isExpanded()) {
                expandedItemIndices.add(treeViewItems.indexOf(treeItem));
            }
        }

        treeViewItems.clear();

        ArrayList<String> groupsInter = selection.getIntersectingTags().getGroups();
        ArrayList<String> groupsShare = selection.getSharedTags().getGroups();
        ArrayList<String> groupsAll = mainTags.getGroups();

        for (String groupInter : groupsInter) {
            groupsShare.remove(groupInter);
            groupsAll.remove(groupInter);

            TreeItem groupTreeItem = new TreeItem(new ColorText(groupInter, Color.GREEN));
            ArrayList<String> namesInter = selection.getIntersectingTags().getNames(groupInter);
            ArrayList<String> namesShare = selection.getSharedTags().getNames(groupInter);
            ArrayList<String> namesAll = mainTags.getNames(groupInter);

            for (String nameInter : namesInter) {
                namesShare.remove(nameInter);
                namesAll.remove(nameInter);

                groupTreeItem.getChildren().add(new TreeItem(new ColorText(nameInter, Color.GREEN)));
            }
            for (String nameShare : namesShare) {
                namesAll.remove(nameShare);

                groupTreeItem.getChildren().add(new TreeItem(new ColorText(nameShare, Color.BLUE)));
            }
            for (String nameAll : namesAll) {
                groupTreeItem.getChildren().add(new TreeItem(new ColorText(nameAll, Color.BLACK)));
            }

            treeViewItems.add(groupTreeItem);
        }
        for (String groupShare : groupsShare) {
            groupsAll.remove(groupShare);

            TreeItem groupTreeItem = new TreeItem(new ColorText(groupShare, Color.BLUE));
            ArrayList<String> namesShare = selection.getSharedTags().getNames(groupShare);
            ArrayList<String> namesAll = mainTags.getNames(groupShare);

            for (String nameShare : namesShare) {
                namesAll.remove(nameShare);

                groupTreeItem.getChildren().add(new TreeItem(new ColorText(nameShare, Color.BLUE)));
            }
            for (String nameAll : namesAll) {
                groupTreeItem.getChildren().add(new TreeItem(new ColorText(nameAll, Color.BLACK)));
            }

            treeViewItems.add(groupTreeItem);
        }
        for (String groupAll : groupsAll) {
            TreeItem groupTreeItem = new TreeItem(new ColorText(groupAll, Color.BLACK));
            ArrayList<String> namesAll = mainTags.getNames(groupAll);

            for (String nameAll : namesAll) {
                groupTreeItem.getChildren().add(new TreeItem(new ColorText(nameAll, Color.BLACK)));
            }

            treeViewItems.add(groupTreeItem);
        }

        treeViewItems.sort(Comparator.comparing(colorTextTreeItem -> colorTextTreeItem.getValue().getText()));

        if (treeViewItemsCountBefore == treeViewItems.size()) {
            for (TreeItem<ColorText> treeItem : treeViewItems) {
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
                RightPaneEvent.onMouseClick(this);
            }
        });
    }
}
