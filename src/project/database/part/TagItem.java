package project.database.part;

import org.apache.commons.text.WordUtils;

public class TagItem {
    /* variables */
    private String category;
    private String name;

    /* constructors */
    public TagItem(String name, String category) {
        this.name = WordUtils.capitalizeFully(name);
        this.category = WordUtils.capitalizeFully(category);
    }

    public TagItem() {
        this.name = "";
        this.category = "";
    }

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
        this.name = name;
    }
    public void setCategory(String category) {
        this.category = category;
    }
}
