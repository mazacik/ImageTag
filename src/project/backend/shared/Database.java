package project.backend.shared;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import project.frontend.shared.Frontend;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Shared class for all database array lists and respective methods used across the entire project.
 */
public class Database {
    private static final ArrayList<DatabaseItem> itemDatabase = new ArrayList<>();
    private static final ArrayList<DatabaseItem> filteredItems = new ArrayList<>();
    private static final ArrayList<DatabaseItem> selectedItems = new ArrayList<>();
    private static final ArrayList<String> tagDatabase = new ArrayList<>();
    private static final ArrayList<String> tagsWhitelist = new ArrayList<>();
    private static final ArrayList<String> tagsBlacklist = new ArrayList<>();
    private static final String databaseCacheFileName = "/databasecache";
    private static ArrayList<File> validFiles;
    private static int fileCount;
    private static DatabaseItem selectedItem = null;

    /**
     * Initialization.
     */
    public static void initilize() {
        File[] validFilesArray = new File(Backend.DIRECTORY_PATH).listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".JPG") || name.endsWith(".PNG") || name.endsWith(".jpeg") || name.endsWith(".JPEG"));
        validFiles = (validFilesArray != null) ? new ArrayList<>(Arrays.asList(validFilesArray)) : new ArrayList<>();
        fileCount = validFiles.size();
    }

    /**
     * Adds the last selected item to the shared selection list.
     */
    public static void addToSelection(DatabaseItem databaseItem) {
        selectedItems.add(databaseItem);
        setSelectedItem(databaseItem);
        databaseItem.getImageView().setEffect(Frontend.getGalleryPane().getHighlightEffect());
        selectionChanged();
    }

    /**
     * Notifies the frontend components of an item being added to or removed from the shared selection list.
     */
    public static void selectionChanged() {
        Frontend.getNamePane().getListView().getSelectionModel().clearSelection();
        for (DatabaseItem item : selectedItems)
            Frontend.getNamePane().getListView().getSelectionModel().select(item.getColoredText());

        if (Frontend.getMainBorderPane().getCenter().equals(Frontend.getPreviewPane()))
            Backend.getPreviewPane().drawPreview();

        /*
        TilePane galleryTilePane = Frontend.getGalleryPane().getTilePane();
        int selectedItemIndex = galleryTilePane.getChildren().indexOf(selectedItem.getImageView());

        int galleryRowHeight = (int) (galleryTilePane.getTileHeight() + galleryTilePane.getVgap());
        int galleryRows = (int) (galleryTilePane.getHeight() / galleryRowHeight);
        if (galleryRowHeight % galleryTilePane.getTileHeight() != 0) galleryRows++;

        int galleryColumnWidth = (int) (galleryTilePane.getTileWidth() + galleryTilePane.getHgap());
        int galleryColumns = (int) (galleryTilePane.getWidth() / galleryColumnWidth);

        double scrollBarPosition = (double) (selectedItemIndex / galleryColumns) / (double) galleryRows;
        Frontend.getGalleryPane().setVvalue(scrollBarPosition);
        */

        Frontend.getRightPane().getListView().getItems().setAll(getSelectedItemsSharedTags());
    }

    /**
     * Returns the current selection's intersecting tags as an array list.
     */
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

    public static void setSelectedItem(DatabaseItem databaseItem) {
        selectedItem = databaseItem;
    }

    /**
     * Reloads the filtered database according to current tag whitelist and blacklist.
     */
    public static void filterByTags() {
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

    /**
     * Removes the last selected item from the shared selection list.
     */
    public static void removeIndexFromSelection(DatabaseItem databaseItem) {
        selectedItems.remove(databaseItem);
        databaseItem.getImageView().setEffect(null);
        selectionChanged();
    }

    /**
     * Removes all items from the shared selection list.
     */
    public static void clearSelection() {
        selectedItems.clear();
        Frontend.getNamePane().getListView().getSelectionModel().clearSelection();
        Frontend.getRightPane().getListView().getItems().clear();
        for (DatabaseItem databaseItem : itemDatabase) {
            if (databaseItem.getImageView().getEffect() != null)
                databaseItem.getImageView().setEffect(null);
        }
    }

    /**
     * Writes the current state of the main database to disk.
     */
    public static void writeToDisk() {
        writeToDisk(Database.getItemDatabase());
    }

    public static ArrayList<DatabaseItem> getItemDatabase() {
        return itemDatabase;
    }

    /**
     * Writes the current state of a database to disk.
     *
     * @param itemDatabase the database whose current state is to be written to disk
     */
    public static void writeToDisk(ArrayList<DatabaseItem> itemDatabase) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(Backend.DIRECTORY_PATH + databaseCacheFileName));
            objectOutputStream.writeObject(itemDatabase);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the state of the main database from disk.
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<DatabaseItem> readFromDisk() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(Backend.DIRECTORY_PATH + databaseCacheFileName));
            ArrayList<DatabaseItem> itemDatabase = (ArrayList<DatabaseItem>) objectInputStream.readObject();
            objectInputStream.close();
            return itemDatabase;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("java.io.InvalidClassException: Serialization class incompatible. Rebuilding cache.");
            Platform.runLater(Database::showDatabaseLoadingFailureAlert);
            return null;
        }
    }

    private static void showDatabaseLoadingFailureAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Database Loading Failed");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText("java.io.InvalidClassException: Serialization class incompatible. Rebuilding cache.");
        alert.showAndWait();
    }

    public static ArrayList<DatabaseItem> getFilteredItems() {
        return filteredItems;
    }

    public static DatabaseItem getSelectedItem() {
        return selectedItem;
    }

    public static int getFileCount() {
        return fileCount;
    }

    public static ArrayList<String> getTagDatabase() {
        return tagDatabase;
    }

    public static ArrayList<DatabaseItem> getSelectedItems() {
        return selectedItems;
    }

    public static ArrayList<File> getValidFiles() {
        return validFiles;
    }

    public static ArrayList<String> getTagsBlacklist() {
        return tagsBlacklist;
    }

    public static ArrayList<String> getTagsWhitelist() {
        return tagsWhitelist;
    }
}
