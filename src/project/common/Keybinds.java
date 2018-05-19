package project.common;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import project.component.gallery.GalleryPaneBack;
import project.component.gallery.GalleryPaneFront;
import project.database.Database;
import project.database.DatabaseItem;

import java.util.ArrayList;
import java.util.Random;

public class Keybinds {
    /* lazy singleton */
    private static Keybinds instance;
    public static Keybinds getInstance(Scene mainScene) {
        if (instance == null) instance = new Keybinds(mainScene);
        return instance;
    }

    /* imports */
    private final ArrayList<DatabaseItem> databaseItemsFiltered = Database.getDatabaseItemsFiltered();

    /* constructors */
    private Keybinds(Scene mainScene) {
        mainScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R:
                    randomSelect(); break;
                case F12:
                    swapImageDisplayMode(); break;
                case Q:
                    Selection.getInstance().swap(GalleryPaneFront.getInstance().getFocusedItem()); break;
                case W:
                case A:
                case S:
                case D:
                    moveFocusMarker(event.getCode()); break;
                default:
                    break;
            }
        });
    }

    /* private methods */
    private void randomSelect() {
        ArrayList<DatabaseItem> databaseItemsFiltered = Database.getDatabaseItemsFiltered();
        int databaseItemsFilteredSize = databaseItemsFiltered.size();
        int randomIndex = new Random().nextInt(databaseItemsFilteredSize);
        Selection.getInstance().set(databaseItemsFiltered.get(randomIndex));
    }

    private void swapImageDisplayMode() {
        Utility.swapImageDisplayMode();
    }

    private void moveFocusMarker(KeyCode keyCode) {
        DatabaseItem focusedItem = GalleryPaneFront.getInstance().getFocusedItem();
        if (focusedItem == null) return;

        int newMarkPosition = databaseItemsFiltered.indexOf(focusedItem);

        if (keyCode.equals(KeyCode.W)) {
            newMarkPosition -= GalleryPaneBack.getInstance().getColumnCount();
        } else if (keyCode.equals(KeyCode.A)) {
            newMarkPosition -= 1;
        } else if (keyCode.equals(KeyCode.S)) {
            newMarkPosition += GalleryPaneBack.getInstance().getColumnCount();
        } else if (keyCode.equals(KeyCode.D)) {
            newMarkPosition += 1;
        }

        DatabaseItem newMarkedItem = databaseItemsFiltered.get(newMarkPosition);
        GalleryPaneFront.getInstance().markGalleryTile(newMarkedItem);
    }
}
