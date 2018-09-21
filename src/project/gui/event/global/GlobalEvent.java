package project.gui.event.global;

import project.control.Control;
import project.gui.GUIInstance;
import project.gui.GUIUtils;

public abstract class GlobalEvent {
    public static void initialize() {
        onKeyPressed();
    }

    private static void onKeyPressed() {
        GUIInstance.getInstance().getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Q:
                    Control.getSelectionControl().swapSelectionStateOf(Control.getFocusControl().getCurrentFocus());
                    break;
                case R:
                    Control.getSelectionControl().setRandomValidDataObject();
                    break;
                case F12:
                    GUIUtils.swapDisplayMode();
                    break;
                case W:
                case A:
                case S:
                case D:
                    Control.getFocusControl().moveFocusByKeyCode(event.getCode());
                    break;
                default:
                    break;
            }
            Control.getReloadControl().doReload();
        });
    }
}
