package project.common;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import project.control.FilterControl;
import project.control.FocusControl;
import project.control.SelectionControl;
import project.database.element.DataElement;
import project.gui.GUIControl;
import project.gui.GUIStage;

import java.util.ArrayList;

public abstract class Keybinds {
    public static void initialize(Scene mainScene) {
        mainScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R:
                    SelectionControl.setRandomValidDataElement(); break;
                case F12:
                    GUIControl.swapDisplayMode(); break;
                case Q:
                    SelectionControl.swapSelectionStateOf(FocusControl.getCurrentFocus()); break;
                case W:
                case A:
                case S:
                case D:
                    moveFocus(event.getCode()); break; //todo move this method somewhere else
                default:
                    break;
            }
        });
    }

    private static void moveFocus(KeyCode keyCode) {
        ArrayList<DataElement> databaseItemsFiltered = FilterControl.getValidDataElements();
        DataElement focusedItem = FocusControl.getCurrentFocus();
        if (focusedItem == null) {
            DataElement firstItem = FilterControl.getValidDataElements().get(0);
            FocusControl.setFocus(firstItem);
            focusedItem = firstItem;
        }

        int newFocusPosition = databaseItemsFiltered.indexOf(focusedItem);
        if (keyCode.equals(KeyCode.W)) {
            newFocusPosition -= GUIStage.getPaneGallery().getColumnCount();
        } else if (keyCode.equals(KeyCode.A)) {
            newFocusPosition -= 1;
        } else if (keyCode.equals(KeyCode.S)) {
            newFocusPosition += GUIStage.getPaneGallery().getColumnCount();
        } else if (keyCode.equals(KeyCode.D)) {
            newFocusPosition += 1;
        }

        if (newFocusPosition >= 0 && newFocusPosition < databaseItemsFiltered.size()) {
            FocusControl.setFocus(databaseItemsFiltered.get(newFocusPosition));
            GUIStage.getPaneGallery().adjustViewportToFocus();
        }
    }
}
