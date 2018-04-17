package project.backend;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final int fileCount = DatabaseBuilder.getFileCount();
    private static final List<DatabaseItem> itemDatabase = new ArrayList<>();
    private static final List<DatabaseItem> itemDatabaseFiltered = new ArrayList<>();
    private static final List<String> tagDatabase = new ArrayList<>();
    private static final List<String> tagsWhitelist = new ArrayList<>();
    private static final List<String> tagsBlacklist = new ArrayList<>();
    private static final List<Integer> selectedIndexes = new ArrayList<>();
    private static int lastSelectedIndex = 0;
    //private static int previousIndex = 0;
    //private static int currentIndex = 0;

    public static void addIndexToSelection(int index) {
        if (index < 0 || index >= fileCount) return;
        selectedIndexes.add(index);
        selectedIndexes.sort(null);
        Main.selectionChanged();
    }

    public static void clearSelection() {
        selectedIndexes.clear();
        Main.selectionChanged();
    }

    public static void removeIndexFromSelection(int index) {
        selectedIndexes.remove(Integer.valueOf(index));
        selectedIndexes.sort(null);
        Main.selectionChanged();
    }

    public static void filter() {
        itemDatabaseFiltered.clear();
        if (tagsWhitelist.isEmpty() && tagsBlacklist.isEmpty())
            itemDatabaseFiltered.addAll(itemDatabase);
        else
            for (DatabaseItem item : itemDatabase)
                if (item.getTags().containsAll(tagsWhitelist)) {
                    itemDatabaseFiltered.add(item);
                    for (String tag : tagsBlacklist)
                        if (item.getTags().contains(tag)) {
                            itemDatabaseFiltered.remove(item);
                            break;
                        }
                }
        selectedIndexes.clear();
    }

    public static int getLastSelectedIndex() {
        return lastSelectedIndex;
    }

    public static List<Integer> getSelectedIndexes() {
        return selectedIndexes;
    }

    public static int getFileCount() {
        return fileCount;
    }

    public static List<DatabaseItem> getItemDatabase() {
        return itemDatabase;
    }

    public static List<DatabaseItem> getItemDatabaseFiltered() {
        return itemDatabaseFiltered;
    }

    public static List<String> getTagDatabase() {
        return tagDatabase;
    }

    public static List<String> getTagsBlacklist() {
        return tagsBlacklist;
    }

    public static List<String> getTagsWhitelist() {
        return tagsWhitelist;
    }

    public static void setLastSelectedIndex(int index) {
        lastSelectedIndex = index;
    }
}

//Main.getTopPane().getInfoLabel().setText(itemDatabaseFiltered.get(currentIndex).getSimpleName());