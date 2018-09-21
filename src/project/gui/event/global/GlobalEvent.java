package project.gui.event.global;

import project.control.MainControl;
import project.gui.GUIInstance;

public abstract class GlobalEvent {
    public static void initialize() {
        onKeyPressed();
    }

    private static void onKeyPressed() {
        GUIInstance.getInstance().getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Q:
                    MainControl.getSelectionControl().swapSelectionStateOf(MainControl.getFocusControl().getCurrentFocus());
                    break;
                case R:
                    MainControl.getSelectionControl().setRandomValidDataObject();
                    break;
                case F12:
                    GUIInstance.swapDisplayMode();
                    break;
                case W:
                case A:
                case S:
                case D:
                    MainControl.getFocusControl().moveFocusByKeyCode(event.getCode());
                    break;
                default:
                    break;
            }
            MainControl.getReloadControl().doReload();
        });
    }
}
