package userinterface.node.side;

import database.object.DataObject;
import database.object.InfoObject;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import namespace.Namespace;
import settings.SettingsNamespace;
import userinterface.BackgroundEnum;
import userinterface.node.BaseNode;
import utils.MainUtil;

import java.util.ArrayList;
import java.util.Comparator;

public class InfoListViewR extends VBox implements BaseNode, MainUtil {
    private final TreeView<CustomTreeCell> treeView;
    private final Button btnExpCol = new Button(Namespace.GUI_SIDE_BTN_EXPCOL_STATE_FALSE.getValue());

    public InfoListViewR() {
        this.setMinWidth(200);
        this.setPrefWidth(250);
        this.setMaxWidth(300);
        this.setSpacing(settings.valueOf(SettingsNamespace.GLOBAL_PADDING));
        this.setBackground(BackgroundEnum.NIGHT_1.getValue());

        treeView = new TreeView(new TreeItem());
        treeView.setMaxHeight(this.getMaxHeight());
        treeView.setShowRoot(false);
        VBox.setVgrow(treeView, Priority.ALWAYS);
        treeView.setBackground(BackgroundEnum.NIGHT_1.getValue());
        this.setCellFactory();

        btnExpCol.setPrefWidth(this.getPrefWidth());

        this.getChildren().addAll(btnExpCol, treeView);
    }

    public void changeCellState(TreeCell<CustomTreeCell> sourceCell) {
        CustomTreeCell customTreeCell;
        try { customTreeCell = sourceCell.getTreeItem().getValue(); } catch (NullPointerException e) { return; }

        InfoObject infoObject = mainListInfo.getInfoObject(sourceCell);
        // if sourceCell is group level
        if (infoObject != null) {
            CustomTreeCell parentTreeCell = null;
            try {
                parentTreeCell = sourceCell.getTreeItem().getParent().getValue();
            } catch (NullPointerException ignored) {}

            if (customTreeCell.getColor().equals(Color.GREEN) || customTreeCell.getColor().equals(Color.BLUE)) {
                if (parentTreeCell != null) parentTreeCell.setColor(Color.BLACK);
                customTreeCell.setColor(Color.BLACK);
                this.removeTagObjectFromSelection(infoObject);
            } else {
                if (parentTreeCell != null) parentTreeCell.setColor(Color.GREEN);
                customTreeCell.setColor(Color.GREEN);
                this.addTagObjectToSelection(infoObject);
            }
        }
        treeView.refresh();
    }
    public void addTagObjectToSelection(InfoObject infoObject) {
        if (select.size() < 1) {
            DataObject currentFocusedItem = target.getCurrentTarget();
            if (currentFocusedItem != null) {
                currentFocusedItem.getBaseListInfo().add(infoObject);
            }
        } else {
            select.addTagObject(infoObject);
        }
    }
    public void removeTagObjectFromSelection(InfoObject infoObject) {
        if (select.size() < 1) {
            DataObject currentFocusedItem = target.getCurrentTarget();
            if (currentFocusedItem != null) {
                currentFocusedItem.getBaseListInfo().remove(infoObject);
            }
        } else {
            select.removeTagObject(infoObject);
        }
    }

    public void reload() {
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
        ArrayList<String> groupsAll = mainListInfo.getGroups();

        for (String groupInter : groupsInter) {
            groupsShare.remove(groupInter);
            groupsAll.remove(groupInter);

            TreeItem groupTreeItem = new TreeItem(new CustomTreeCell(groupInter, Color.GREEN));
            ArrayList<String> namesInter = select.getIntersectingTags().getNames(groupInter);
            ArrayList<String> namesShare = select.getSharedTags().getNames(groupInter);
            ArrayList<String> namesAll = mainListInfo.getNames(groupInter);

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
            ArrayList<String> namesAll = mainListInfo.getNames(groupShare);

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
            ArrayList<String> namesAll = mainListInfo.getNames(groupAll);

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

                setBackground(BackgroundEnum.NIGHT_1.getValue());

                InfoListViewREvent.onMouseClick(this);
                this.addEventFilter(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
                    if (e.getClickCount() % 2 == 0 && e.getButton().equals(MouseButton.PRIMARY)) {
                        // disable double click expand
                        e.consume();
                    }
                });
            }
        });
    }

    public TreeView<CustomTreeCell> getTreeView() {
        return treeView;
    }
    public Button getBtnExpCol() {
        return btnExpCol;
    }
}
