package gui.event.rightpane;

import gui.node.ColorText;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseEvent;
import utils.MainUtil;

public class RightPaneEvent implements MainUtil {
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
        rightPane.changeCellState(sourceCell);
    }
    private static void onRightClick(TreeCell<ColorText> sourceCell, MouseEvent event) {
        sourceCell.getContextMenu().show(rightPane, event.getX(), event.getY());
    }
}
