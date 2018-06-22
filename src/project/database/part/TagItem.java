package project.database.part;

import org.apache.commons.text.WordUtils;

public class TagItem {
    /* variables */
    private String category;
    private String name;

    /* getters */
    public String getName() {
        return name;
    }
    public String getCategory() {
        return category;
    }
    public String getCategoryAndName() {
        return category + " - " + name;
    }

    /* setters */
    public void setName(String name) {
        this.name = WordUtils.capitalizeFully(name);
    }
    public void setCategory(String category) {
        this.category = WordUtils.capitalizeFully(category);
    }
}
