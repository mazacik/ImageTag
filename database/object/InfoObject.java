package database.object;

import org.apache.commons.text.WordUtils;

import java.util.Comparator;

public class InfoObject {
    private String group;
    private String name;

    public InfoObject(String group, String name) {
        setGroup(group);
        setName(name);
    }
    public static Comparator getComparator() {
        return Comparator.comparing(InfoObject::getGroupAndName);
    }
    public boolean isEmpty() {
        return group.isEmpty() || name.isEmpty();
    }
    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = WordUtils.capitalizeFully(group);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = WordUtils.capitalizeFully(name);
    }
    public String getGroupAndName() {
        return group + " - " + name;
    }
    public void setValue(String group, String name) {
        this.setGroup(group);
        this.setName(name);
    }
}
