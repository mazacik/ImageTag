package database.list;

import utils.FileUtil;
import database.object.DataObject;
import lifecycle.InstanceManager;
import utils.JsonUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;

public class ObjectListMain extends ObjectList {
    public ObjectListMain() {

    }

    public static ObjectListMain readFromDisk() {
        Type typeToken = JsonUtil.TypeTokenEnum.MAINDATALIST.getValue();
        String path = FileUtil.getFileData();
        return (ObjectListMain) JsonUtil.read(typeToken, path);
    }

    public void sort() {
        super.sort(Comparator.comparing(DataObject::getName));
    }
    public void writeToDisk() {
        Type typeToken = JsonUtil.TypeTokenEnum.MAINDATALIST.getValue();
        String path = FileUtil.getFileData();
        JsonUtil.write(InstanceManager.getObjectListMain(), typeToken, path);
    }

    public ArrayList<Integer> getAllGroups() {
        ArrayList<Integer> groups = new ArrayList<>();
        for (DataObject dataObject : this) {
            if (dataObject.getMergeID() != 0 && !groups.contains(dataObject.getMergeID())) {
                groups.add(dataObject.getMergeID());
            }
        }
        return groups;
    }
}
