package project.gui.event.handler.rightpane;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.gui.component.rightpane.RightPane;
import project.gui.custom.specific.RightPaneContextMenu;

public abstract class EventHandlerRightPane {
    private static final RightPaneContextMenu contextMenu = RightPane.getContextMenu();
    private static final Region rightPane = RightPane.getInstance();

    public static void onLeftClick() {
        rightPane.requestFocus();
        contextMenu.hide();
    }
    public static void onRightClick(MouseEvent event) {
        SelectionControl.addDataElement(FocusControl.getCurrentFocus());
        contextMenu.show(rightPane, event.getScreenX(), event.getScreenY());
    }
}
