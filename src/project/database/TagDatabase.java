package project.database;

import project.database.part.DatabaseItem;
import project.database.part.TagItem;
import project.gui.ChangeNotificationHelper;
import project.gui.GUIController;
import project.gui.GUIStage;
import project.gui.stage.TagEditor;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class TagDatabase {
    /* change listeners */
    private static final ArrayList<ChangeNotificationHelper> changeListeners = new ArrayList<>();

    /* variables */
    private static final ArrayList<TagItem> databaseTags = new ArrayList<>();
    private static final ArrayList<TagItem> databaseTagsWhitelist = new ArrayList<>();
    private static final ArrayList<TagItem> databaseTagsBlacklist = new ArrayList<>();

    /* imports */
    private static final ArrayList<DatabaseItem> databaseItems = ItemDatabase.getDatabaseItems();
    private static final ArrayList<DatabaseItem> databaseItemsFiltered = ItemDatabase.getDatabaseItemsFiltered();
    private static final ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();

    /* public methods */
    public static void addTag(TagItem tagItem) {
        if (tagItem != null && !contains(tagItem)) {
            databaseTags.add(tagItem);
            sort();
            GUIController.requestReload(GUIStage.getLeftPane());
        }
    }

    public static void removeTag(TagItem tagItem) {
        for (DatabaseItem databaseItem : databaseItems) {
            databaseItem.getTags().remove(tagItem);
        }
        databaseTags.remove(tagItem);
        databaseTagsWhitelist.remove(tagItem);
        databaseTagsBlacklist.remove(tagItem);
        GUIController.requestReload();
    }

    public static void editTag(TagItem tagItem) {
        if (tagItem != null) {
            new TagEditor(tagItem);
            GUIController.requestReload(GUIStage.getLeftPane(), GUIStage.getRightPane());
        }
    }

    public static void createTag() {
        TagItem newTagItem = new TagItem();
        new TagEditor(newTagItem);
        addTag(newTagItem);
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

    public static void addTagToItemSelection(TagItem tagItem) {
        if (tagItem != null) {
            if (!contains(tagItem)) {
                addTag(tagItem);
                sort();
                GUIController.requestReload(GUIStage.getLeftPane());
            }
            for (DatabaseItem databaseItem : databaseItemsSelected)
                if (!databaseItem.getTags().contains(tagItem)) {
                    databaseItem.getTags().add(tagItem);
                }
            GUIController.requestReload(GUIStage.getRightPane());
        }
    }

    public static void removeSelectedTagsFromItemSelection() {
        ArrayList<TagItem> tagsToRemove = new ArrayList<>();
        for (String selectedTag : GUIStage.getRightPane().getListView().getSelectionModel().getSelectedItems()) {
            tagsToRemove.add(getTagItem(selectedTag));
        }

        for (TagItem tagItem : tagsToRemove) {
            for (DatabaseItem databaseItem : databaseItemsSelected) {
                databaseItem.getTags().remove(tagItem);
            }

            boolean tagExists = false;
            for (DatabaseItem databaseItem : databaseItems) {
                if (databaseItem.getTags().contains(tagItem)) {
                    tagExists = true;
                    break;
                }
            }
            if (!tagExists) {
                databaseTags.remove(tagItem);
                databaseTagsWhitelist.remove(tagItem);
                databaseTagsBlacklist.remove(tagItem);
            }
        }
        GUIController.requestReload();
    }

    public static void applyFilters() {
        databaseItemsFiltered.clear();
        if (databaseTagsWhitelist.isEmpty() && databaseTagsBlacklist.isEmpty()) {
            databaseItemsFiltered.addAll(databaseItems);
        } else {
            for (DatabaseItem databaseItem : databaseItems) {
                if (databaseItem.getTags().containsAll(databaseTagsWhitelist)) {
                    boolean isWhitelisted = true;
                    for (TagItem tagItem : databaseTagsBlacklist) {
                        if (databaseItem.getTags().contains(tagItem)) {
                            isWhitelisted = false;
                            break;
                        }
                    }
                    if (isWhitelisted) {
                        databaseItemsFiltered.add(databaseItem);
                    }
                }
            }
        }
        GUIController.requestReload(GUIStage.getGalleryPane());
    }

    public static TagItem getTagItem(String category, String name) {
        for (TagItem tagItem : databaseTags)
            if (tagItem.getName().equals(name) && tagItem.getCategory().equals(category))
                return tagItem;
        return null;
    }

    public static TagItem getTagItem(String categoryAndName) {
        for (TagItem tagItem : databaseTags)
            if (tagItem.getCategory().equals(categoryAndName.split("-")[0].trim()) && tagItem.getName().equals(categoryAndName.split("-")[1].trim()))
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
    public static ArrayList<ChangeNotificationHelper> getChangeListeners() {
        return changeListeners;
    }
}
