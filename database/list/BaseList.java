package database.list;

import java.util.ArrayList;

public class BaseList<T> extends ArrayList<T> {
    public BaseList() {
        super();
    }
    public BaseList(BaseList<T> baseList) {
        super(baseList);
    }

    public boolean add(T t) {
        if (t == null || this.contains(t)) return false;
        if (super.add(t)) {
            this.sort();
            return true;
        }
        return false;
    }
    public boolean addAll(BaseList<T> baseList) {
        if (baseList == null) return false;
        baseList.removeAll(this.getIntersection(baseList));
        if (super.addAll(baseList)) {
            this.sort();
            return true;
        }
        return false;
    }
    public boolean setAll(BaseList<T> baseList) {
        super.clear();
        return this.addAll(baseList);
    }
    public boolean remove(Object o) {
        if (o == null) return false;
        return super.remove(o);
    }
    public boolean removeAll(BaseList<T> baseList) {
        if (baseList == null) return false;
        return super.removeAll(baseList);
    }
    public void sort() {}

    public BaseList<T> getIntersection(BaseList<T> baseList) {
        BaseList<T> returnValue = new BaseList<>(baseList);
        for (Object iterator : baseList) {
            if (!this.contains(iterator)) {
                //noinspection SuspiciousMethodCalls
                returnValue.remove(iterator);
            }
        }
        return returnValue;
    }
}
