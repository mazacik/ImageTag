package project.backend;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import project.GUIController;
import project.database.ItemDatabase;
import project.database.part.DatabaseItem;
import project.gui.component.gallery.GalleryPaneBack;
import project.gui.component.gallery.GalleryPaneFront;

import java.util.ArrayList;

public class Keybinds {
    /* lazy singleton */
    private static Keybinds instance;
    public static Keybinds getInstance(Scene mainScene) {
        if (instance == null) instance = new Keybinds(mainScene);
        return instance;
    }

    /* imports */
    private final ArrayList<DatabaseItem> databaseItemsFiltered = ItemDatabase.getDatabaseItemsFiltered();

    /* constructors */
    private Keybinds(Scene mainScene) {
        mainScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R:
                    Selection.selectRandomItem(); break;
                case F12:
                    GUIController.swapPreviewMode(); break;
                case Q:
                    Selection.swap(GalleryPaneFront.getInstance().getCurrentFocusedItem()); break;
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

    /* private methods */
    private void moveFocus(KeyCode keyCode) {
        DatabaseItem focusedItem = GalleryPaneFront.getInstance().getCurrentFocusedItem();
        if (focusedItem == null) {
            DatabaseItem firstItem = ItemDatabase.getDatabaseItemsFiltered().get(0);
            GalleryPaneFront.getInstance().focusTile(firstItem);
            focusedItem = firstItem;
        }

        int newFocusPosition = databaseItemsFiltered.indexOf(focusedItem);
        if (keyCode.equals(KeyCode.W)) {
            newFocusPosition -= GalleryPaneBack.getInstance().getColumnCount();
        } else if (keyCode.equals(KeyCode.A)) {
            newFocusPosition -= 1;
        } else if (keyCode.equals(KeyCode.S)) {
            newFocusPosition += GalleryPaneBack.getInstance().getColumnCount();
        } else if (keyCode.equals(KeyCode.D)) {
            newFocusPosition += 1;
        }

        if (newFocusPosition >= 0 && newFocusPosition < databaseItemsFiltered.size()) {
            GalleryPaneFront.getInstance().focusTile(databaseItemsFiltered.get(newFocusPosition));
            GalleryPaneBack.getInstance().adjustViewportPositionToFocus();
        }
    }
}
