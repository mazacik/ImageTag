package project.backend.common;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import project.backend.database.Database;
import project.backend.database.DatabaseItem;
import project.Main;

import java.io.*;
import java.util.ArrayList;

public abstract class Serialization {
    private static final String databaseCacheFileName = "/databasecache";

    public static void writeToDisk() {
        writeToDisk(Database.getDatabaseItems());
    }

    public static void writeToDisk(ArrayList<DatabaseItem> itemDatabase) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(Main.DIRECTORY_PATH + databaseCacheFileName));
            objectOutputStream.writeObject(itemDatabase);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<DatabaseItem> readFromDisk() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(Main.DIRECTORY_PATH + databaseCacheFileName));
            ArrayList<DatabaseItem> itemDatabase = (ArrayList<DatabaseItem>) objectInputStream.readObject();
            objectInputStream.close();
            return itemDatabase;
        } catch (IOException | ClassNotFoundException e) {
            Platform.runLater(Serialization::showReadFromDiskFailureAlert);
            return null;
        }
    }

    private static void showReadFromDiskFailureAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Database Loading Failed");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.setContentText("java.io.InvalidClassException: Serialization class incompatible. Rebuilding cache.");
        alert.showAndWait();
    }
}
