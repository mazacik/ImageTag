package project.database.control;

import project.control.FilterControl;
import project.control.SelectionControl;
import project.database.element.DataObject;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class DataObjectControl {
    /* vars */
    private static final ArrayList<DataObject> dataObjects = new ArrayList<>();

    /* public */
    public static boolean add(DataObject dataObject) {
        return DataObjectControl.getDataObjects().add(dataObject);
    }
    public static boolean addAll(ArrayList<DataObject> dataObjects) {
        return DataObjectControl.getDataObjects().addAll(dataObjects);
    }
    public static boolean remove(DataObject dataObject) {
        return DataObjectControl.getDataObjects().remove(dataObject);
    }

    public static void sortAll() {
        Comparator dataElementComparator = Comparator.comparing(DataObject::getName);
        DataObjectControl.getDataObjects().sort(dataElementComparator);
        FilterControl.getValidObjects().sort(dataElementComparator);
        SelectionControl.getDataObjects().sort(dataElementComparator);
    }

    /* get */
    public static ArrayList<DataObject> getDataElementsLive() {
        return dataObjects;
    }
    public static ArrayList<DataObject> getDataElementsCopy() {
        return new ArrayList<>(dataObjects);
    }
    private static ArrayList<DataObject> getDataObjects() {
        return dataObjects;
    }
}
