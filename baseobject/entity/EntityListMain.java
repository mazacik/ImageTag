package baseobject.entity;

import com.google.gson.reflect.TypeToken;
import tools.FileUtil;
import tools.JsonUtil;

import java.lang.reflect.Type;

public class EntityListMain extends EntityList {
	private static final Type typeToken = new TypeToken<EntityListMain>() {}.getType();
	
	public EntityListMain() {
	
	}
	
	public static EntityListMain readFromDisk() {
		return (EntityListMain) JsonUtil.read(typeToken, FileUtil.getProjectFileData());
	}
	public void writeToDisk() {
		JsonUtil.write(entityListMain, typeToken, FileUtil.getProjectFileData());
	}
}
