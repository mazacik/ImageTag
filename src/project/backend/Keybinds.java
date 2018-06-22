package project.backend;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import project.database.ItemDatabase;
import project.database.part.DatabaseItem;
import project.gui.GUIController;
import project.gui.GUIStage;
import project.gui.component.GalleryPane;

import java.util.ArrayList;

public class Keybinds {
    public Keybinds(Scene mainScene) {
        mainScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R:
                    Selection.selectRandomItem(); break;
                case F12:
                    GUIController.swapDisplayMode(); break;
                case Q:
                    Selection.swap(GUIStage.getGalleryPane().getCurrentFocusedItem()); break;
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
        GalleryPane galleryPane = GUIStage.getGalleryPane();
        DatabaseItem focusedItem = galleryPane.getCurrentFocusedItem();
        if (focusedItem == null) {
            DatabaseItem firstItem = ItemDatabase.getDatabaseItemsFiltered().get(0);
            galleryPane.focusTile(firstItem);
            focusedItem = firstItem;
        }

        int newFocusPosition = databaseItemsFiltered.indexOf(focusedItem);
        if (keyCode.equals(KeyCode.W)) {
            newFocusPosition -= GUIStage.getGalleryPane().getColumnCount();
        } else if (keyCode.equals(KeyCode.A)) {
            newFocusPosition -= 1;
        } else if (keyCode.equals(KeyCode.S)) {
            newFocusPosition += GUIStage.getGalleryPane().getColumnCount();
        } else if (keyCode.equals(KeyCode.D)) {
            newFocusPosition += 1;
        }

        if (newFocusPosition >= 0 && newFocusPosition < databaseItemsFiltered.size()) {
            galleryPane.focusTile(databaseItemsFiltered.get(newFocusPosition));
            GUIStage.getGalleryPane().adjustViewportToFocus();
        }
    }
}
