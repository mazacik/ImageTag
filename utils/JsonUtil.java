package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import database.list.ObjectListMain;
import database.loader.Project;
import lifecycle.InstanceManager;
import settings.Settings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class JsonUtil {
    public static void write(Object object, Type type, String path) {
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        String JSON = GSONBuilder.create().toJson(object, type);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, false));
            writer.write(JSON);
            writer.close();
            InstanceManager.getLogger().debug("serializing " + path + " .. ok");
        } catch (IOException e) {
            InstanceManager.getLogger().debug("serializing " + path + " .. fail");
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
            InstanceManager.getLogger().debug("reading " + path + " .. ok");
            return fromJson;
        } catch (Exception e) {
            InstanceManager.getLogger().debug("reading " + path + " .. fail");
            return null;
        }
    }

    public enum TypeTokenEnum {
        MAINDATALIST(new TypeToken<ObjectListMain>() {}.getType()),
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
