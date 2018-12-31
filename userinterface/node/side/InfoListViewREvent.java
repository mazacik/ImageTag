package userinterface.node.side;

import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseEvent;
import namespace.Namespace;
import userinterface.template.specific.InfoContextMenu;
import utils.MainUtil;

public class InfoListViewREvent implements MainUtil {
    private boolean isExpanded = false;

    public InfoListViewREvent() {
        onAction_btnExpCol();
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
        infoListViewR.changeCellState(sourceCell);
    }
    private static void onRightClick(TreeCell<CustomTreeCell> sourceCell, MouseEvent event) {
        InfoContextMenu infoContextMenu = mainStage.getInfoContextMenu();
        infoContextMenu.setInfoObject(mainListInfo.getInfoObject(sourceCell));
        infoContextMenu.show(infoListViewR, event.getX(), event.getY());
    }

    private void onAction_btnExpCol() {
        infoListViewR.getBtnExpCol().setOnAction(event -> {
            isExpanded = !isExpanded;
            if (isExpanded) infoListViewR.getBtnExpCol().setText(Namespace.GUI_SIDE_BTN_EXPCOL_STATE_TRUE.getValue());
            else infoListViewR.getBtnExpCol().setText(Namespace.GUI_SIDE_BTN_EXPCOL_STATE_FALSE.getValue());
            infoListViewR.getTreeView().getRoot().getChildren().forEach(node -> node.setExpanded(isExpanded));
        });
    }
}
