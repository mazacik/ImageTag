package project.backend;

import project.gui_components.LeftPaneDisplayMode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static project.backend.ImageDisplayMode.MAXIMIZED;

public class Database {
    private static final ArrayList<DatabaseItem> itemDatabase = new ArrayList<>();
    private static final ArrayList<DatabaseItem> filteredItems = new ArrayList<>();
    private static final ArrayList<DatabaseItem> selectedItems = new ArrayList<>();
    private static final ArrayList<String> tagDatabase = new ArrayList<>();
    private static final ArrayList<String> tagsWhitelist = new ArrayList<>();
    private static final ArrayList<String> tagsBlacklist = new ArrayList<>();
    private static final File[] validFiles = new File(Settings.DIRECTORY_PATH).listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".JPG") || name.endsWith(".PNG") || name.endsWith(".jpeg") || name.endsWith(".JPEG"));
    private static final int fileCount = validFiles != null ? validFiles.length : 0;
    private static DatabaseItem lastSelectedItem = null;

    public static void addToSelection(DatabaseItem databaseItem) {
        selectedItems.add(databaseItem);
        setLastSelectedItem(databaseItem);
        databaseItem.getImageView().setEffect(Main.getGalleryTileHighlightEffect());
        selectionChanged();
    }

    public static void removeIndexFromSelection(DatabaseItem databaseItem) {
        selectedItems.remove(databaseItem);
        databaseItem.getImageView().setEffect(null);
        selectionChanged();
    }

    public static void clearSelection() {
        selectedItems.clear();
        Main.getLeftPane().getListView().getSelectionModel().clearSelection();
        Main.getRightPane().getListView().getItems().clear();
        for (DatabaseItem databaseItem : itemDatabase) {
            if (databaseItem.getImageView().getEffect() != null)
                databaseItem.getImageView().setEffect(null);
        }
    }

    private static void selectionChanged() {
        if (Main.getLeftPane().getDisplayMode() == LeftPaneDisplayMode.NAMES)
            Main.getLeftPane().getListView().getSelectionModel().clearSelection();
        for (DatabaseItem item : selectedItems)
            Main.getLeftPane().getListView().getSelectionModel().select(item.getColoredText());
        if (Main.getImageDisplayMode().equals(MAXIMIZED))
            Main.getPreviewPane().drawPreview();
        Main.getRightPane().getListView().getItems().setAll(getSelectedItemsSharedTags());
    }

    public static void filter() {
        filteredItems.clear();
        if (tagsWhitelist.isEmpty() && tagsBlacklist.isEmpty())
            filteredItems.addAll(itemDatabase);
        else
            for (DatabaseItem item : itemDatabase)
                if (item.getTags().containsAll(tagsWhitelist)) {
                    filteredItems.add(item);
                    for (String tag : tagsBlacklist)
                        if (item.getTags().contains(tag)) {
                            filteredItems.remove(item);
                            break;
                        }
                }
    }

    public static void writeToDisk() {
        writeToDisk(Database.getItemDatabase());
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

    public static ArrayList<String> getSelectedItemsSharedTags() {
        if (selectedItems.isEmpty()) return new ArrayList<>();
        ArrayList<String> sharedTags = new ArrayList<>();
        ArrayList<String> firstItemTags = selectedItems.get(0).getTags();
        for (String tag : firstItemTags)
            for (DatabaseItem databaseItem : selectedItems) {
                if (databaseItem.getTags().contains(tag)) {
                    if (databaseItem.equals(selectedItems.get(selectedItems.size() - 1)))
                        sharedTags.add(tag);
                    continue;
                }
                break;
            }
        return sharedTags;
    }

    public static ArrayList<DatabaseItem> getFilteredItems() {
        return filteredItems;
    }

    public static ArrayList<DatabaseItem> getSelectedItems() {
        return selectedItems;
    }

    public static File[] getValidFiles() {
        return validFiles;
    }

    public static DatabaseItem getLastSelectedItem() {
        return lastSelectedItem;
    }

    public static int getFileCount() {
        return fileCount;
    }

    public static ArrayList<DatabaseItem> getItemDatabase() {
        return itemDatabase;
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

    public static void setLastSelectedItem(DatabaseItem databaseItem) {
        lastSelectedItem = databaseItem;
    }
}