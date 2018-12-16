package database.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import database.list.DataListMain;
import database.object.DataObject;
import settings.Settings;
import utils.MainUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public abstract class Serialization implements MainUtil {
    public static void writeToDisk() {
        String path_data = Settings.getPath_data() + "\\data.json";
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();

        Type databaseItemListType = new TypeToken<Collection<DataObject>>() {}.getType();
        String JSON = GSON.toJson(dataListMain, databaseItemListType);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path_data, false));
            writer.write(JSON);
            writer.close();
            logger.out("writing database to disk ... ok", Serialization.class);
        } catch (IOException e) {
            logger.out("writing database to disk ... fail", Serialization.class);
            e.printStackTrace();
        }
    }
    public static DataListMain readFromDisk() {
        Path path_data = Paths.get(Settings.getPath_data() + "\\data.json");
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();

        Type databaseItemListType = new TypeToken<DataListMain>() {}.getType();
        try {
            String JSON = new String(Files.readAllBytes(path_data));
            logger.out("reading database from disk ... ok", Serialization.class);
            return GSON.fromJson(JSON, databaseItemListType);
        } catch (Exception e) {
            logger.out("reading database from disk ... fail", Serialization.class);
            return new DataListMain();
        }
    }
}
