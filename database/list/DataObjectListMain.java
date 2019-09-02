package application.database.list;

import application.database.object.DataObject;
import application.main.Instances;
import application.misc.FileUtil;
import application.misc.JsonUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataObjectListMain extends DataObjectList {
	public DataObjectListMain() {
	
	}
	
	public static DataObjectListMain readFromDisk() {
		Type typeToken = JsonUtil.TypeTokenEnum.MAINDATALIST.getValue();
		String path = FileUtil.getFileData();
		return (DataObjectListMain) JsonUtil.read(typeToken, path);
	}
	
	public void writeToDisk() {
		Type typeToken = JsonUtil.TypeTokenEnum.MAINDATALIST.getValue();
		String path = FileUtil.getFileData();
		JsonUtil.write(Instances.getObjectListMain(), typeToken, path);
	}
	
	public ArrayList<Integer> getAllGroups() {
		ArrayList<Integer> groups = new ArrayList<>();
		for (DataObject dataObject : this) {
			if (dataObject.getJointID() != 0 && !groups.contains(dataObject.getJointID())) {
				groups.add(dataObject.getJointID());
			}
		}
		return groups;
	}
}
