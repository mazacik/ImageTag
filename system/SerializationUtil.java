package system;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import database.list.DataObjectListMain;
import settings.Settings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class SerializationUtil implements InstanceRepo {
    public static void writeJSON(Object object, Type type, String path) {
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        String JSON = GSONBuilder.create().toJson(object, type);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, false));
            writer.write(JSON);
            writer.close();
            logger.debug(SerializationUtil.class, "serializing " + path + " ... ok");
        } catch (IOException e) {
            logger.debug(SerializationUtil.class, "serializing " + path + " ... fail");
            e.printStackTrace();
        }
    }
    public static Object readJSON(Type type, String path) {
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();
        try {
            String JSON = new String(Files.readAllBytes(Paths.get(path)));
            Object fromJson = GSON.fromJson(JSON, type);
            logger.debug(SerializationUtil.class, "reading " + path + " ... ok");
            return fromJson;
        } catch (Exception e) {
            logger.debug(SerializationUtil.class, "reading " + path + " ... fail");
            return null;
        }
    }

    public enum TypeTokenEnum {
        MAINDATALIST(new TypeToken<DataObjectListMain>() {}.getType()),
        SETTINGS(new TypeToken<Settings>() {}.getType()),
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
