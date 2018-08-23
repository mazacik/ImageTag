package project.gui.event.listener.leftpane;

import javafx.scene.control.TreeCell;
import project.gui.component.leftpane.ColoredText;
import project.gui.event.handler.leftpane.EventHandlerLeftPaneColoredText;

public abstract class EventListenerLeftPaneColoredText {
    public static void onMouseClick(TreeCell<ColoredText> source) {
        source.setOnMouseClicked(event -> {
            switch (event.getButton()) {
                case PRIMARY:
                    EventHandlerLeftPaneColoredText.onLeftClick(source);
                    break;
                default:
                    break;
            }
        });
    }
}
