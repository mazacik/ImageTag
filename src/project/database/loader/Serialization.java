package project.database.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import project.database.control.DataElementControl;
import project.database.element.DataElement;
import project.helper.Settings;

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

        Type databaseItemListType = new TypeToken<Collection<DataElement>>() {}.getType();
        String JSON = GSON.toJson(DataElementControl.getDataElementsLive(), databaseItemListType);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(databaseCacheFilePath, false));
            writer.write(JSON);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<DataElement> readFromDisk() {
        Path databaseCacheFilePath = Paths.get(Settings.getDatabaseCacheFilePath());
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();

        Type databaseItemListType = new TypeToken<Collection<DataElement>>() {}.getType();
        try {
            String JSON = new String(Files.readAllBytes(databaseCacheFilePath));
            return GSON.fromJson(JSON, databaseItemListType);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
