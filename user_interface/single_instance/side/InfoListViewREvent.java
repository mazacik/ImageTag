package user_interface.single_instance.side;

import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseEvent;
import system.InstanceRepo;
import user_interface.node_factory.template.InfoContextMenu;

public class InfoListViewREvent implements InstanceRepo {
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
        infoContextMenu.setInfoObject(mainInfoList.getInfoObject(sourceCell));
        infoContextMenu.show(infoListViewR, event.getX(), event.getY());
    }

    private void onAction_btnExpCol() {
        infoListViewR.getBtnExpCol().setOnMouseClicked(event -> {
            isExpanded = !isExpanded;
            if (isExpanded) infoListViewR.getBtnExpCol().setText("Collapse");
            else infoListViewR.getBtnExpCol().setText("Expand");
            infoListViewR.getTreeView().getRoot().getChildren().forEach(node -> node.setExpanded(isExpanded));
        });
    }
}
