package gui.event.side;

import control.reload.Reload;
import database.object.InfoObject;
import gui.node.side.CustomTreeCell;
import gui.template.specific.InfoContextMenu;
import gui.template.specific.InfoObjectEditor;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseEvent;
import utils.MainUtil;

public class InfoListLeftEvent implements MainUtil {
    public InfoListLeftEvent() {
        onAction_btnExpand();
        onAction_btnCollapse();
        onAction_btnNew();
    }
    private static void onLeftClick(TreeCell<CustomTreeCell> sourceCell) {
        mainStage.getInfoContextMenu().hide();
        infoListViewL.changeCellState(sourceCell);
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
    private static void onRightClick(TreeCell<CustomTreeCell> sourceCell, MouseEvent event) {
        InfoContextMenu infoContextMenu = mainStage.getInfoContextMenu();
        infoContextMenu.setInfoObject(mainListInfo.getInfoObject(sourceCell));
        infoContextMenu.show(infoListViewL, event.getX(), event.getY());
    }

    private void onAction_btnExpand() {
        infoListViewL.getBtnExpand().setOnAction(event -> infoListViewL.getTreeView().getRoot().getChildren().forEach(node -> node.setExpanded(true)));
    }
    private void onAction_btnCollapse() {
        infoListViewL.getBtnCollapse().setOnAction(event -> infoListViewL.getTreeView().getRoot().getChildren().forEach(node -> node.setExpanded(false)));
    }
    private void onAction_btnNew() {
        infoListViewL.getBtnNew().setOnAction(event -> {
            InfoObject newInfoObject = new InfoObjectEditor().getResult();
            mainListInfo.add(newInfoObject);
            reload.notifyChangeIn(Reload.Control.INFO);
        });
    }
}
