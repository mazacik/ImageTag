package database.list;

import database.object.DataObject;

import java.util.Comparator;

public class DataListMain extends BaseList<DataObject> {
    public void sort() {
        super.sort(Comparator.comparing(DataObject::getName));
        //todo other sorts?
    }
}
