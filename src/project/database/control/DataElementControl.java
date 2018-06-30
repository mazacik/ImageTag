package project.database.control;

import project.control.FilterControl;
import project.control.SelectionControl;
import project.database.element.DataElement;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class DataElementControl {
    /* vars */
    private static final ArrayList<DataElement> dataElements = new ArrayList<>();

    /* public */
    public static boolean add(DataElement dataElement) {
        return DataElementControl.getDataElements().add(dataElement);
    }
    public static boolean addAll(ArrayList<DataElement> dataElements) {
        return DataElementControl.getDataElements().addAll(dataElements);
    }
    public static boolean remove(DataElement dataElement) {
        return DataElementControl.getDataElements().remove(dataElement);
    }

    public static void sortAll() {
        Comparator dataElementComparator = Comparator.comparing(DataElement::getName);
        DataElementControl.getDataElements().sort(dataElementComparator);
        FilterControl.getValidDataElements().sort(dataElementComparator);
        SelectionControl.getDataElements().sort(dataElementComparator);
    }

    /* get */
    public static ArrayList<DataElement> getDataElementsLive() {
        return dataElements;
    }
    public static ArrayList<DataElement> getDataElementsCopy() {
        return new ArrayList<>(dataElements);
    }
    private static ArrayList<DataElement> getDataElements() {
        return dataElements;
    }
}
