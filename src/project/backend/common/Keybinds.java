package project.backend.common;

import project.backend.database.Database;
import project.backend.database.DatabaseItem;
import project.frontend.shared.Frontend;

import java.util.ArrayList;
import java.util.Random;

public abstract class Keybinds {
    public static void initialize() {
        Frontend.getMainScene().setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case R:
                    ArrayList<DatabaseItem> databaseItemsFiltered = Database.getDatabaseItemsFiltered();
                    int databaseItemsFilteredSize = databaseItemsFiltered.size();
                    int randomIndex = new Random().nextInt(databaseItemsFilteredSize);
                    Selection.set(databaseItemsFiltered.get(randomIndex));
                    break;
                case F12:
                    Frontend.swapImageDisplayMode();
                    break;
                default:
                    break;
            }
        });
    }
}
