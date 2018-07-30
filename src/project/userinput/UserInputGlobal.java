package project.userinput;

import project.control.FocusControl;
import project.control.ReloadControl;
import project.control.SelectionControl;
import project.gui.GUI_Instance;
import project.gui.GUI_Utility;

public abstract class UserInputGlobal {
    public static void initialize() {
        GUI_Instance.getInstance().getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Q:
                    SelectionControl.swapSelectionStateOf(FocusControl.getCurrentFocus()); break;
                case R:
                    SelectionControl.setRandomValidDataElement(); break;
                case F12:
                    GUI_Utility.swapDisplayMode(); break;
                case W:
                case A:
                case S:
                case D:
                    FocusControl.moveFocusByKeyCode(event.getCode()); break;
                default:
                    break;
            }
            ReloadControl.forceReload();
        });
    }
}
