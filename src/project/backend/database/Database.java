package project.backend.database;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class Database {
    private static final ArrayList<DatabaseItem> databaseItems = new ArrayList<>();
    private static final ArrayList<DatabaseItem> databaseItemsFiltered = new ArrayList<>();
    private static final ArrayList<DatabaseItem> databaseItemsSelected = new ArrayList<>();
    private static final ArrayList<String> databaseTags = new ArrayList<>();
    private static final ArrayList<String> databaseTagsWhitelist = new ArrayList<>();
    private static final ArrayList<String> databaseTagsBlacklist = new ArrayList<>();

    public static void sort() {
        Database.getDatabaseItems().sort(Comparator.comparing(DatabaseItem::getName));
        Database.getDatabaseItemsFiltered().sort(Comparator.comparing(DatabaseItem::getName));
        Database.getDatabaseItemsSelected().sort(Comparator.comparing(DatabaseItem::getName));
    }

    public static ArrayList<DatabaseItem> getDatabaseItems() {
        return databaseItems;
    }

    public static ArrayList<DatabaseItem> getDatabaseItemsFiltered() {
        return databaseItemsFiltered;
    }

    public static ArrayList<DatabaseItem> getDatabaseItemsSelected() {
        return databaseItemsSelected;
    }

    public static ArrayList<String> getDatabaseTags() {
        return databaseTags;
    }

    public static ArrayList<String> getDatabaseTagsWhitelist() {
        return databaseTagsWhitelist;
    }

    public static ArrayList<String> getDatabaseTagsBlacklist() {
        return databaseTagsBlacklist;
    }

}
