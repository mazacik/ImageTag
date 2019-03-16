package user_interface.singleton.side;

import control.reload.Reload;
import database.object.DataObject;
import database.object.InfoObject;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import system.CommonUtil;
import system.InstanceRepo;
import user_interface.factory.NodeFactory;
import user_interface.factory.util.ColorUtil;
import user_interface.factory.util.enums.ColorType;
import user_interface.singleton.BaseNode;

import java.util.ArrayList;
import java.util.Comparator;

public class InfoListViewR extends VBox implements BaseNode, InstanceRepo {
    private final Label selectLabel = NodeFactory.getLabel("Selection", ColorType.DEF, ColorType.DEF);
    private final Label btnExpCol = NodeFactory.getLabel("Expand", ColorType.DEF, ColorType.ALT, ColorType.DEF, ColorType.NULL);
    private final TreeView<InfoListCell> treeView = new TreeView(new TreeItem());

    private final TextField textField = new TextField();
    private String actualText = "";

    public InfoListViewR() {
        VBox.setVgrow(treeView, Priority.ALWAYS);
        treeView.setShowRoot(false);
        treeView.expandedItemCountProperty().addListener((observable, oldValue, newValue) -> {
            treeView.lookupAll(".scroll-bar").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
            treeView.lookupAll(".increment-button").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
            treeView.lookupAll(".decrement-button").forEach(sb -> sb.setStyle("-fx-background-color: transparent;"));
            treeView.lookupAll(".thumb").forEach(sb -> sb.setStyle("-fx-background-color: gray; -fx-background-insets: 0 4 0 4;"));
        });
        NodeFactory.addNodeToBackgroundManager(treeView, ColorType.DEF);

        BorderPane bp = new BorderPane();
        bp.setCenter(selectLabel);
        bp.setRight(btnExpCol);
        bp.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

        selectLabel.setFont(CommonUtil.getFont());

        btnExpCol.setFont(CommonUtil.getFont());
        btnExpCol.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 0, 1))));
        btnExpCol.setPrefWidth(75);

        textField.setFont(CommonUtil.getFont());
        textField.setBorder(new Border(new BorderStroke(ColorUtil.getBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {
                actualText = actualText.substring(0, actualText.length() - 1);
            } else {
                actualText += event.getText();
            }
        });
        textField.setOnKeyTyped(event -> {
            if (actualText.equals("")) {
                textField.setText("");
                return;
            }
            boolean match = false;
            for (InfoObject infoObject : mainInfoList) {
                if (infoObject.getGroupAndName().toLowerCase().contains(actualText.toLowerCase())) {
                    String groupAndName = infoObject.getGroupAndName();
                    textField.setText(groupAndName);
                    int caretPos = groupAndName.toLowerCase().lastIndexOf(actualText.toLowerCase()) + actualText.length();
                    textField.positionCaret(caretPos);
                    match = true;
                    break;
                }
            }
            if (!match) {
                textField.setText(actualText);
                textField.positionCaret(textField.getLength());
            }
        });
        textField.setOnAction(event -> {
            InfoObject infoObject = mainInfoList.getInfoObject(textField.getText());
            if (infoObject != null) {
                this.addTagObjectToSelection(infoObject);
                textField.clear();
                actualText = "";
                reload.notifyChangeIn(Reload.Control.INFO);
                reload.doReload();
            }
        });
        NodeFactory.addNodeToBackgroundManager(textField, ColorType.ALT, ColorType.ALT, ColorType.DEF, ColorType.DEF);
        this.setPrefWidth(999);
        this.setMinWidth(200);
        this.setCellFactory();
        this.getChildren().addAll(bp, textField, treeView);
    }

    public void changeCellState(TreeCell<InfoListCell> sourceCell) {
        InfoListCell infoListCell;
        try { infoListCell = sourceCell.getTreeItem().getValue(); } catch (NullPointerException e) { return; }

        InfoObject infoObject = mainInfoList.getInfoObject(sourceCell);
        // if sourceCell is group level
        if (infoObject != null) {
            InfoListCell parentTreeCell = null;
            try {
                parentTreeCell = sourceCell.getTreeItem().getParent().getValue();
            } catch (NullPointerException ignored) {}

            if (infoListCell.getColor().equals(ColorUtil.getTextColorPos()) || infoListCell.getColor().equals(ColorUtil.getTextColorInt())) {
                if (parentTreeCell != null) parentTreeCell.setColor(ColorUtil.getTextColorDef());
                infoListCell.setColor(ColorUtil.getTextColorDef());
                this.removeTagObjectFromSelection(infoObject);
            } else {
                if (parentTreeCell != null) parentTreeCell.setColor(ColorUtil.getTextColorPos());
                infoListCell.setColor(ColorUtil.getTextColorPos());
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
                currentTargetedItem.getInfoObjectList().add(infoObject);
            }
        } else {
            select.addTagObject(infoObject);
        }
    }
    public void removeTagObjectFromSelection(InfoObject infoObject) {
        if (select.size() < 1) {
            DataObject currentTargetedItem = target.getCurrentTarget();
            if (currentTargetedItem != null) {
                currentTargetedItem.getInfoObjectList().remove(infoObject);
            }
        } else {
            select.removeTagObject(infoObject);
        }
    }

    public void reload() {
        ObservableList<TreeItem<InfoListCell>> treeViewItems = treeView.getRoot().getChildren();

        int treeViewItemsCountBefore = treeViewItems.size();
        ArrayList<Integer> expandedItemIndices = new ArrayList<>();
        for (TreeItem<InfoListCell> treeItem : treeViewItems) {
            if (treeItem.isExpanded()) {
                expandedItemIndices.add(treeViewItems.indexOf(treeItem));
            }
        }

        treeViewItems.clear();
        ArrayList<String> groupsInter = select.getIntersectingTags().getGroups();
        ArrayList<String> groupsShare = select.getSharedTags().getGroups();
        ArrayList<String> groupsAll = mainInfoList.getGroups();

        Color textColorDefault = ColorUtil.getTextColorDef();
        Color textColorPositive = ColorUtil.getTextColorPos();
        Color textColorIntersect = ColorUtil.getTextColorInt();

        for (String groupInter : groupsInter) {
            groupsShare.remove(groupInter);
            groupsAll.remove(groupInter);

            TreeItem groupTreeItem = new TreeItem(new InfoListCell(groupInter, textColorPositive));
            ArrayList<String> namesInter = select.getIntersectingTags().getNames(groupInter);
            ArrayList<String> namesShare = select.getSharedTags().getNames(groupInter);
            ArrayList<String> namesAll = mainInfoList.getNames(groupInter);

            for (String nameInter : namesInter) {
                namesShare.remove(nameInter);
                namesAll.remove(nameInter);

                groupTreeItem.getChildren().add(new TreeItem(new InfoListCell(nameInter, textColorPositive)));
            }
            for (String nameShare : namesShare) {
                namesAll.remove(nameShare);

                groupTreeItem.getChildren().add(new TreeItem(new InfoListCell(nameShare, textColorIntersect)));
            }
            for (String nameAll : namesAll) {
                groupTreeItem.getChildren().add(new TreeItem(new InfoListCell(nameAll, textColorDefault)));
            }

            treeViewItems.add(groupTreeItem);
        }
        for (String groupShare : groupsShare) {
            groupsAll.remove(groupShare);

            TreeItem groupTreeItem = new TreeItem(new InfoListCell(groupShare, textColorIntersect));
            ArrayList<String> namesShare = select.getSharedTags().getNames(groupShare);
            ArrayList<String> namesAll = mainInfoList.getNames(groupShare);

            for (String nameShare : namesShare) {
                namesAll.remove(nameShare);

                groupTreeItem.getChildren().add(new TreeItem(new InfoListCell(nameShare, textColorIntersect)));
            }
            for (String nameAll : namesAll) {
                groupTreeItem.getChildren().add(new TreeItem(new InfoListCell(nameAll, textColorDefault)));
            }

            treeViewItems.add(groupTreeItem);
        }
        for (String groupAll : groupsAll) {
            TreeItem groupTreeItem = new TreeItem(new InfoListCell(groupAll, textColorDefault));
            ArrayList<String> namesAll = mainInfoList.getNames(groupAll);

            for (String nameAll : namesAll) {
                groupTreeItem.getChildren().add(new TreeItem(new InfoListCell(nameAll, textColorDefault)));
            }

            treeViewItems.add(groupTreeItem);
        }

        treeViewItems.sort(Comparator.comparing(colorTextTreeItem -> colorTextTreeItem.getValue().getText()));

        if (treeViewItemsCountBefore == treeViewItems.size()) {
            for (TreeItem<InfoListCell> treeItem : treeViewItems) {
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
            protected void updateItem(InfoListCell infoListCell, boolean empty) {
                super.updateItem(infoListCell, empty);
                if (infoListCell == null) {
                    setText(null);
                    setTextFill(null);
                } else {
                    setText(infoListCell.getText());
                    setTextFill(infoListCell.getColor());
                }

                setFont(CommonUtil.getFont());
                setBackground(ColorUtil.getBackgroundDef());

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

    public TreeView<InfoListCell> getTreeView() {
        return treeView;
    }
    public Label getBtnExpCol() {
        return btnExpCol;
    }
}
