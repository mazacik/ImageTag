package project.common;

import javafx.scene.Scene;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.gui.GUIControl;

public abstract class Keybinds {
    public static void initialize(Scene mainScene) {
        mainScene.setOnKeyPressed(event -> {
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
