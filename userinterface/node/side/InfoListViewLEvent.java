package userinterface.node.side;

import control.reload.Reload;
import database.object.InfoObject;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseEvent;
import namespace.Namespace;
import userinterface.template.specific.InfoContextMenu;
import userinterface.template.specific.InfoObjectEditor;
import utils.MainUtil;

public class InfoListViewLEvent implements MainUtil {
    private boolean isExpanded = false;

    public InfoListViewLEvent() {
        onAction_btnExpCol();
        onAction_btnNew();
    }

    public static void onMouseClick(TreeCell<CustomTreeCell> sourceCell) {
        sourceCell.setOnMouseClicked(event -> {
            if (event.getX() > 25) {
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
            }
        });
    }
    private static void onLeftClick(TreeCell<CustomTreeCell> sourceCell) {
        mainStage.getInfoContextMenu().hide();
        infoListViewL.changeCellState(sourceCell);
    }
    private static void onRightClick(TreeCell<CustomTreeCell> sourceCell, MouseEvent event) {
        InfoContextMenu infoContextMenu = mainStage.getInfoContextMenu();
        infoContextMenu.setInfoObject(mainListInfo.getInfoObject(sourceCell));
        infoContextMenu.show(infoListViewL, event.getX(), event.getY());
    }

    private void onAction_btnExpCol() {
        infoListViewL.getBtnExpCol().setOnAction(event -> {
            isExpanded = !isExpanded;
            if (isExpanded) infoListViewL.getBtnExpCol().setText(Namespace.GUI_SIDE_BTN_EXPCOL_STATE_TRUE.getValue());
            else infoListViewL.getBtnExpCol().setText(Namespace.GUI_SIDE_BTN_EXPCOL_STATE_FALSE.getValue());
            infoListViewL.getTreeView().getRoot().getChildren().forEach(node -> node.setExpanded(isExpanded));
        });
    }
    private void onAction_btnNew() {
        infoListViewL.getBtnNew().setOnAction(event -> {
            InfoObject newInfoObject = new InfoObjectEditor().getResult();
            mainListInfo.add(newInfoObject);
            reload.notifyChangeIn(Reload.Control.INFO);
        });
    }
}
