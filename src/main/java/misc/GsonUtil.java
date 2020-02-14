package misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.logging.Logger;

public abstract class GsonUtil {
	public static void write(Object object, Type type, String path) {
		GsonBuilder GSONBuilder = new GsonBuilder();
		String JSON = GSONBuilder.serializeNulls().setPrettyPrinting().create().toJson(object, type);
		try {
			new File(path).getParentFile().mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(path, false));
			writer.write(JSON);
			writer.close();
			Logger.getGlobal().config("GSON WRITE \"" + path + "\" OK");
		} catch (IOException e) {
			Logger.getGlobal().severe("GSON WRITE \"" + path + "\" ERROR NO ACCESS");
		}
	}
	public static <T> T read(Type type, String path) {
		GsonBuilder GSONBuilder = new GsonBuilder();
		Gson GSON = GSONBuilder.serializeNulls().setPrettyPrinting().create();
		try {
			String JSON = new String(Files.readAllBytes(Paths.get(path)));
			T fromJson = GSON.fromJson(JSON, type);
			Logger.getGlobal().config("GSON READ \"" + path + "\" OK");
			return fromJson;
		} catch (NoSuchFileException e) {
			Logger.getGlobal().severe("GSON READ \"" + path + "\" ERROR NOT FOUND");
		} catch (IOException e) {
			Logger.getGlobal().severe("GSON READ \"" + path + "\" ERROR NO ACCESS");
		} catch (Exception e) {
			Logger.getGlobal().severe("GSON READ \"" + path + "\" ERROR");
		}
		return null;
	}
}
