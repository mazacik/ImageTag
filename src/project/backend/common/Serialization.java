package project.backend.common;

import javafx.application.Platform;
import project.backend.Backend;
import project.backend.database.Database;
import project.backend.database.DatabaseItem;
import project.frontend.common.AlertWindow;

import java.io.*;
import java.util.ArrayList;

public abstract class Serialization {
    private static final String databaseCacheFileName = "databasecache";


    public static void writeToDisk() {
        writeToDisk(Database.getDatabaseItems());
    }

    public static void writeToDisk(ArrayList<DatabaseItem> itemDatabase) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(Backend.DIRECTORY_PATH + "/" + databaseCacheFileName));
            objectOutputStream.writeObject(itemDatabase);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<DatabaseItem> readFromDisk() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(Backend.DIRECTORY_PATH + "/" + databaseCacheFileName));
            ArrayList<DatabaseItem> itemDatabase = (ArrayList<DatabaseItem>) objectInputStream.readObject();
            objectInputStream.close();
            return itemDatabase != null ? itemDatabase : new ArrayList<>();
        } catch (IOException e) {
            Platform.runLater(() -> AlertWindow.showErrorAlertSimple("Database Loading Failed", "java.io.IOException: Database cache not found. Rebuilding cache."));
            return new ArrayList<>();
        } catch (ClassNotFoundException e) {
            Platform.runLater(() -> AlertWindow.showErrorAlertSimple("Database Loading Failed", "java.io.InvalidClassException: Serialization class incompatible. Rebuilding cache."));
            return new ArrayList<>();
        }
    }
}
