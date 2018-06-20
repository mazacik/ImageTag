package project.backend;

import org.apache.commons.text.WordUtils;
import project.GUIController;
import project.database.Database;
import project.database.DatabaseItem;
import project.gui.component.left.LeftPaneBack;
import project.gui.component.right.RightPaneBack;
import project.gui.component.right.RightPaneFront;
import project.gui.stage.generic.TextInputWindow;

import java.util.ArrayList;
import java.util.Comparator;

public abstract class Filter {
    /* imports */
    private static final ArrayList<DatabaseItem> databaseItems = Database.getDatabaseItems();
    private static final ArrayList<DatabaseItem> databaseItemsFiltered = Database.getDatabaseItemsFiltered();
    private static final ArrayList<DatabaseItem> databaseItemsSelected = Database.getDatabaseItemsSelected();

    private static final ArrayList<String> databaseTags = Database.getDatabaseTags();
    private static final ArrayList<String> databaseTagsWhitelist = Database.getDatabaseTagsWhitelist();
    private static final ArrayList<String> databaseTagsBlacklist = Database.getDatabaseTagsBlacklist();

    /* public methods */
    public static void addTagToDatabase() {
        String tag = WordUtils.capitalizeFully(new TextInputWindow("New Tag", "Name of the new tag:").getResultValue());
        if (!databaseTags.contains(tag)) {
            databaseTags.add(tag);
            Database.sort();
            LeftPaneBack.getInstance().reloadContent();
        }
    }

    public static void addTagToSelectedItems(String tag) {
        String finalFormat = WordUtils.capitalizeFully(tag);
        if (!finalFormat.isEmpty()) {
            if (!databaseTags.contains(finalFormat)) {
                databaseTags.add(finalFormat);
                databaseTags.sort(Comparator.naturalOrder());
                LeftPaneBack.getInstance().reloadContent();
            }
            for (DatabaseItem databaseItem : databaseItemsSelected)
                if (!databaseItem.getTags().contains(finalFormat))
                    databaseItem.getTags().add(finalFormat);
            RightPaneBack.getInstance().reloadContent();
        }
    }

    public static void removeTagFromDatabase(String tag) {
        for (DatabaseItem databaseItem : databaseItems)
            databaseItem.getTags().remove(tag);
        databaseTags.remove(tag);
        databaseTagsWhitelist.remove(tag);
        databaseTagsBlacklist.remove(tag);
        GUIController.getInstance().reloadComponentData(false);
    }

    public static void removeTagFromSelectedItems() {
        for (String tag : RightPaneFront.getInstance().getListView().getSelectionModel().getSelectedItems()) {
            for (DatabaseItem databaseItem : databaseItemsSelected)
                databaseItem.getTags().remove(tag);
            boolean tagExists = false;
            for (DatabaseItem databaseItem : databaseItems)
                if (databaseItem.getTags().contains(tag)) {
                    tagExists = true;
                    break;
                }
            if (!tagExists) {
                databaseTags.remove(tag);
                databaseTagsWhitelist.remove(tag);
                databaseTagsBlacklist.remove(tag);
            }
        }
        GUIController.getInstance().reloadComponentData(false);
    }

    public static void renameTag(String oldTagName) {
        String newTagName = new TextInputWindow("Tag Rename", "New name:").getResultValue();

        if (!newTagName.isEmpty()) {
            if (databaseTags.contains(oldTagName)) {
                databaseTags.set(databaseTags.indexOf(oldTagName), newTagName);
                if (databaseTagsWhitelist.contains(oldTagName))
                    databaseTagsWhitelist.set(databaseTagsWhitelist.indexOf(oldTagName), newTagName);
                if (databaseTagsBlacklist.contains(oldTagName))
                    databaseTagsBlacklist.set(databaseTagsBlacklist.indexOf(oldTagName), newTagName);
                for (DatabaseItem databaseItem : databaseItems)
                    if (databaseItem.getTags().contains(oldTagName))
                        databaseItem.getTags().set(databaseItem.getTags().indexOf(oldTagName), newTagName);
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
                    for (String tag : databaseTagsBlacklist)
                        if (databaseItem.getTags().contains(tag)) {
                            isWhitelisted = false;
                            break;
                        }
                    if (isWhitelisted) databaseItemsFiltered.add(databaseItem);
                }
    }

    public static ArrayList<String> getIntersectingTagsOfSelectedItems() {
        if (databaseItemsSelected.isEmpty()) return new ArrayList<>();

        ArrayList<String> sharedTags = new ArrayList<>();
        ArrayList<String> firstItemTags = databaseItemsSelected.get(0).getTags();
        DatabaseItem lastItemInSelection = databaseItemsSelected.get(databaseItemsSelected.size() - 1);

        for (String tag : firstItemTags)
            for (DatabaseItem databaseItem : databaseItemsSelected)
                if (databaseItem.getTags().contains(tag)) {
                    if (databaseItem.equals(lastItemInSelection))
                        sharedTags.add(tag);
                } else break;

        return sharedTags;
    }
}
