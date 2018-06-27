package project.database.part;

import org.apache.commons.text.WordUtils;

public class TagItem {
    /* variables */
    private String group;
    private String name;

    /* getters */
    public String getGroup() {
        return group;
    }
    public String getName() {
        return name;
    }
    public String getGroupAndName() {
        return group + " - " + name;
    }

    /* setters */
    public void setGroup(String group) {
        this.group = WordUtils.capitalizeFully(group);
    }
    public void setName(String name) {
        this.name = WordUtils.capitalizeFully(name);
    }
}
