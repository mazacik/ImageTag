package database.list;

import database.object.DataObject;
import system.InstanceRepo;

import java.util.ArrayList;

public class DataObjectList extends ArrayList<DataObject> implements InstanceRepo {
    public boolean setAll(DataObjectList dataObjects) {
        this.clear();
        return this.addAll(dataObjects);
    }
}
