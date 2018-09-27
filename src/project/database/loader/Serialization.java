package project.database.loader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import project.database.object.DataCollection;
import project.database.object.DataObject;
import project.settings.Settings;
import project.utils.MainUtil;

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
        log.out("writing database to disk", Serialization.class, true);
        String path_data = Settings.getPath_data() + "\\data.json";
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();

        Type databaseItemListType = new TypeToken<Collection<DataObject>>() {}.getType();
        String JSON = GSON.toJson(mainData, databaseItemListType);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path_data, false));
            writer.write(JSON);
            writer.close();
            log.out("... success");
        } catch (IOException e) {
            log.out("... failed");
            e.printStackTrace();
        }
    }
    public static DataCollection readFromDisk() {
        log.out("reading database from disk", Serialization.class, true);
        Path path_data = Paths.get(Settings.getPath_data() + "\\data.json");
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();

        Type databaseItemListType = new TypeToken<DataCollection>() {}.getType();
        try {
            String JSON = new String(Files.readAllBytes(path_data));
            log.out("... success");
            return GSON.fromJson(JSON, databaseItemListType);
        } catch (Exception e) {
            log.out("... failed");
            return new DataCollection();
        }
    }
}
