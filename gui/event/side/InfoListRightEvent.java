package gui.event.side;

import gui.node.side.CustomTreeCell;
import gui.template.specific.InfoContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseEvent;
import utils.MainUtil;

public class InfoListRightEvent implements MainUtil {
    public InfoListRightEvent() {
        onAction_btnExpand();
        onAction_btnCollapse();
    }

    public static void onMouseClick(TreeCell<CustomTreeCell> sourceCell) {
        sourceCell.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    onLeftClick(sourceCell);
                    break;
                case SECONDARY:
                    onRightClick(sourceCell, event);
                    break;
                default:
                    break;
            }
        });
    }

    private static void onLeftClick(TreeCell<CustomTreeCell> sourceCell) {
        mainStage.getInfoContextMenu().hide();
        infoListViewR.changeCellState(sourceCell);
    }
    private static void onRightClick(TreeCell<CustomTreeCell> sourceCell, MouseEvent event) {
        InfoContextMenu infoContextMenu = mainStage.getInfoContextMenu();
        infoContextMenu.setInfoObject(mainListInfo.getInfoObject(sourceCell));
        infoContextMenu.show(infoListViewR, event.getX(), event.getY());
    }

    private void onAction_btnExpand() {
        infoListViewR.getBtnExpand().setOnAction(event -> infoListViewR.getTreeView().getRoot().getChildren().forEach(node -> node.setExpanded(true)));
    }
    private void onAction_btnCollapse() {
        infoListViewR.getBtnCollapse().setOnAction(event -> infoListViewR.getTreeView().getRoot().getChildren().forEach(node -> node.setExpanded(false)));
    }
}
