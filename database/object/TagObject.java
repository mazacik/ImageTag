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
    public static Comparator<TagObject> getComparator() {
        return Comparator.comparing(TagObject::getFull);
    }

    public String getGroup() {
        return group;
    }
    public String getName() {
        return name;
    }
    public String getFull() {
        return group + " - " + name;
    }

    public void setGroup(String group) {
        this.group = WordUtils.capitalizeFully(group, '-', '/', ' ');
    }
    public void setName(String name) {
        this.name = WordUtils.capitalizeFully(name, '-', '/', ' ');
    }
    public void setFull(String group, String name) {
        this.setGroup(group);
        this.setName(name);
    }
}
