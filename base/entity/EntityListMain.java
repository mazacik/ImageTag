package application.base.entity;

import application.util.FileUtil;
import application.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

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
