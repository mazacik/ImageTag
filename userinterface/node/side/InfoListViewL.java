package userinterface.node.side;

import database.object.InfoObject;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
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

public class InfoListViewL extends VBox implements BaseNode, InstanceRepo {
    private final TreeView<CustomTreeCell> treeView = new TreeView(new TreeItem());
    private final CustomButton btnExpCol = new CustomButton("Expand");
    private final CustomButton btnNew = new CustomButton("New");

    public InfoListViewL() {
        this.setMinWidth(200);
        this.setPrefWidth(250);
        this.setMaxWidth(300);

        VBox.setVgrow(treeView, Priority.ALWAYS);
        treeView.setShowRoot(false);
        treeView.setBackground(CommonUtil.getBackgroundDefault());

        btnExpCol.setPrefWidth(this.getPrefWidth());
        btnExpCol.setTextFill(CommonUtil.getTextColorDefault());
        btnExpCol.setFont(CommonUtil.getFont());
        btnExpCol.setBorder(new Border(new BorderStroke(CommonUtil.getNodeBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

        btnNew.setPrefWidth(this.getPrefWidth());
        btnNew.setTextFill(CommonUtil.getTextColorDefault());
        btnNew.setFont(CommonUtil.getFont());
        btnNew.setBorder(new Border(new BorderStroke(CommonUtil.getNodeBorderColor(), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 0, 0, 0))));

        this.setCellFactory();
        this.setSpacing(settings.valueOf(SettingsNamespace.GLOBAL_PADDING));
        this.getChildren().addAll(btnExpCol, treeView, btnNew);
    }

    public void changeCellState(TreeCell<CustomTreeCell> sourceCell) {
        CustomTreeCell customTreeCell;
        try { customTreeCell = sourceCell.getTreeItem().getValue(); } catch (NullPointerException e) { return; }

        InfoObject infoObject = mainListInfo.getInfoObject(sourceCell);
        // if sourceCell is group level
        if (infoObject == null) {
            String groupName = customTreeCell.getText();
            Color textColor;
            if (filter.isGroupWhitelisted(groupName)) {
                filter.blacklistGroup(groupName);
                textColor = CommonUtil.getTextColorNegative();
            } else if (filter.isGroupBlacklisted(groupName)) {
                filter.unlistGroup(groupName);
                textColor = CommonUtil.getTextColorDefault();
            } else {
                filter.whitelistGroup(groupName);
                textColor = CommonUtil.getTextColorPositive();
            }
            customTreeCell.setColor(textColor);
            sourceCell.getTreeItem().getChildren().forEach(cell -> cell.getValue().setColor(textColor));
        } else {
            if (filter.isTagObjectWhitelisted(infoObject)) {
                filter.blacklistTagObject(infoObject);
                customTreeCell.setColor(CommonUtil.getTextColorNegative());
            } else if (filter.isTagObjectBlacklisted(infoObject)) {
                filter.unlistTagObject(infoObject);
                customTreeCell.setColor(CommonUtil.getTextColorDefault());
            } else {
                filter.whitelistTagObject(infoObject);
                customTreeCell.setColor(CommonUtil.getTextColorPositive());
            }
        }
        filter.apply();
        reload.doReload();
        treeView.refresh();
    }

    public void reload() {
        ObservableList<TreeItem<CustomTreeCell>> treeViewItems = treeView.getRoot().getChildren();
        treeViewItems.clear();

        Color textColorDefault = CommonUtil.getTextColorDefault();
        Color textColorPositive = CommonUtil.getTextColorPositive();
        Color textColorNegative = CommonUtil.getTextColorNegative();

        ArrayList<String> groupNames = mainListInfo.getGroups();
        for (String groupName : groupNames) {
            TreeItem groupTreeItem;
            if (filter.isGroupWhitelisted(groupName)) {
                groupTreeItem = new TreeItem(new CustomTreeCell(groupName, textColorPositive));
            } else if (filter.isGroupBlacklisted(groupName)) {
                groupTreeItem = new TreeItem(new CustomTreeCell(groupName, textColorNegative));
            } else {
                groupTreeItem = new TreeItem(new CustomTreeCell(groupName, textColorDefault));
            }

            for (String tagName : mainListInfo.getNames(groupName)) {
                if (filter.isTagObjectWhitelisted(groupName, tagName)) {
                    groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(tagName, textColorPositive)));
                } else if (filter.isTagObjectBlacklisted(groupName, tagName)) {
                    groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(tagName, textColorNegative)));
                } else {
                    groupTreeItem.getChildren().add(new TreeItem(new CustomTreeCell(tagName, textColorDefault)));
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
                    setFont(CommonUtil.getFont());
                    setText(customTreeCell.getText());
                    setTextFill(customTreeCell.getColor());
                }

                this.setBackground(CommonUtil.getBackgroundDefault());

                InfoListViewLEvent.onMouseClick(this);
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
    public Button getBtnNew() {
        return btnNew;
    }
}
