package project.userinput;

import project.control.FocusControl;
import project.control.SelectionControl;
import project.gui.GUIControl;
import project.gui.GUIMain;

public abstract class UserInputGlobal {
    public static void initialize() {
        GUIMain.getInstance().getScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Q:
                    SelectionControl.swapSelectionStateOf(FocusControl.getCurrentFocus()); break;
                case R:
                    SelectionControl.setRandomValidDataElement(); break;
                case F12:
                    GUIControl.swapDisplayMode(); break;
                case W:
                case A:
                case S:
                case D:
                    FocusControl.moveFocusByKeyCode(event.getCode()); break;
                default:
                    break;
            }
        });
    }
}
