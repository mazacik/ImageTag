package project.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import project.common.Database;
import project.common.Settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public abstract class Serialization {
    /* imports */
    private static String databaseCacheFilePath = Settings.getDatabaseCacheFilePath();

    /* public methods */
    public static void writeToDisk() {
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();

        Type databaseItemListType = new TypeToken<Collection<DatabaseItem>>() {}.getType();
        String JSON = GSON.toJson(Database.getDatabaseItems(), databaseItemListType);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(databaseCacheFilePath, false));
            writer.write(JSON);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createBackup() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm-ss dd-MM-yyyy");
        String sb = databaseCacheFilePath.replace(".json", " @ " + dateFormat.format(new Date()) + ".json");
        File file = new File(databaseCacheFilePath);
        try {
            Files.copy(file.toPath(), new File(sb).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<DatabaseItem> readFromDisk() {
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();

        Type databaseItemListType = new TypeToken<Collection<DatabaseItem>>() {}.getType();
        String JSON = "[]";
        try {
            JSON = new String(Files.readAllBytes(Paths.get(databaseCacheFilePath)));
        } catch (IOException e) {
            Serialization.createBackup();
            e.printStackTrace();
        }
        return GSON.fromJson(JSON, databaseItemListType);
    }
}
