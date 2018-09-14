package project.gui.event.global;

import project.control.FocusControl;
import project.control.ReloadControl;
import project.control.SelectionControl;
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
                    SelectionControl.swapSelectionStateOf(FocusControl.getCurrentFocus());
                    break;
                case R:
                    SelectionControl.setRandomValidDataObject();
                    break;
                case F12:
                    GUIUtils.swapDisplayMode();
                    break;
                case W:
                case A:
                case S:
                case D:
                    FocusControl.moveFocusByKeyCode(event.getCode());
                    break;
                default:
                    break;
            }
            ReloadControl.doReload();
        });
    }
}
