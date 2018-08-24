package project.database.element;

public class DataCollection extends Collection<DataObject> {
    public DataCollection() {
        super();
    }
    public DataCollection(DataCollection collection) {
        super(collection);
    }

    public boolean contains(Object object) {
        if (object.equals(null)) return false;
        return super.contains(object);
    }
    public void sort() {
        super.sort(DataObject.getComparator());
    }

    public static void removeTagObject(TagObject object, DataCollection collection) {
        for (Object iterator : collection) {
            ((DataObject) iterator).getTagCollection().remove(object);
        }
    }
    public static void removeTagObject(TagCollection tagCollection, DataCollection dataCollection) {
        for (Object iterator : dataCollection) {
            ((DataObject) iterator).getTagCollection().removeAll(tagCollection);
        }
    }
}
