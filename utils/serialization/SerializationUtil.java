package utils.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import utils.MainUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class SerializationUtil implements MainUtil {
    public static void writeJSON(Object object, Type type, String path) {
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        String JSON = GSONBuilder.create().toJson(object, type);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, false));
            writer.write(JSON);
            writer.close();
            logger.debug(SerializationUtil.class, "serializing " + type.getTypeName() + " ... ok");
        } catch (IOException e) {
            logger.debug(SerializationUtil.class, "serializing " + type.getTypeName() + " ... fail");
            e.printStackTrace();
        }
    }
    public static Object readJSON(Type type, String path) {
        GsonBuilder GSONBuilder = new GsonBuilder();
        GSONBuilder.setPrettyPrinting().serializeNulls();
        Gson GSON = GSONBuilder.create();
        try {
            String JSON = new String(Files.readAllBytes(Paths.get(path)));
            logger.debug(SerializationUtil.class, "reading " + type.getTypeName() + " ... ok");
            return GSON.fromJson(JSON, type);
        } catch (Exception e) {
            logger.debug(SerializationUtil.class, "reading " + type.getTypeName() + " ... fail");
            return null;
        }
    }
}
