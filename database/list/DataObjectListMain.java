package database.list;

import database.object.DataObject;
import system.InstanceRepo;
import system.SerializationUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;

public class DataObjectListMain extends DataObjectList implements InstanceRepo {
    private static final String dataFile = "data\\data.json";

    public void sort() {
        super.sort(Comparator.comparing(DataObject::getName));
    }

    public void writeToDisk() {
        Type typeToken = SerializationUtil.TypeTokenEnum.MAINDATALIST.getValue();
        String path = settings.getCurrentDirectory() + dataFile;
        SerializationUtil.writeJSON(mainDataList, typeToken, path);
        mainInfoList.writeDummyToDisk();
    }
    public DataObjectListMain readFromDisk() {
        Type typeToken = SerializationUtil.TypeTokenEnum.MAINDATALIST.getValue();
        String path = settings.getCurrentDirectory() + dataFile;
        return (DataObjectListMain) SerializationUtil.readJSON(typeToken, path);
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
