package project.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import project.common.Settings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

public abstract class Serialization {
    /* imports */
    private static final String databaseCacheFilePath = Settings.getDatabaseCacheFilePath();
    /* public methods */
    public static void writeToDisk() {
        writeToDisk(Database.getDatabaseItems());
    }

    public static void writeToDisk(ArrayList<DatabaseItem> itemDatabase) {
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();

        Type databaseItemListType = new TypeToken<Collection<DatabaseItem>>() {}.getType();
        String JSON = GSON.toJson(itemDatabase, databaseItemListType);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(databaseCacheFilePath, false));
            writer.write(JSON);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<DatabaseItem> readFromDisk() {
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();

        Type databaseItemListType = new TypeToken<Collection<DatabaseItem>>() {}.getType();
        String JSON = "";
        try {
            JSON = new String(Files.readAllBytes(Paths.get(databaseCacheFilePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return GSON.fromJson(JSON, databaseItemListType);
    }
}
