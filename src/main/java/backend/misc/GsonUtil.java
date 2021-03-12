package backend.misc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public abstract class GsonUtil {
	private static ObjectMapper getMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.AUTO_DETECT_CREATORS,
		               MapperFeature.AUTO_DETECT_FIELDS,
		               MapperFeature.AUTO_DETECT_GETTERS,
		               MapperFeature.AUTO_DETECT_IS_GETTERS
		);
		// if you want to prevent an exception when classes have no annotated properties
		// om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		return mapper;
	}
	
	public static boolean write(Object object, String path) {
		try {
			File file = new File(path);
			file.getParentFile().mkdirs();
			if (file.exists()) file.delete();
			file.createNewFile();
			
			getMapper().writeValue(file, object);
			
			Logger.getGlobal().config("GSON WRITE \"" + path + "\" OK");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static <T> List<T> readList(TypeReference<List<T>> t, String path) {
		try {
			List<T> fromJson = getMapper().readValue(new File(path), t);
			Logger.getGlobal().config("GSON READ \"" + path + "\" OK");
			return fromJson;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public static <T> T read(TypeReference<T> t, String path) {
		try {
			T fromJson = getMapper().readValue(new File(path), t);
			Logger.getGlobal().config("GSON READ \"" + path + "\" OK");
			return fromJson;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static <T> T read(Class<T> c, String path) {
		try {
			T fromJson = getMapper().readValue(new File(path), c);
			Logger.getGlobal().config("GSON READ \"" + path + "\" OK");
			return fromJson;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
