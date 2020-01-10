package misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public abstract class JsonUtil {
	public static void write(Object object, Type type, String path) {
		GsonBuilder GSONBuilder = new GsonBuilder();
		String JSON = GSONBuilder.serializeNulls().setPrettyPrinting().create().toJson(object, type);
		try {
			new File(path).getParentFile().mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(path, false));
			writer.write(JSON);
			writer.close();
			Logger.getGlobal().config("GSON W \"" + path + "\" OK");
		} catch (IOException e) {
			Logger.getGlobal().severe("GSON W \"" + path + "\" ERROR");
			e.printStackTrace();
		}
	}
	public static Object read(Type type, String path) {
		GsonBuilder GSONBuilder = new GsonBuilder();
		Gson GSON = GSONBuilder.serializeNulls().setPrettyPrinting().create();
		try {
			String JSON = new String(Files.readAllBytes(Paths.get(path)));
			Object fromJson = GSON.fromJson(JSON, type);
			Logger.getGlobal().config("GSON R \"" + path + "\" OK");
			return fromJson;
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getGlobal().severe("GSON R \"" + path + "\" ERROR");
			return null;
		}
	}
}
