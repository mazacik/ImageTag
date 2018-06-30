package project.database.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import project.common.Settings;
import project.database.DataElementDatabase;
import project.database.element.DataElement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public abstract class Serialization {

    public static void writeToDisk() {
        String databaseCacheFilePath = Settings.getDatabaseCacheFilePath();
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();

        Type databaseItemListType = new TypeToken<Collection<DataElement>>() {}.getType();
        String JSON = GSON.toJson(DataElementDatabase.getDataElements(), databaseItemListType);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(databaseCacheFilePath, false));
            writer.write(JSON);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createBackup() {
        String databaseCacheFilePath = Settings.getDatabaseCacheFilePath();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm-ss dd-MM-yyyy");
        String sb = databaseCacheFilePath.replace(".json", " @ " + dateFormat.format(new Date()) + ".json");
        File file = new File(databaseCacheFilePath);
        try {
            Files.copy(file.toPath(), new File(sb).toPath());
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
            Serialization.createBackup();
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
