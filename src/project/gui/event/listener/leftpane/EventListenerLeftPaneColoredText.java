package project.gui.event.listener.leftpane;

import javafx.scene.control.TreeCell;
import project.gui.component.leftpane.ColoredText;
import project.gui.event.handler.leftpane.EventHandlerLeftPaneColoredText;

public abstract class EventListenerLeftPaneColoredText {
    public static void onMouseClick(TreeCell<ColoredText> sourceCell) {
        sourceCell.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    EventHandlerLeftPaneColoredText.onLeftClick(sourceCell);
                    break;
                default:
                    break;
            }
        });
    }
}
