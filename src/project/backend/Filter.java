package project.backend;

import project.database.ItemDatabase;
import project.database.TagDatabase;
import project.database.part.DatabaseItem;
import project.database.part.TagItem;
import project.gui.ChangeNotificationHelper;
import project.gui.GUIController;
import project.gui.GUIStage;
import project.gui.stage.generic.TextInputWindow;

import java.util.ArrayList;

public abstract class Filter {
    /* change listeners */
    private static final ArrayList<ChangeNotificationHelper> changeListeners = new ArrayList<>();

    /* imports */
    private static final ArrayList<DatabaseItem> databaseItems = ItemDatabase.getDatabaseItems();
    private static final ArrayList<DatabaseItem> databaseItemsFiltered = ItemDatabase.getDatabaseItemsFiltered();
    private static final ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();

    private static final ArrayList<TagItem> databaseTags = TagDatabase.getDatabaseTags();
    private static final ArrayList<TagItem> databaseTagsWhitelist = TagDatabase.getDatabaseTagsWhitelist();
    private static final ArrayList<TagItem> databaseTagsBlacklist = TagDatabase.getDatabaseTagsBlacklist();

    /* public methods */
    public static void addTagToDatabase() {
        //todo custom window
        String tagName = new TextInputWindow("New Tag", "Name of the new tag:").getResultValue();
        String tagCategory = "";

        TagItem tagItem = new TagItem(tagName, tagCategory);
        if (!TagDatabase.contains(tagItem)) {
            TagDatabase.addTag(tagItem);
            TagDatabase.sort();
            GUIController.requestReload(GUIStage.getLeftPane());
        }
    }

    public static void addTagToSelectedItems(TagItem tagItem) {
        if (tagItem != null) {
            if (!TagDatabase.contains(tagItem)) {
                TagDatabase.addTag(tagItem);
                TagDatabase.sort();
                GUIController.requestReload(GUIStage.getLeftPane());
            }
            for (DatabaseItem databaseItem : databaseItemsSelected)
                if (!databaseItem.getTags().contains(tagItem)) {
                    databaseItem.getTags().add(tagItem);
                }
            GUIController.requestReload(GUIStage.getRightPane());
        }
    }

    public static void removeTagFromDatabase(TagItem tagItem) {
        for (DatabaseItem databaseItem : databaseItems) {
            databaseItem.getTags().remove(tagItem);
        }
        databaseTags.remove(tagItem);
        databaseTagsWhitelist.remove(tagItem);
        databaseTagsBlacklist.remove(tagItem);
        GUIController.requestReload();
    }

    public static void removeSelectedTagsFromItemSelection() {
        for (String tagName : GUIStage.getRightPane().getListView().getSelectionModel().getSelectedItems()) {
            TagItem tagItem = TagDatabase.getTagItem(tagName);

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

    public static void editTag(TagItem tagItem) {
        //todo custom window
        if (tagItem != null) {
            String newTagName = new TextInputWindow("Tag Edit", "New name:").getResultValue();

            if (!newTagName.isEmpty()) {
                tagItem.setName(newTagName);
                GUIController.requestReload(GUIStage.getLeftPane(), GUIStage.getRightPane());
            }
        }
    }

    public static void applyTagFilters() {
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

    /* getters */
    public static ArrayList<ChangeNotificationHelper> getChangeListeners() {
        return changeListeners;
    }
}