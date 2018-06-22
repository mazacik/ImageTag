package project.database;

import project.database.part.DatabaseItem;
import project.database.part.TagItem;
import project.gui.ChangeEventControl;
import project.gui.ChangeEventListener;
import project.gui.GUIStage;
import project.gui.component.PaneGallery;
import project.gui.component.part.GalleryTile;

import java.util.ArrayList;
import java.util.Random;

public abstract class Selection {
    /* change listeners */
    private static final ArrayList<ChangeEventListener> changeListeners = new ArrayList<>();

    public static ArrayList<TagItem> getSharedTags() {
        ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();
        if (databaseItemsSelected.isEmpty()) return new ArrayList<>();

        ArrayList<TagItem> sharedTags = new ArrayList<>();
        ArrayList<TagItem> firstItemTags = databaseItemsSelected.get(0).getTags();
        DatabaseItem lastItemInSelection = databaseItemsSelected.get(databaseItemsSelected.size() - 1);

        for (TagItem tagItem : firstItemTags)
            for (DatabaseItem databaseItem : databaseItemsSelected)
                if (databaseItem.getTags().contains(tagItem)) {
                    if (databaseItem.equals(lastItemInSelection))
                        sharedTags.add(tagItem);
                } else break;

        return sharedTags;
    }

    public static void selectRandomItem() {
        ArrayList<DatabaseItem> databaseItemsFiltered = ItemDatabase.getDatabaseItemsFiltered();
        int databaseItemsFilteredSize = databaseItemsFiltered.size();
        int randomIndex = new Random().nextInt(databaseItemsFilteredSize);
        Selection.set(databaseItemsFiltered.get(randomIndex));
        GUIStage.getPaneGallery().adjustViewportToFocus();
    }

    public static void add(DatabaseItem databaseItem) {
        ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();
        PaneGallery paneGallery = GUIStage.getPaneGallery();
        databaseItemsSelected.add(databaseItem);
        paneGallery.focusTile(databaseItem);
    }

    public static void add(ArrayList<DatabaseItem> databaseItemsToAddToSelection) {
        ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();
        for (DatabaseItem databaseItem : databaseItemsToAddToSelection) {
            if (!databaseItemsSelected.contains(databaseItem)) {
                databaseItemsSelected.add(databaseItem);
                GalleryTile.generateEffect(databaseItem);
            }
        }

        ChangeEventControl.requestReload(GUIStage.getPaneRight());
    }

    public static void set(DatabaseItem databaseItem) {
        clear();
        add(databaseItem);
    }

    public static void remove(DatabaseItem databaseItem) {
        ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();
        PaneGallery paneGallery = GUIStage.getPaneGallery();
        databaseItemsSelected.remove(databaseItem);
        paneGallery.focusTile(databaseItem);
    }

    public static void swap(DatabaseItem databaseItem) {
        ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();
        if (!databaseItemsSelected.contains(databaseItem))
            add(databaseItem);
        else
            remove(databaseItem);
    }

    public static void clear() {
        ArrayList<DatabaseItem> databaseItemsSelected = ItemDatabase.getDatabaseItemsSelected();
        databaseItemsSelected.clear();
        GUIStage.getPaneRight().getListView().getItems().clear();
        for (DatabaseItem databaseItem : ItemDatabase.getDatabaseItems()) {
            GalleryTile.generateEffect(databaseItem);
        }
    }

    /* getters */
    public static ArrayList<ChangeEventListener> getChangeListeners() {
        return changeListeners;
    }
}
