package project.database;

import project.database.part.DatabaseItem;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class ItemDatabase {
    /* variables */
    private static final ArrayList<DatabaseItem> databaseItems = new ArrayList<>();
    private static final ArrayList<DatabaseItem> databaseItemsFiltered = new ArrayList<>();
    private static final ArrayList<DatabaseItem> databaseItemsSelected = new ArrayList<>();

    /* public methods */
    public static void sort() {
        databaseItems.sort(Comparator.comparing(DatabaseItem::getName));
        databaseItemsFiltered.sort(Comparator.comparing(DatabaseItem::getName));
        databaseItemsSelected.sort(Comparator.comparing(DatabaseItem::getName));
    }

    /* getters */
    public static ArrayList<DatabaseItem> getDatabaseItems() {
        return databaseItems;
    }
    public static ArrayList<DatabaseItem> getDatabaseItemsFiltered() {
        return databaseItemsFiltered;
    }
    public static ArrayList<DatabaseItem> getDatabaseItemsSelected() {
        return databaseItemsSelected;
    }
}
