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
    public static void sort() {
        Comparator dataElementComparator = Comparator.comparing(DataElement::getName);
        DataElementControl.getDataElements().sort(dataElementComparator);
        FilterControl.getValidDataElements().sort(dataElementComparator);
        SelectionControl.getDataElements().sort(dataElementComparator);
    }

    /* get */
    public static ArrayList<DataElement> getDataElements() {
        return dataElements;
    }
}
