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
import namespace.Namespace;
import settings.SettingsNamespace;
import userinterface.BackgroundEnum;
import userinterface.node.BaseNode;
import utils.CommonUtil;
import utils.MainUtil;

import java.util.ArrayList;

public class InfoListViewL extends VBox implements BaseNode, MainUtil {
    private final TreeView<CustomTreeCell> treeView;
    private final Button btnExpCol = new Button(Namespace.GUI_SIDE_BTN_EXPCOL_STATE_FALSE.getValue());
    private final Button btnNew = new Button("New");

    public InfoListViewL() {
        this.setMinWidth(200);
        this.setPrefWidth(250);
        this.setMaxWidth(300);
        this.setSpacing(settings.valueOf(SettingsNamespace.GLOBAL_PADDING));
        //this.setPadding(new Insets(settings.valueOf(SettingsNamespace.GLOBAL_PADDING)));

        treeView = new TreeView(new TreeItem());
        treeView.setShowRoot(false);
        VBox.setVgrow(treeView, Priority.ALWAYS);

        this.setCellFactory();

        btnExpCol.setBackground(BackgroundEnum.NIGHT_1.getValue());
        btnNew.setBackground(BackgroundEnum.NIGHT_1.getValue());
        treeView.setBackground(BackgroundEnum.NIGHT_1.getValue());

        btnExpCol.setPrefWidth(this.getPrefWidth());
        btnNew.setPrefWidth(this.getPrefWidth());
        btnExpCol.setMaxWidth(this.getMaxWidth());
        btnNew.setMaxWidth(this.getMaxWidth());

        btnExpCol.setTextFill(Color.LIGHTGRAY);
        btnExpCol.setFont(CommonUtil.getFont());
        btnNew.setTextFill(Color.LIGHTGRAY);
        btnNew.setFont(CommonUtil.getFont());

        btnExpCol.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        btnNew.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1, 0, 0, 0))));

        this.getChildren().addAll(btnExpCol, treeView, btnNew);
    }

    public void changeCellState(TreeCell<CustomTreeCell> sourceCell) {
        CustomTreeCell customTreeCell;
        try { customTreeCell = sourceCell.getTreeItem().getValue(); } catch (NullPointerException e) { return; }

        InfoObject infoObject = mainListInfo.getInfoObject(sourceCell);
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
        reload.doReload();
        treeView.refresh();
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

                this.setBackground(BackgroundEnum.NIGHT_1.getValue());

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
