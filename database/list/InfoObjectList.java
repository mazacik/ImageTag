package database.list;

import database.object.InfoObject;

import java.util.ArrayList;
import java.util.Comparator;

public class InfoObjectList extends ArrayList<InfoObject> {
    public InfoObjectList() {
        super();
    }

    public boolean contains(InfoObject object) {
        if (object == null) return false;
        if (super.contains(object)) return true;
        else {
            String group = object.getGroup();
            String name = object.getName();
            for (InfoObject iterator : this) {
                String iteratorGroup = iterator.getGroup();
                String iteratorName = iterator.getName();
                if (group.equals(iteratorGroup) && name.equals(iteratorName)) {
                    return true;
                }
            }
            return false;
        }
    }
    public void sort() {
        super.sort(InfoObject.getComparator());
    }

    public ArrayList<String> getGroups() {
        ArrayList<String> groups = new ArrayList<>();
        for (InfoObject iterator : this) {
            if (!groups.contains(iterator.getGroup())) {
                groups.add(iterator.getGroup());
            }
        }
        groups.sort(Comparator.naturalOrder());
        return groups;
    }
    public ArrayList<String> getNames(String group) {
        ArrayList<String> names = new ArrayList<>();
        for (InfoObject iterator : this) {
            String iteratorGroup = iterator.getGroup();
            String iteratorName = iterator.getName();
            if (iteratorGroup.equals(group) && !names.contains(iteratorName)) {
                names.add(iteratorName);
            }
        }
        return names;
    }
}
