package project.database.control;

import project.database.object.DataCollection;
import project.database.object.DataObject;

public abstract class DataControl {
    /* vars */
    private static final DataCollection collection = new DataCollection();

    /* public */
    public static boolean add(DataObject dataObject) {
        return collection.add(dataObject);
    }
    public static boolean addAll(DataCollection dataObjects) {
        return collection.addAll(dataObjects);
    }
    public static boolean remove(DataObject dataObject) {
        return collection.remove(dataObject);
    }

    /* get */
    public static DataCollection getCollection() {
        return collection;
    }
}
