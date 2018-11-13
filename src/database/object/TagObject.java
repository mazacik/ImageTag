package database.object;

import org.apache.commons.text.WordUtils;

import java.util.Comparator;

public class TagObject {
    private String group;
    private String name;

    public TagObject(String group, String name) {
        setGroup(group);
        setName(name);
    }

    public boolean isEmpty() {
        return group.isEmpty() || name.isEmpty();
    }

    public String getGroup() {
        return group;
    }
    public String getName() {
        return name;
    }
    public String getGroupAndName() {
        return group + " - " + name;
    }

    public static Comparator getComparator() {
        return Comparator.comparing(TagObject::getGroupAndName);
    }

    public void setValue(String group, String name) {
        this.setGroup(group);
        this.setName(name);
    }
    public void setGroup(String group) {
        this.group = WordUtils.capitalizeFully(group);
    }
    public void setName(String name) {
        this.name = WordUtils.capitalizeFully(name);
    }
}
