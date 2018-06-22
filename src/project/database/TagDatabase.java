package project.database;

import project.database.part.TagItem;
import project.gui.GUIController;
import project.gui.GUIStage;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class TagDatabase {
    /* variables */
    private static final ArrayList<TagItem> databaseTags = new ArrayList<>();
    private static final ArrayList<TagItem> databaseTagsWhitelist = new ArrayList<>();
    private static final ArrayList<TagItem> databaseTagsBlacklist = new ArrayList<>();

    /* public methods */
    public static void addTag(TagItem tagItem) {
        databaseTags.add(tagItem);
        GUIController.requestReload(GUIStage.getLeftPane());
    }

    public static void addTag(String name, String category) {
        addTag(new TagItem(name, category));
    }

    public static void sort() {
        databaseTags.sort(Comparator.comparing(TagItem::getCategoryAndName));
        databaseTagsWhitelist.sort(Comparator.comparing(TagItem::getCategoryAndName));
        databaseTagsBlacklist.sort(Comparator.comparing(TagItem::getCategoryAndName));
    }

    public static boolean contains(TagItem item) {
        for (TagItem tagItem : databaseTags)
            if (tagItem.getName().equals(item.getName()) && tagItem.getCategory().equals(item.getCategory()))
                return true;
        return false;
    }

    public static TagItem getTagItem(String category, String name) {
        for (TagItem tagItem : databaseTags)
            if (tagItem.getName().equals(name) && tagItem.getCategory().equals(category))
                return tagItem;
        return null;
    }

    public static TagItem getTagItem(String name) {
        for (TagItem tagItem : databaseTags)
            if (tagItem.getName().equals(name))
                return tagItem;
        return null;
    }

    public static ArrayList<String> getCategories() {
        ArrayList<String> categories = new ArrayList<>();
        for (TagItem tagItem : databaseTags) {
            categories.add(tagItem.getCategory());
        }
        return categories;
    }

    public static ArrayList<String> getItemsInCategory(String category) {
        ArrayList<String> items = new ArrayList<>();
        for (TagItem tagItem : databaseTags) {
            if (tagItem.getCategory().equals(category)) {
                if (!items.contains(tagItem.getName())) {
                    items.add(tagItem.getName());
                }
            }
        }
        return items;
    }

    /* getters */
    public static ArrayList<TagItem> getDatabaseTags() {
        return databaseTags;
    }
    public static ArrayList<TagItem> getDatabaseTagsWhitelist() {
        return databaseTagsWhitelist;
    }
    public static ArrayList<TagItem> getDatabaseTagsBlacklist() {
        return databaseTagsBlacklist;
    }
}
