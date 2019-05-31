package database.list;

import database.object.DataObject;
import loader.DirectoryUtil;
import system.Instances;
import system.JsonUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;

public class DataObjectListMain extends DataObjectList implements Instances {
    private static final String dataFile = DirectoryUtil.getDirNameData() + File.separator + "data.json";

    public void sort() {
        super.sort(Comparator.comparing(DataObject::getName));
    }
    public static DataObjectListMain readFromDisk() {
        Type typeToken = JsonUtil.TypeTokenEnum.MAINDATALIST.getValue();
        String path = DirectoryUtil.getPathSource() + dataFile;
        return (DataObjectListMain) JsonUtil.read(typeToken, path);
    }
    public void writeToDisk() {
        Type typeToken = JsonUtil.TypeTokenEnum.MAINDATALIST.getValue();
        String path = DirectoryUtil.getPathSource() + dataFile;
        JsonUtil.write(mainDataList, typeToken, path);
        mainInfoList.writeDummyToDisk();
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
