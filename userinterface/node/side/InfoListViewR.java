package userinterface.node.side;

import database.object.DataObject;
import database.object.InfoObject;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import settings.SettingsNamespace;
import userinterface.node.BaseNode;
import userinterface.node.CustomButton;
import utils.CommonUtil;
import utils.InstanceRepo;

import java.util.ArrayList;
import java.util.Comparator;

public class InfoListViewR extends VBox implements BaseNode, InstanceRepo {
    private final Label selectLabel = new Label("Selection");
    private final TreeView<CustomTreeCell> treeView = new TreeView(new TreeItem());
    private final CustomButton btnExpCol = new CustomButton("Expand");

    public InfoListViewR() {
        VBox.setVgrow(treeView, Priority.ALWAYS);
        treeView.setShowRoot(false);
        treeView.setBackground(CommonUtil.getBackgroundDefault());
        treeView.expandedItemCountProperty().addListener((observable, oldValue, newValue) -> {
            treeView.lookupAll(".scroll-bar").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
            treeView.lookupAll(".increment-button").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
            treeView.lookupAll(".decrement-button").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
            treeView.lookupAll(".thumb").forEach(sb -> sb.setStyle("-fx-background-color: gray; -fx-background-insets: 0 4 0 4;"));
        });

        BorderPane bp = new BorderPane();
        bp.setCenter(selectLabel);
        bp.setRight(btnExpCol);
        bp.setBorder(new Border(new BorderStroke(CommonUtil.getNodeBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

        selectLabel.setTextFill(CommonUtil.getTextColorDefault());
        selectLabel.setFont(CommonUtil.getFont());

        btnExpCol.setTextFill(CommonUtil.getTextColorDefault());
        btnExpCol.setFont(CommonUtil.getFont());
        btnExpCol.setBorder(new Border(new BorderStroke(CommonUtil.getNodeBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 0, 1))));
        btnExpCol.setPrefWidth(75);

        this.setPrefWidth(999);
        this.setCellFactory();
        this.setSpacing(coreSettings.valueOf(SettingsNamespace.GLOBAL_PADDING));
        this.getChildren().addAll(bp, treeView);
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

            if (customTreeCell.getColor().equals(CommonUtil.getTextColorPositive()) || customTreeCell.getColor().equals(CommonUtil.getTextColorIntersect())) {
                if (parentTreeCell != null) parentTreeCell.setColor(CommonUtil.getTextColorDefault());
                customTreeCell.setColor(CommonUtil.getTextColorDefault());
                this.removeTagObjectFromSelection(infoObject);
            } else {
                if (parentTreeCell != null) parentTreeCell.setColor(CommonUtil.getTextColorPositive());
                customTreeCell.setColor(CommonUtil.getTextColorPositive());
                this.addTagObjectToSelection(infoObject);
            }
        }
        //reload.doReload();
        treeView.refresh();
    }
    public void addTagObjectToSelection(InfoObject infoObject) {
        if (select.size() < 1) {
            DataObject currentTargetedItem = target.getCurrentTarget();
            if (currentTargetedItem != null) {
                currentTargetedItem.getBaseListInfo().add(infoObject);
            }
        } else {
            select.addTagObject(infoObject);
        }
    }
    public void removeTagObjectFromSelection(InfoObject infoObject) {
        if (select.size() < 1) {
            DataObject currentTargetedItem = target.getCurrentTarget();
            if (currentTargetedItem != null) {
                currentTargetedItem.getBaseListInfo().remove(infoObject);
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

        Color textColorDefault = CommonUtil.getTextColorDefault();
        Color textColorPositive = CommonUtil.getTextColorPositive();
        Color textColorIntersect = CommonUtil.getTextColorIntersect();

        for (String groupInter : groupsInter) {
            groupsShare.remove(groupInter);
            groupsAll.remove(groupInter);

            TreeItem groupTreeItem = new TreeItem(new CustomTreeCell(groupInter, textColorPositive));
            ArrayList<String> namesInter = select.getIntersectingTags().getNames(groupInter);
            ArrayList<String> namesShare = select.getSharedTags().getNames(groupInter);
            ArrayList<String> namesAll = mainListInfo.getNames(groupInter);

            for (String nameInter : namesInter) {
                namesShare.remove(nameInter);
                namesAll.remove(nameInter);

                groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(nameInter, textColorPositive)));
            }
            for (String nameShare : namesShare) {
                namesAll.remove(nameShare);

                groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(nameShare, textColorIntersect)));
            }
            for (String nameAll : namesAll) {
                groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(nameAll, textColorDefault)));
            }

            treeViewItems.add(groupTreeItem);
        }
        for (String groupShare : groupsShare) {
            groupsAll.remove(groupShare);

            TreeItem groupTreeItem = new TreeItem(new CustomTreeCell(groupShare, textColorIntersect));
            ArrayList<String> namesShare = select.getSharedTags().getNames(groupShare);
            ArrayList<String> namesAll = mainListInfo.getNames(groupShare);

            for (String nameShare : namesShare) {
                namesAll.remove(nameShare);

                groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(nameShare, textColorIntersect)));
            }
            for (String nameAll : namesAll) {
                groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(nameAll, textColorDefault)));
            }

            treeViewItems.add(groupTreeItem);
        }
        for (String groupAll : groupsAll) {
            TreeItem groupTreeItem = new TreeItem(new CustomTreeCell(groupAll, textColorDefault));
            ArrayList<String> namesAll = mainListInfo.getNames(groupAll);

            for (String nameAll : namesAll) {
                groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(nameAll, textColorDefault)));
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

                setFont(CommonUtil.getFont());
                setBackground(CommonUtil.getBackgroundDefault());

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
