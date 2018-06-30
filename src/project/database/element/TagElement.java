package project.database.element;

import org.apache.commons.text.WordUtils;

public class TagElement {
    /* vars */
    private String group;
    private String name;

    /* constructors */
    public TagElement(String group, String name) {
        setGroup(group);
        setName(name);
    }

    /* boolean */
    public boolean isEmpty() {
        return group.isEmpty() || name.isEmpty();
    }

    /* get */
    public String getGroup() {
        return group;
    }
    public String getName() {
        return name;
    }
    public String getGroupAndName() {
        return group + " - " + name;
    }

    /* set */
    public void setGroup(String group) {
        this.group = WordUtils.capitalizeFully(group);
    }
    public void setName(String name) {
        this.name = WordUtils.capitalizeFully(name);
    }
}
