package project.backend.shared;

import project.frontend.shared.Frontend;

import java.io.*;
import java.util.ArrayList;

public class Database {
    private static final ArrayList<DatabaseItem> itemDatabase = new ArrayList<>();
    private static final ArrayList<DatabaseItem> filteredItems = new ArrayList<>();
    private static final ArrayList<DatabaseItem> selectedItems = new ArrayList<>();
    private static final ArrayList<String> tagDatabase = new ArrayList<>();
    private static final ArrayList<String> tagsWhitelist = new ArrayList<>();
    private static final ArrayList<String> tagsBlacklist = new ArrayList<>();
    private static File[] validFiles;
    private static int fileCount;
    private static DatabaseItem selectedItem = null;

    static void initilize() {
        validFiles =
                new File(Backend.DIRECTORY_PATH)
                        .listFiles(
                                (dir, name) ->
                                        name.endsWith(".jpg")
                                                || name.endsWith(".png")
                                                || name.endsWith(".JPG")
                                                || name.endsWith(".PNG")
                                                || name.endsWith(".jpeg")
                                                || name.endsWith(".JPEG"));
        fileCount = validFiles != null ? validFiles.length : 0;
    }

    public static void addToSelection(DatabaseItem databaseItem) {
        selectedItems.add(databaseItem);
        setSelectedItem(databaseItem);
        databaseItem.getImageView().setEffect(Frontend.getGalleryPane().getHighlightEffect());
        selectionChanged();
    }

    static void selectionChanged() {
        Frontend.getNamePane().getListView().getSelectionModel().clearSelection();
        for (DatabaseItem item : selectedItems)
            Frontend.getNamePane().getListView().getSelectionModel().select(item.getColoredText());
        Frontend.getNamePane().getListView().scrollTo(selectedItem.getColoredText());
        if (Frontend.getMainBorderPane().getCenter().equals(Frontend.getPreviewPane()))
            Backend.getPreviewPane().drawPreview();
        // Frontend.getGalleryPane().setVvalue(0.5);
        Frontend.getRightPane().getListView().getItems().setAll(getSelectedItemsSharedTags());
    }

    public static ArrayList<String> getSelectedItemsSharedTags() {
        if (selectedItems.isEmpty()) return new ArrayList<>();
        ArrayList<String> sharedTags = new ArrayList<>();
        ArrayList<String> firstItemTags = selectedItems.get(0).getTags();
        for (String tag : firstItemTags)
            for (DatabaseItem databaseItem : selectedItems) {
                if (databaseItem.getTags().contains(tag)) {
                    if (databaseItem.equals(selectedItems.get(selectedItems.size() - 1))) sharedTags.add(tag);
                    continue;
                }
                break;
            }
        return sharedTags;
    }

    public static void filterByTags() {
        filteredItems.clear();
        if (tagsWhitelist.isEmpty() && tagsBlacklist.isEmpty()) filteredItems.addAll(itemDatabase);
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

    public static void removeIndexFromSelection(DatabaseItem databaseItem) {
        selectedItems.remove(databaseItem);
        databaseItem.getImageView().setEffect(null);
        selectionChanged();
    }

    public static ArrayList<DatabaseItem> getItemDatabase() {
        return itemDatabase;
    }

    public static void clearSelection() {
        selectedItems.clear();
        Frontend.getNamePane().getListView().getSelectionModel().clearSelection();
        Frontend.getRightPane().getListView().getItems().clear();
        for (DatabaseItem databaseItem : itemDatabase) {
            if (databaseItem.getImageView().getEffect() != null)
                databaseItem.getImageView().setEffect(null);
        }
    }

    public static void writeToDisk() {
        writeToDisk(Database.getItemDatabase());
    }

    static void writeToDisk(ArrayList<DatabaseItem> itemDatabase) {
        try {
            ObjectOutputStream objectOutputStream =
                    new ObjectOutputStream(new FileOutputStream(Backend.DIRECTORY_PATH + "/database"));
            objectOutputStream.writeObject(itemDatabase);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setSelectedItem(DatabaseItem databaseItem) {
        selectedItem = databaseItem;
    }

    @SuppressWarnings("unchecked")
    static ArrayList<DatabaseItem> readFromDisk() {
        try {
            ObjectInputStream objectInputStream =
                    new ObjectInputStream(new FileInputStream(Backend.DIRECTORY_PATH + "/database"));
            ArrayList<DatabaseItem> itemDatabase =
                    (ArrayList<DatabaseItem>) objectInputStream.readObject();
            objectInputStream.close();
            return itemDatabase;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(
                    "java.io.InvalidClassException: Serialization class incompatible. Rebuilding cache.");
            return null;
        }
    }

    public static ArrayList<String> getTagDatabase() {
        return tagDatabase;
    }

    public static ArrayList<DatabaseItem> getFilteredItems() {
        return filteredItems;
    }

    public static ArrayList<DatabaseItem> getSelectedItems() {
        return selectedItems;
    }

    static File[] getValidFiles() {
        return validFiles;
    }

    public static DatabaseItem getSelectedItem() {
        return selectedItem;
    }

    static int getFileCount() {
        return fileCount;
    }

    public static ArrayList<String> getTagsBlacklist() {
        return tagsBlacklist;
    }

    public static ArrayList<String> getTagsWhitelist() {
        return tagsWhitelist;
    }
}
