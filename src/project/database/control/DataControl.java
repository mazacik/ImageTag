package project.database.control;

import project.database.element.DataCollection;
import project.database.element.DataObject;

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
    public static DataCollection getDataCollectionCopy() {
        return new DataCollection(collection);
    }

    public static DataCollection getCollection() {
        return collection;
    }
}
