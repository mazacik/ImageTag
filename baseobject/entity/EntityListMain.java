package baseobject.entity;

import com.google.gson.reflect.TypeToken;
import tools.FileUtil;
import tools.JsonUtil;

import java.lang.reflect.Type;

public class EntityListMain extends EntityList {
	private static final Type typeToken = new TypeToken<EntityListMain>() {}.getType();
	
	public EntityListMain() {
	
	}
	
	public void readFromDisk() {
		Object jsonResult = JsonUtil.read(typeToken, FileUtil.getProjectFileData());
		if (jsonResult != null) this.setAll((EntityListMain) jsonResult);
	}
	public void writeToDisk() {
		JsonUtil.write(entityListMain, typeToken, FileUtil.getProjectFileData());
	}
}
