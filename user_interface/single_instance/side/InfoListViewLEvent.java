package user_interface.single_instance.side;

import control.reload.Reload;
import database.object.InfoObject;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseEvent;
import system.InstanceRepo;
import user_interface.node_factory.template.InfoContextMenu;
import user_interface.node_factory.template.InfoObjectEditor;

public class InfoListViewLEvent implements InstanceRepo {
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
        infoListViewL.getBtnExpCol().setOnMouseClicked(event -> {
            isExpanded = !isExpanded;
            if (isExpanded) infoListViewL.getBtnExpCol().setText("Collapse");
            else infoListViewL.getBtnExpCol().setText("Expand");
            infoListViewL.getTreeView().getRoot().getChildren().forEach(node -> node.setExpanded(isExpanded));
        });
    }
    private void onAction_btnNew() {
        infoListViewL.getBtnNew().setOnMouseClicked(event -> {
            InfoObject newInfoObject = new InfoObjectEditor().getResult();
            mainListInfo.add(newInfoObject);
            reload.notifyChangeIn(Reload.Control.INFO);
            reload.doReload();
        });
    }
}
