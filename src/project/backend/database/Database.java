package project.backend.database;

import project.Main;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Database {
    private static final ArrayList<DatabaseItem> databaseItems = new ArrayList<>();
    private static final ArrayList<DatabaseItem> databaseItemsFiltered = new ArrayList<>();
    private static final ArrayList<DatabaseItem> databaseItemsSelected = new ArrayList<>();
    private static final ArrayList<String> databaseTags = new ArrayList<>();
    private static final ArrayList<String> databaseTagsWhitelist = new ArrayList<>();
    private static final ArrayList<String> databaseTagsBlacklist = new ArrayList<>();

    private static ArrayList<File> validFiles;
    private static int fileCount;

    public static void initialize() {
        FilenameFilter filenameFilter = (dir, name) -> name.endsWith(".jpg") || name.endsWith(".JPG") || name.endsWith(".png") || name.endsWith(".PNG") || name.endsWith(".jpeg") || name.endsWith(".JPEG");
        File[] validFilesArray = new File(Main.DIRECTORY_PATH).listFiles(filenameFilter);
        validFiles = (validFilesArray != null) ? new ArrayList<>(Arrays.asList(validFilesArray)) : new ArrayList<>();
        fileCount = validFiles.size();

        new DatabaseLoader().start();
    }

    public static void sort() {
        Database.getDatabaseItems().sort(Comparator.comparing(DatabaseItem::getSimpleName));
        Database.getDatabaseItemsFiltered().sort(Comparator.comparing(DatabaseItem::getSimpleName));
        Database.getDatabaseItemsSelected().sort(Comparator.comparing(DatabaseItem::getSimpleName));
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

    public static ArrayList<File> getValidFiles() {
        return validFiles;
    }

    public static int getFileCount() {
        return fileCount;
    }
}
