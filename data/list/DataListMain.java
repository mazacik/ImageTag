package application.data.list;

import application.main.Instances;
import application.misc.FileUtil;
import application.misc.JsonUtil;

import java.lang.reflect.Type;

public class DataListMain extends DataList {
	public DataListMain() {
	
	}
	
	public static DataListMain readFromDisk() {
		Type typeToken = JsonUtil.TypeTokenEnum.MAINDATALIST.getValue();
		String path = FileUtil.getFileData();
		return (DataListMain) JsonUtil.read(typeToken, path);
	}
	public void writeToDisk() {
		Type typeToken = JsonUtil.TypeTokenEnum.MAINDATALIST.getValue();
		String path = FileUtil.getFileData();
		JsonUtil.write(Instances.getDataListMain(), typeToken, path);
	}
}
