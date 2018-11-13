package database.object;

public class DataCollection extends Collection<DataObject> {
    public DataCollection() {
        super();
    }
    public DataCollection(DataCollection collection) {
        super(collection);
    }

    public boolean contains(Object object) {
        if (object == null) return false;
        return super.contains(object);
    }
    public void sort() {
        super.sort(DataObject.getComparator());
    }
}
