package application.misc;

import application.database.list.DataObjectListMain;
import application.database.loader.Project;
import application.settings.Settings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public abstract class JsonUtil {
    public static void write(Object object, Type type, String path) {
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        String JSON = GSONBuilder.create().toJson(object, type);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, false));
            writer.write(JSON);
            writer.close();
			Logger.getGlobal().info("\"" + path + "\" .. ok");
        } catch (IOException e) {
			Logger.getGlobal().info("\"" + path + "\" .. fail");
            e.printStackTrace();
        }
    }
    public static Object read(Type type, String path) {
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();
        try {
            String JSON = new String(Files.readAllBytes(Paths.get(path)));
            Object fromJson = GSON.fromJson(JSON, type);
			Logger.getGlobal().info("\"" + path + "\" .. ok");
            return fromJson;
        } catch (Exception e) {
			Logger.getGlobal().info("\"" + path + "\" .. fail");
            return null;
        }
    }

    public enum TypeTokenEnum {
		MAINDATALIST(new TypeToken<DataObjectListMain>() {}.getType()),
        SETTINGS(new TypeToken<Settings>() {}.getType()),
        PROJECT(new TypeToken<Project>() {}.getType()),
        ;

        private Type value;

        TypeTokenEnum(Type value) {
            this.value = value;
        }

        public Type getValue() {
            return value;
        }
    }
}
