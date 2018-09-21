package project.control;

import project.database.object.DataCollection;
import project.database.object.DataObject;

public class DataControl extends ControlCollection {
    private final DataCollection collection;

    DataControl() {
        collection = new DataCollection();
    }

    public boolean add(DataObject dataObject) {
        return collection.add(dataObject);
    }
    public boolean addAll(DataCollection dataObjects) {
        return collection.addAll(dataObjects);
    }
    public boolean remove(DataObject dataObject) {
        return collection.remove(dataObject);
    }

    public DataCollection getCollection() {
        return collection;
    }
}
