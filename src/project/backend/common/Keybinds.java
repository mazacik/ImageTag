package project.backend.common;

import project.backend.database.Database;
import project.backend.database.DatabaseItem;
import project.frontend.Frontend;

import java.util.ArrayList;
import java.util.Random;

public class Keybinds {
    private static final Keybinds instance = new Keybinds();


    private Keybinds() {
        Frontend.getMainScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R:
                    randomSelect(); break;
                case F12:
                    swapImageDisplayMode(); break;
                default:
                    break;
            }
        });
    }

    private static void randomSelect() {
        ArrayList<DatabaseItem> databaseItemsFiltered = Database.getDatabaseItemsFiltered();
        int databaseItemsFilteredSize = databaseItemsFiltered.size();
        int randomIndex = new Random().nextInt(databaseItemsFilteredSize);
        Selection.set(databaseItemsFiltered.get(randomIndex));
    }

    private static void swapImageDisplayMode() {
        Frontend.swapImageDisplayMode();
    }

    public static Keybinds getInstance() {
        return instance;
    }
}
