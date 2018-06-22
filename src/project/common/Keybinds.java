package project.common;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import project.database.ItemDatabase;
import project.database.Selection;
import project.database.part.DatabaseItem;
import project.gui.GUIStage;
import project.gui.GUIUtility;
import project.gui.component.PaneGallery;

import java.util.ArrayList;

public class Keybinds {
    public Keybinds(Scene mainScene) {
        mainScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R:
                    Selection.selectRandomItem(); break;
                case F12:
                    GUIUtility.swapDisplayMode(); break;
                case Q:
                    Selection.swapItemStatus(GUIStage.getPaneGallery().getCurrentFocusedItem()); break;
                case W:
                case A:
                case S:
                case D:
                    moveFocus(event.getCode()); break;
                default:
                    break;
            }
        });
    }

    private void moveFocus(KeyCode keyCode) {
        ArrayList<DatabaseItem> databaseItemsFiltered = ItemDatabase.getDatabaseItemsFiltered();
        PaneGallery paneGallery = GUIStage.getPaneGallery();
        DatabaseItem focusedItem = paneGallery.getCurrentFocusedItem();
        if (focusedItem == null) {
            DatabaseItem firstItem = ItemDatabase.getDatabaseItemsFiltered().get(0);
            paneGallery.focusTile(firstItem);
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
            paneGallery.focusTile(databaseItemsFiltered.get(newFocusPosition));
            GUIStage.getPaneGallery().adjustViewportToFocus();
        }
    }
}
