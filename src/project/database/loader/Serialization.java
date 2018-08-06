package project.database.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import project.database.control.DataObjectControl;
import project.database.element.DataObject;
import project.settings.Settings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

public abstract class Serialization {

    public static void writeToDisk() {
        String databaseCacheFilePath = Settings.getDatabaseCacheFilePath();
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();

        Type databaseItemListType = new TypeToken<Collection<DataObject>>() {}.getType();
        String JSON = GSON.toJson(DataObjectControl.getDataElementsLive(), databaseItemListType);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(databaseCacheFilePath, false));
            writer.write(JSON);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<DataObject> readFromDisk() {
        Path databaseCacheFilePath = Paths.get(Settings.getDatabaseCacheFilePath());
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();

        Type databaseItemListType = new TypeToken<Collection<DataObject>>() {}.getType();
        try {
            String JSON = new String(Files.readAllBytes(databaseCacheFilePath));
            return GSON.fromJson(JSON, databaseItemListType);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
