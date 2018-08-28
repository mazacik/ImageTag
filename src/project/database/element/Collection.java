package project.database.element;

import java.util.ArrayList;

public class Collection<T> extends ArrayList<T> {
    public Collection() {
        super();
    }
    public Collection(Collection<T> collection) {
        super(collection);
    }

    public boolean add(T t) {
        if (t == null || this.contains(t)) return false;
        if (super.add(t)) {
            this.sort();
            return true;
        }
        return false;
    }
    public boolean addAll(Collection<T> collection) {
        if (collection == null) return false;
        collection.removeAll(this.getIntersection(collection));
        if (super.addAll(collection)) {
            this.sort();
            return true;
        }
        return false;
    }
    public boolean setAll(Collection<T> collection) {
        super.clear();
        return this.addAll(collection);
    }
    public boolean remove(Object o) {
        if (o == null) return false;
        return super.remove(o);
    }
    public boolean removeAll(Collection<T> collection) {
        if (collection == null) return false;
        return super.removeAll(collection);
    }
    public void sort() {}

    public Collection<T> getIntersection(Collection<T> collection) {
        Collection<T> returnValue = new Collection<>(collection);
        for (Object iterator : collection) {
            if (!this.contains(iterator)) {
                returnValue.remove(iterator);
            }
        }
        return returnValue;
    }
}
