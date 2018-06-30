package project.database;

import project.control.FilterControl;
import project.control.SelectionControl;
import project.database.element.DataElement;

import java.util.ArrayList;
import java.util.Comparator;
//todo check
public abstract class DataElementDatabase {
    /* vars */
    private static final ArrayList<DataElement> dataElements = new ArrayList<>();

    /* public */
    public static void sortLists() { //todo move
        Comparator dataElementComparator = Comparator.comparing(DataElement::getName);
        dataElements.sort(dataElementComparator);
        FilterControl.getValidDataElements().sort(dataElementComparator);
        SelectionControl.getDataElements().sort(dataElementComparator);
    }

    /* get */ //todo return a copy of the array to protect it?
    public static ArrayList<DataElement> getDataElements() {
        return dataElements;
    }
}
