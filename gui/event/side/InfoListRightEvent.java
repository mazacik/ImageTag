package gui.event.side;

import gui.node.side.CustomTreeCell;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseEvent;
import utils.MainUtil;

public class InfoListRightEvent implements MainUtil {
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
        infoListR.changeCellState(sourceCell);
    }
    private static void onRightClick(TreeCell<CustomTreeCell> sourceCell, MouseEvent event) {
        sourceCell.getContextMenu().show(infoListR, event.getX(), event.getY());
    }
}
