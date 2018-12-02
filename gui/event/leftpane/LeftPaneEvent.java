package gui.event.leftpane;

import gui.component.ColorText;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseEvent;
import utils.MainUtil;

public class LeftPaneEvent implements MainUtil {
    public static void onMouseClick(TreeCell<ColorText> sourceCell) {
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

    private static void onLeftClick(TreeCell<ColorText> sourceCell) {
        leftPane.changeCellState(sourceCell);
    }
    private static void onRightClick(TreeCell<ColorText> sourceCell, MouseEvent event) {
        sourceCell.getContextMenu().show(leftPane, event.getX(), event.getY());
    }
}
