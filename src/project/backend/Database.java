package project.backend;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final ArrayList<DatabaseItem> itemDatabase = new ArrayList<>();
    private static final ArrayList<DatabaseItem> itemDatabaseFiltered = new ArrayList<>();
    private static final ArrayList<String> tagDatabase = new ArrayList<>();
    private static final ArrayList<String> tagsWhitelist = new ArrayList<>();
    private static final ArrayList<String> tagsBlacklist = new ArrayList<>();
    private static final ArrayList<Integer> selectedIndexes = new ArrayList<>();
    private static final File[] validFiles = new File(Settings.DIRECTORY_PATH).listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".JPG") || name.endsWith(".PNG") || name.endsWith(".jpeg") || name.endsWith(".JPEG"));
    private static final int fileCount = validFiles != null ? validFiles.length : 0;
    private static int lastSelectedIndex = 0;

    public static void addToSelection(int index) {
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

    public static void writeToDisk(ArrayList<DatabaseItem> itemDatabase) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(Settings.DIRECTORY_PATH + "/database"));
            objectOutputStream.writeObject(itemDatabase);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<DatabaseItem> readFromDisk() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(Settings.DIRECTORY_PATH + "/database"));
            ArrayList<DatabaseItem> itemDatabase = (ArrayList<DatabaseItem>) objectInputStream.readObject();
            objectInputStream.close();
            return itemDatabase;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static File[] getValidFiles() {
        return validFiles;
    }

    public static int getLastSelectedIndex() {
        return lastSelectedIndex;
    }

    public static ArrayList<Integer> getSelectedIndexes() {
        return selectedIndexes;
    }

    public static int getFileCount() {
        return fileCount;
    }

    public static ArrayList<DatabaseItem> getItemDatabase() {
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