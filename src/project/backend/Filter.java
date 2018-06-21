package project.backend;

import project.database.ItemDatabase;
import project.database.TagDatabase;
import project.database.part.DatabaseItem;
import project.database.part.TagItem;
import project.gui.GUIStage;
import project.gui.component.left.LeftPaneBack;
import project.gui.component.right.RightPaneBack;
import project.gui.stage.generic.TextInputWindow;

import java.util.ArrayList;

public abstract class Filter {
    private static final ArrayList<DatabaseItem> databaseItems = ItemDatabase.getDatabaseItems();
    private static final ArrayList<DatabaseItem> databaseItemsFiltered = ItemDatabase.getDatabaseItemsFiltered();
    private static final ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();

    private static final ArrayList<TagItem> databaseTags = TagDatabase.getDatabaseTags();
    private static final ArrayList<TagItem> databaseTagsWhitelist = TagDatabase.getDatabaseTagsWhitelist();
    private static final ArrayList<TagItem> databaseTagsBlacklist = TagDatabase.getDatabaseTagsBlacklist();

    public static void addTagToDatabase() {
        String tagName = new TextInputWindow("New Tag", "Name of the new tag:").getResultValue();
        String tagCategory = ""; //todo: fixme

        TagItem tagItem = new TagItem(tagName, tagCategory);
        if (!databaseTags.contains(tagItem)) {
            databaseTags.add(tagItem);
            ItemDatabase.sort();
            LeftPaneBack.getInstance().reloadContent();
        }
    }

    public static void addTagToSelectedItems(TagItem tagItem) {
        if (tagItem != null) {
            if (!databaseTags.contains(tagItem)) {
                databaseTags.add(tagItem);
                //databaseTags.sort(Comparator.naturalOrder()); //todo: fixme
                LeftPaneBack.getInstance().reloadContent();
            }
            for (DatabaseItem databaseItem : databaseItemsSelected)
                if (!databaseItem.getTags().contains(tagItem))
                    databaseItem.getTags().add(tagItem);
            RightPaneBack.getInstance().reloadContent();
        }
    }

    public static void removeTagFromDatabase(TagItem tagItem) {
        for (DatabaseItem databaseItem : databaseItems)
            databaseItem.getTags().remove(tagItem);
        databaseTags.remove(tagItem);
        databaseTagsWhitelist.remove(tagItem);
        databaseTagsBlacklist.remove(tagItem);
        GUIStage.refresh(false);
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
        GUIStage.refresh(false);
    }

    public static void renameTag(TagItem tagItem) {
        if (tagItem != null) {
            String newTagName = new TextInputWindow("Tag Rename", "New name:").getResultValue(); //todo: add category, change to "edit tag"

            if (!newTagName.isEmpty()) {
                tagItem.setName(newTagName);

                LeftPaneBack.getInstance().reloadContent();
                RightPaneBack.getInstance().reloadContent();
            }
        }
    }

    public static void applyTagFilters() {
        databaseItemsFiltered.clear();
        if (databaseTagsWhitelist.isEmpty() && databaseTagsBlacklist.isEmpty())
            databaseItemsFiltered.addAll(databaseItems);
        else
            for (DatabaseItem databaseItem : databaseItems)
                if (databaseItem.getTags().containsAll(databaseTagsWhitelist)) {
                    boolean isWhitelisted = true;
                    for (TagItem tagItem : databaseTagsBlacklist)
                        if (databaseItem.getTags().contains(tagItem)) {
                            isWhitelisted = false;
                            break;
                        }
                    if (isWhitelisted) databaseItemsFiltered.add(databaseItem);
                }
    }
}
