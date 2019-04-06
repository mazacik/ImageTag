package database.list;

import database.object.TagObject;

import java.util.ArrayList;
import java.util.Comparator;

public class InfoObjectList extends ArrayList<TagObject> {
    public InfoObjectList() {
        super();
    }

    public boolean contains(TagObject object) {
        if (object == null) return false;
        if (super.contains(object)) return true;
        else {
            String group = object.getGroup();
            String name = object.getName();
            for (TagObject iterator : this) {
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
        super.sort(TagObject.getComparator());
    }

    public ArrayList<String> getGroups() {
        ArrayList<String> groups = new ArrayList<>();
        for (TagObject iterator : this) {
            if (!groups.contains(iterator.getGroup())) {
                groups.add(iterator.getGroup());
            }
        }
        groups.sort(Comparator.naturalOrder());
        return groups;
    }
    public ArrayList<String> getNames(String group) {
        ArrayList<String> names = new ArrayList<>();
        for (TagObject iterator : this) {
            String iteratorGroup = iterator.getGroup();
            String iteratorName = iterator.getName();
            if (iteratorGroup.equals(group) && !names.contains(iteratorName)) {
                names.add(iteratorName);
            }
        }
        return names;
    }
}
